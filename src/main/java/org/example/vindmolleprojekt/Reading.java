package org.example.vindmolleprojekt;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class Reading {

    // API
    @SerializedName("daily_wind_total")
    public float dailyWindTotal;

    @SerializedName("date")
    public String date;

    @SerializedName("logged_at")
    public String loggedAt;

    @SerializedName("wind_effect")
    public int windEffect;

    @SerializedName("wind_speed")
    public float windSpeed;

    @SerializedName("data")
    public Data data;

    // Setters & Getters
    public float getDailyWindTotal() {
        return dailyWindTotal;
    }
    public void setDailyWindTotal(float dailyWindTotal) {
        this.dailyWindTotal = dailyWindTotal;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getLoggedAt() {
        return loggedAt;
    }
    public void setLoggedAt(String loggedAt) {
        this.loggedAt = loggedAt;
    }
    public int getWindEffect() {
        return windEffect;
    }
    public void setWindEffect(int windEffect) {
        this.windEffect = windEffect;
    }
    public float getWindSpeed() {
        return windSpeed;
    }
    public void setWindSpeed(float windSpeed) {
        this.windSpeed = windSpeed;
    }
    public Data getData() {
        return data;
    }
    public String getFormattedLoggedAt() {
        return loggedAt.trim().replace("T", " ").substring(0, loggedAt.length() - 8);
    }

    // Data class for turbines
    public static class Data {
        @SerializedName("turbines")
        public Map<String, Integer> turbines;
        public Map<String, Integer> getTurbines() {
            return turbines;
        }
    }
}
