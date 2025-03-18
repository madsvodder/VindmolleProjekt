package org.example.vindmolleprojekt;

import javafx.concurrent.Task;

import java.lang.reflect.Array;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Data {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    // Connection string
    private final String CONNECTION_STRING = "jdbc:sqlserver://10.176.111.34;Database=RM_Windmills; User=CSt2023_t_2; Password=CSt2023T2!24; TrustServerCertificate = true";

    // Get connection to the database
    private Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(CONNECTION_STRING);
            return conn;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void refreshData(Api api) {

        // Get new readings from the API in a list
        List<Reading> readings = api.getNewReading();

        // Try to connect
        try (Connection conn = getConnection()) {
            System.out.println("Connected to the database successfully");
            int readingId = 0;

            // INSERT INTO DATABASE
            for (Reading reading : readings) {

                // Insert reading into the readings table
                PreparedStatement ps = conn.prepareStatement("INSERT INTO dbo.readings (wind_effect, wind_speed, logged_at) VALUES (?, ?, ?)",

                        // Get the key for the turbines table
                        PreparedStatement.RETURN_GENERATED_KEYS);

                // Set the variables
                ps.setInt(1, reading.windEffect);
                ps.setDouble(2, reading.windSpeed);
                ps.setString(3, reading.loggedAt);
                ps.executeUpdate();

                // Retrieve the generated reading_id
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    readingId = rs.getInt(1);
                }

                // Close
                ps.close();
                rs.close();

                if (readingId == -1) {
                    throw new SQLException("Could not get generated key");
                }

                // Insert turbine reading into turbine_readings table
                insertTurbineReading(conn, readingId, reading);

            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void insertTurbineReading(Connection conn, int readingId, Reading reading) {

        // For each turbine entry
        for (Map.Entry<String, Integer> turbineEntry : reading.data.turbines.entrySet()) {
            System.out.println("Inserting turbine: " + turbineEntry.getKey());

            try {
                // Merge turbine data into the turbine table
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO turbine_readings (reading_id, turbine_name, output) VALUES (?, ?, ?)"
                );

                // Set variables
                ps.setInt(1, readingId);
                ps.setString(2, turbineEntry.getKey());
                ps.setInt(3, turbineEntry.getValue());
                ps.executeUpdate();
                ps.close();

                // Debug
                System.out.println("Inserted turbine: " + turbineEntry.getKey());

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public Reading getLatestReading() {
        Future<Reading> future = executor.submit(() -> {
            try (Connection conn = getConnection()) {
                System.out.println("Connected to the database successfully");
                Reading reading = new Reading();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "SELECT TOP 1 logged_at, wind_speed, wind_effect FROM readings ORDER BY logged_at DESC"
                );

                if (rs.next()) {
                    String loggedAt = rs.getString("logged_at");
                    float windSpeed = rs.getFloat("wind_speed");
                    int windEffect = rs.getInt("wind_effect");

                    reading.setLoggedAt(loggedAt);
                    reading.setWindSpeed(windSpeed);
                    reading.setWindEffect(windEffect);
                }
                rs.close();
                stmt.close();
                return reading;

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        try {
            // Waits for the task to complete and retrieves the result
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
