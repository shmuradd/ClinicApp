package bda.Clinics.util.location;

import bda.Clinics.dao.model.dto.request.RequestClinicDto;
import bda.Clinics.dao.model.dto.request.RequestDoctorDto;
import bda.Clinics.dao.model.dto.response.ResponseDoctorDto;
import bda.Clinics.dao.model.location.UserLocation;
import com.google.maps.GeoApiContext;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@Slf4j
public class LocationOperation {

    private final GeoApiContext geoApiContext;

    private static final String GEOCODING_API_URL = "https://maps.googleapis.com/maps/api/geocode/json";
    private static final String API_KEY = "AIzaSyCt-YiA9TJ2hNVuVWbytkAcbqEMga-nGLs"; // Add your Google API key here


    public LocationOperation() {
        this.geoApiContext = new GeoApiContext.Builder()
                .apiKey("AIzaSyCt-YiA9TJ2hNVuVWbytkAcbqEMga-nGLs") // Replace with your API key
                .build();
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
        RestTemplate restTemplate = new RestTemplate();
        String url = GEOCODING_API_URL + "?address=" + location + "&key=" + API_KEY;
        log.info("Sending request to Geocoding API: {}", url);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Geocoding API response received: {}", response.getBody());

            JSONObject jsonResponse = new JSONObject(response.getBody());
            if (!jsonResponse.getJSONArray("results").isEmpty()) {
                JSONObject locationData = jsonResponse.getJSONArray("results")
                        .getJSONObject(0)
                        .getJSONObject("geometry")
                        .getJSONObject("location");

                double lat = locationData.getDouble("lat");
                double lon = locationData.getDouble("lng");
                log.info("Coordinates extracted: lat = {}, lon = {}", lat, lon);

                UserLocation userLocation = new UserLocation();
                userLocation.setUserLat(lat);
                userLocation.setUserLon(lon);
                return userLocation;
            } else {
                log.error("No results found in Geocoding API response for location: {}", location);
            }
        } else {
            log.error("Geocoding API request failed with status: {}", response.getStatusCode());
        }

        throw new RuntimeException("Failed to convert location to coordinates.");
    }



    public List<ResponseDoctorDto> doctorSearchForLocationSpecWithinRadius(List<ResponseDoctorDto> doctorDtoList, RequestDoctorDto requestDoctorDto, double radiusInKm) {
        UserLocation userLocation = getUserLocation(requestDoctorDto.getLocation());

        return doctorDtoList.stream()
                .map(doctorDto -> {
                    Set<RequestClinicDto> doctorClinics = doctorDto.getClinics();

                    List<RequestClinicDto> clinicsWithinRadius = doctorClinics.stream()
                            .map(clinic -> {
                                String googleMapsLink = clinic.getLocation();
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
                            .filter(clinic -> clinic.getDistance() <= radiusInKm)
                            .sorted(Comparator.comparingDouble(RequestClinicDto::getDistance))
                            .collect(Collectors.toList());
                    doctorDto.setClinics(new HashSet<>(clinicsWithinRadius));

                    return doctorDto;
                })
                .filter(doctorDto -> !doctorDto.getClinics().isEmpty())
                .collect(Collectors.toList());
    }
}