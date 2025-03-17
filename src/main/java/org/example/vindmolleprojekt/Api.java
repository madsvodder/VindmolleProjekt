package org.example.vindmolleprojekt;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class Api {

    Data data = new Data();

    private final String API_URL = "https://vind-og-klima-app.videnomvind.dk/api/stats?location=vindtved";

    public void connect() {

        try {
            HttpClient client = HttpClient.newBuilder().build();

            HttpRequest request = HttpRequest
                    .newBuilder()
                    .uri(new URI(API_URL))
                    .GET()
                    .build();

            HttpResponse<String> response;
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            //System.out.println(response.body());

            // Parse JSON into an object first
            JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();

            Gson gson = new Gson();

            Reading latestReading = gson.fromJson(jsonObject.get("latest_reading"), Reading.class);

            //ArrayList<Reading> readings = gson.fromJson(jsonObject.get("latest_readings"), ArrayList.class);

            //data.setReadings(gson.fromJson(jsonObject.get("latest_readings"), ArrayList.class));

        } catch (IOException | InterruptedException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public List getNewReading() {
        try {
            HttpClient client = HttpClient.newBuilder().build();

            HttpRequest request = HttpRequest
                    .newBuilder()
                    .uri(new URI(API_URL))
                    .GET()
                    .build();

            HttpResponse<String> response;
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            //System.out.println(response.body());
            JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
            Gson gson = new Gson();

            // Use TypeToken to properly deserialize into a List of Reading objects
            Type listType = new TypeToken<ArrayList<Reading>>(){}.getType();
            ArrayList<Reading> readings = gson.fromJson(jsonObject.get("latest_readings"), listType);

            return readings;

        } catch (IOException | InterruptedException | URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }


}
