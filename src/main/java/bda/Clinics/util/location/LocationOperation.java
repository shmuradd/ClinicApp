package bda.Clinics.util.location;

import bda.Clinics.dao.model.dto.request.RequestClinicDto;
import bda.Clinics.dao.model.dto.request.RequestDoctorDto;
import bda.Clinics.dao.model.dto.response.ResponseDoctorDto;
import bda.Clinics.dao.model.location.UserLocation;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
@Slf4j
public class LocationOperation {

    private final GeoApiContext geoApiContext;
    private final CoordinatesResponse coordinatesResponse;
    private final LocationRequest locationRequest;

    private static final String GEOCODING_API_URL = "https://maps.googleapis.com/maps/api/geocode/json";
    private static final String API_KEY = "AIzaSyCt-YiA9TJ2hNVuVWbytkAcbqEMga-nGLs"; // Add your Google API key here

    public LocationOperation(CoordinatesResponse coordinatesResponse, LocationRequest locationRequest) {
        this.coordinatesResponse = coordinatesResponse;
        this.locationRequest = locationRequest;
        this.geoApiContext = new GeoApiContext.Builder()
                .apiKey("AIzaSyCt-YiA9TJ2hNVuVWbytkAcbqEMga-nGLs") // Replace with your API key
                .build();
    }

    @Bean
    public CoordinatesResponse getCoordinates(LocationRequest request) {
        String location = request.getLocation();


        if (location == null || location.isEmpty()) {
            log.warn("Location is null or empty. Skipping geocoding.");
            coordinatesResponse.setError("Location cannot be null or empty.");
            return coordinatesResponse;
        }
        try {
            GeocodingResult[] results = GeocodingApi.geocode(geoApiContext, location).await();
            log.info("Geocoding API returned {} results for location {}", results.length, location);

            if (results.length > 0) {
                log.info("Coordinates found: Lat: {}, Lng: {}", results[0].geometry.location.lat, results[0].geometry.location.lng);
                coordinatesResponse.setLatitude(results[0].geometry.location.lat);
                coordinatesResponse.setLongitude(results[0].geometry.location.lng);
                coordinatesResponse.setAddress(results[0].formattedAddress);
            } else {
                log.warn("No results found for location: {}", location);
                coordinatesResponse.setError("No results found for this location.");
            }
        } catch (Exception e) {
            coordinatesResponse.setError("Error retrieving location: " + e.getMessage());
            log.error("Error during geocoding for location {}: {}", location, e.getMessage());
        }

        return coordinatesResponse;
    }

    public String[] extractCoordinatesFromGoogleMapsLink(String googleMapLink) {
        log.info("Parsing Google Maps link: {}", googleMapLink);

        if (googleMapLink == null || !googleMapLink.contains("@")) {
            log.error("Invalid Google Maps link: {}", googleMapLink);
            return null;
        }

        try {
            String[] split = googleMapLink.split("@");
            if (split.length < 2) {
                log.error("Google Maps link does not contain coordinates: {}", googleMapLink);
                return null;
            }

            String[] coordinates = split[1].split(",");
            if (coordinates.length < 2) {
                log.error("Invalid coordinates format in Google Maps link: {}", googleMapLink);
                return null;
            }

            return new String[]{coordinates[0], coordinates[1]};
        } catch (Exception e) {
            log.error("Error parsing Google Maps link: {}", e.getMessage());
            return null;
        }
    }

    public double calculateDistance(double userLat, double userLon, double clinicLat, double clinicLon) {
        final int R = 6371; // Radius of the Earth in kilometers

        // Convert degrees to radians
        double latDistance = Math.toRadians(clinicLat - userLat);
        double lonDistance = Math.toRadians(clinicLon - userLon);

        // Apply the Haversine formula
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(clinicLat)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Distance in kilometers
        double distance = R * c;

        return distance;
    }

    public UserLocation getUserLocation(String location) {
        if (location == null || location.isEmpty()) {
            // Return default Baku coordinates
            UserLocation bakuLocation = new UserLocation();
            bakuLocation.setUserLat(40.4093); // Baku Latitude
            bakuLocation.setUserLon(49.8671); // Baku Longitude
            return bakuLocation;
        }

        // Existing code for geocoding other locations
        RestTemplate restTemplate = new RestTemplate();
        String url = GEOCODING_API_URL + "?address=" + location + "&key=" + API_KEY;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            JSONObject jsonResponse = new JSONObject(response.getBody());
            if (!jsonResponse.getJSONArray("results").isEmpty()) {
                JSONObject locationData = jsonResponse.getJSONArray("results")
                        .getJSONObject(0)
                        .getJSONObject("geometry")
                        .getJSONObject("location");

                double lat = locationData.getDouble("lat");
                double lon = locationData.getDouble("lng");

                UserLocation userLocation = new UserLocation();
                userLocation.setUserLat(lat);
                userLocation.setUserLon(lon);
                return userLocation;
            }
        }
        throw new RuntimeException("Failed to convert location to coordinates.");
    }


    public void calculateUserLocationToClinicLocation(RequestClinicDto requestClinicDto, UserLocation userLocation) {
        String googleMapsLink = requestClinicDto.getLocation();
        String[] coordinate = extractCoordinatesFromGoogleMapsLink(googleMapsLink);
        if (coordinate != null && coordinate.length == 2) {
            try {
                double clinicLat = Double.parseDouble(coordinate[0]);
                double clinicLon = Double.parseDouble(coordinate[1]);
                double distance = calculateDistance(userLocation.getUserLat(), userLocation.getUserLon(), clinicLat, clinicLon);
                requestClinicDto.setDistance(distance);
            } catch (NumberFormatException e) {
                log.error("Error parsing coordinates for clinic: {}", requestClinicDto.getCity(), e);
            }
        }
    }

    public List<ResponseDoctorDto> doctorSearchForLocationSpecWithinRadius(List<ResponseDoctorDto> doctorDtoList, RequestDoctorDto requestDoctorDto, double radiusInKm) {
        // Geocode the user's location to get their latitude and longitude
        UserLocation userLocation = getUserLocation(requestDoctorDto.getLocation());

        // Filter and sort clinics by distance within the specified radius (e.g., 10 km)
        return doctorDtoList.stream()
                .map(doctorDto -> {
                    Set<RequestClinicDto> doctorClinics = doctorDto.getClinics();

                    // Calculate the distance between user's location and each clinic
                    List<RequestClinicDto> clinicsWithinRadius = doctorClinics.stream()
                            .map(clinic -> {
                                String googleMapsLink = clinic.getLocation(); // Assuming clinic location is stored as Google Maps link
                                String[] clinicCoordinates = extractCoordinatesFromGoogleMapsLink(googleMapsLink);

                                if (clinicCoordinates != null && clinicCoordinates.length == 2) {
                                    try {
                                        double clinicLat = Double.parseDouble(clinicCoordinates[0]);
                                        double clinicLon = Double.parseDouble(clinicCoordinates[1]);
                                        double distance = calculateDistance(userLocation.getUserLat(), userLocation.getUserLon(), clinicLat, clinicLon);
                                        clinic.setDistance(distance); // Set distance to clinic for future use
                                        log.info("Clinic {}: Distance from user: {}", clinic.getClinicName(), clinic.getDistance());
                                    } catch (NumberFormatException e) {
                                        log.error("Error parsing clinic coordinates for clinic: {}", clinic.getCity(), e);
                                    }
                                }
                                return clinic;
                            })
                            // Filter clinics within the given radius
                            .filter(clinic -> clinic.getDistance() <= radiusInKm)
                            // Sort the clinics by distance (from nearest to farthest)
                            .sorted(Comparator.comparingDouble(RequestClinicDto::getDistance))
                            .collect(Collectors.toList());

                    // Update the clinics in the doctor object with those that are within the radius and sorted by distance
                    doctorDto.setClinics(new HashSet<>(clinicsWithinRadius)); // Ensure clinics are ordered properly here

                    return doctorDto;
                })
                // Only return doctors with clinics within the radius
                .filter(doctorDto -> !doctorDto.getClinics().isEmpty())
                .collect(Collectors.toList());
    }
}
