package org.example.vindmolleprojekt;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class Reading {
    @SerializedName("logged_at")
    public String loggedAt;

    @SerializedName("wind_effect")
    public int windEffect;

    @SerializedName("wind_speed")
    public float windSpeed;

    // Turbines as a map to hold the turbine names and their values
    @SerializedName("data")
    public Data data;

    // The Data class inside Reading
    public static class Data {
        @SerializedName("turbines")
        public Map<String, Integer> turbines;  // Maps turbine name to its value
    }
}
