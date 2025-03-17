package org.example.vindmolleprojekt;

import com.google.gson.annotations.SerializedName;

public class Reading {
    @SerializedName("logged_at")
    public String loggedAt;

    @SerializedName("wind_effect")
    public int windEffect;

    @SerializedName("wind_speed")
    public float windSpeed;

    // Turbines
    @SerializedName("wtg01")
    public int wtg01;

    @SerializedName("wtg02")
    public int wtg02;

    @SerializedName("wtg03")
    public int wtg03;

    @SerializedName("wtg04")
    public int wtg04;

    @SerializedName("wtg05")
    public int wtg05;

    @SerializedName("wtg06")
    public int wtg06;
}
