package com.weatherclient;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.ZonedDateTime;
import java.util.Scanner;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class App {
    private static final String API_KEY = "2b50163e0da601554dc0c6b7c9b984d8"; 

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter city name: ");
        String city = scanner.nextLine();
        scanner.close();

        try {
           
            String encodedCity = URLEncoder.encode(city.trim(), StandardCharsets.UTF_8);
            String urlString = String.format(
                "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric",
                encodedCity, API_KEY);

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

           
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException(" HTTP Error Code: " + conn.getResponseCode());
            }

            
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                json.append(line);
            }
            in.close();

            //  Parse JSON
            JSONObject obj = new JSONObject(json.toString());

            String cityName = obj.getString("name");
            String country = obj.getJSONObject("sys").getString("country");
            double lat = obj.getJSONObject("coord").getDouble("lat");
            double lon = obj.getJSONObject("coord").getDouble("lon");

            double temp = obj.getJSONObject("main").getDouble("temp");
            double feelsLike = obj.getJSONObject("main").getDouble("feels_like");
            int humidity = obj.getJSONObject("main").getInt("humidity");
            int pressure = obj.getJSONObject("main").getInt("pressure");

            double windSpeed = obj.getJSONObject("wind").getDouble("speed");
            String description = obj.getJSONArray("weather").getJSONObject(0).getString("description");

            long timestamp = obj.getLong("dt");
            String localTime = formatUnixTime(timestamp);

            //  Display output
            System.out.println("\n======= Weather Report =======");
            System.out.println(" Location     : " + cityName + ", " + country);
            System.out.println(" Local Time   : " + localTime);
            System.out.println(" Coordinates  : " + lat + ", " + lon);
            System.out.println(" Temperature : " + temp + " °C");
            System.out.println(" Feels Like   : " + feelsLike + " °C");
            System.out.println(" Humidity     : " + humidity + "%");
            System.out.println(" Pressure     : " + pressure + " hPa");
            System.out.println(" Wind Speed   : " + windSpeed + " m/s");
            System.out.println(" Description  : " + description);
            System.out.println("==============================");

        } catch (Exception e) {
            System.out.println(" Error: " + e.getMessage());
        }
    }

    private static String formatUnixTime(long unixSeconds) {
        Instant instant = Instant.ofEpochSecond(unixSeconds);
        ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
        return zdt.format(formatter);
    }
}
