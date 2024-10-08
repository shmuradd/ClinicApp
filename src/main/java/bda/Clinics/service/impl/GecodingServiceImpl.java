package bda.Clinics.service.impl;

import bda.Clinics.service.GeocodingService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class GecodingServiceImpl implements GeocodingService {
    private static final String GEOCODING_API_URL = "https://maps.googleapis.com/maps/api/geocode/json";

    private static final String API_KEY = "AIzaSyCt-YiA9TJ2hNVuVWbytkAcbqEMga-nGLs";
    @Override
    public double[] getCoordinates(String location) {
        double[] coordinates = new double[2]; // [latitude, longitude]
        String urlString = "https://maps.googleapis.com/maps/api/geocode/json?address=" +
                location.replace(" ", "%20") + "&key=" + API_KEY;

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Parse the JSON response
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray results = jsonResponse.getJSONArray("results");

            if (results.length() > 0) {
                JSONObject locationObj = results.getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                coordinates[0] = locationObj.getDouble("lat"); // Latitude
                coordinates[1] = locationObj.getDouble("lng"); // Longitude
            } else {
                coordinates = null; // No results found
            }

        } catch (Exception e) {
            e.printStackTrace();
            coordinates = null; // In case of an error
        }

        return coordinates;
    }

    // Define the response classes according to your API response structure
    public static class GoogleGeocodingResponse {
        private Result[] results;

        public Result[] getResults() {
            return results;
        }

        public static class Result {
            private Geometry geometry;

            public Geometry getGeometry() {
                return geometry;
            }
        }

        public static class Geometry {
            private Location location;

            public Location getLocation() {
                return location;
            }
        }

        public static class Location {
            private double lat;
            private double lng;

            public double getLat() {
                return lat;
            }

            public double getLng() {
                return lng;
            }
        }
    }
}
