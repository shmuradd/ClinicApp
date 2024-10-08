package bda.Clinics.util.location;

import bda.Clinics.dao.model.dto.request.RequestClinicDto;
import bda.Clinics.dao.model.dto.request.RequestDoctorDto;
import bda.Clinics.dao.model.dto.response.ResponseDoctorDto;
import bda.Clinics.dao.model.location.UserLocation;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@Slf4j
public class LocationOperation {

    private final GeoApiContext geoApiContext;
    private final CoordinatesResponse coordinatesResponse;
    private final LocationRequest locationRequest;


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

        try {
            GeocodingResult[] results = GeocodingApi.geocode(geoApiContext, location).await();
            if (results.length > 0) {
                coordinatesResponse.setLatitude(results[0].geometry.location.lat);
                coordinatesResponse.setLongitude(results[0].geometry.location.lng);
                coordinatesResponse.setAddress(results[0].formattedAddress);
            } else {
                coordinatesResponse.setError("No results found for this location.");
            }
        } catch (Exception e) {
            coordinatesResponse.setError("Error retrieving location: " + e.getMessage());
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

    public UserLocation getUserLocation(String requestLocation) {
        UserLocation userLocation = new UserLocation();
        locationRequest.setLocation(requestLocation);
        CoordinatesResponse coordinates = getCoordinates(locationRequest);

        double userLat = coordinates != null ? coordinates.getLatitude() : 0;
        double userLon = coordinates != null ? coordinates.getLongitude() : 0;


        if (coordinates == null || (userLat == 0 && userLon == 0)) {
            log.warn("Using default coordinates for Baku.");
            userLat = 40.40982397041654;
            userLon = 49.87154756154538;
        }
        userLocation.setUserLat(userLat);
        userLocation.setUserLon(userLon);
        return userLocation;
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

    public List<ResponseDoctorDto> doctorSearchForLocationSpec(List<ResponseDoctorDto> doctorDtoList, RequestDoctorDto requestDoctorDto) {
        doctorDtoList.stream().map(doctorDto -> {
            Set<RequestClinicDto> doctorDtoClinics = doctorDto.getClinics();
            UserLocation userLocation = getUserLocation(requestDoctorDto.getLocation());
            for (RequestClinicDto requestClinicDto : doctorDtoClinics) {
                calculateUserLocationToClinicLocation(requestClinicDto, userLocation);
            }
            List<RequestClinicDto> clinics = new ArrayList<>(doctorDtoClinics);
            clinics.sort(Comparator.comparingDouble(RequestClinicDto::getDistance));
            doctorDto.setClinics(doctorDtoClinics);
            return doctorDto;
        }).collect(Collectors.toList());
        return doctorDtoList;
    }
}
