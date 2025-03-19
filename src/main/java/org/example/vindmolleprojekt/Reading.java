package org.example.vindmolleprojekt;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class Reading {

    public String created_at;

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getCreated_at() {
        return created_at;
    }

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

    // Turbines as a map to hold the turbine names and their values
    @SerializedName("data")
    public Data data;

    // The Data class inside Reading
    public static class Data {
        @SerializedName("turbines")
        public Map<String, Integer> turbines;  // Maps turbine name to its value
    }

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

    public int getWindEffect() {
        return windEffect;
    }
    public float getWindSpeed() {
        return windSpeed;
    }

    public Data getData() {
        return data;
    }

    public void setLoggedAt(String loggedAt) {
        this.loggedAt = loggedAt;
    }

    public void setWindEffect(int windEffect) {
        this.windEffect = windEffect;
    }

    public void setWindSpeed(float windSpeed) {
        this.windSpeed = windSpeed;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getFormattedLoggedAt() {
        String trimmedT = loggedAt.trim().replaceAll("T", " ");
        String trimmed8 = trimmedT.substring(0, trimmedT.length() - 8);
        return trimmed8;
    }
}
