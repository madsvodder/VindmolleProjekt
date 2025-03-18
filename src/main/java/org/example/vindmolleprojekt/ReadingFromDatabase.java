package org.example.vindmolleprojekt;

import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;

import java.sql.*;

public class ReadingFromDatabase {

    private String loggedAt;
    private float windSpeed;

    private int windEffect;

    public void setLoggedAt(String loggedAt) {
        this.loggedAt = loggedAt;
    }

    public void setWindSpeed(float windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getLoggedAt() {
        return loggedAt;
    }

    public float getWindSpeed() {
        return windSpeed;
    }

    public int getWindEffect() {
        return windEffect;
    }

    public void setWindEffect(int windEffect) {
        this.windEffect = windEffect;
    }


}
