package org.example.vindmolleprojekt;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class WindData {
    @SerializedName("latest_readings")
    public ArrayList<Reading> latestReadings;
}
