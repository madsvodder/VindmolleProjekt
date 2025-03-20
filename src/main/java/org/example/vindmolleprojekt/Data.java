package org.example.vindmolleprojekt;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Data {

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    // Connection string
    private final String CONNECTION_STRING = "jdbc:sqlserver://10.176.111.34;Database=RM_Windmills; User=CSt2023_t_2; Password=CSt2023T2!24; TrustServerCertificate = true";

    public ExecutorService getExecutorService() {
        return executorService;
    }

    // Get connection to the database
    private Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(CONNECTION_STRING);
            return conn;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertReading(Api api) {

        // Get new readings from the API in a list
        List<Reading> readings = api.getNewReading();

        // Try to connect
        try (Connection conn = getConnection()) {
            System.out.println("Connected to the database successfully");

            // INSERT INTO DATABASE
            for (Reading reading : readings) {

                // Insert reading into the readings table
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO dbo.readings (wind_effect, wind_speed, logged_at) " +
                                "SELECT ?, ?, ? WHERE NOT EXISTS (" +
                                "SELECT 1 FROM dbo.readings WHERE logged_at = ?)",
                        PreparedStatement.RETURN_GENERATED_KEYS);


                // Set the variables
                ps.setInt(1, reading.getWindEffect());
                ps.setDouble(2, reading.getWindSpeed());
                ps.setString(3, reading.getLoggedAt());
                ps.setString(4, reading.getLoggedAt());

                int rowsAffected = ps.executeUpdate();

                // Check if anything was inserted
                if (rowsAffected > 0) {
                    // If a row was inserted, get the generated id
                    ResultSet rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        int readingId = rs.getInt(1);
                        // Insert turbine row
                        insertTurbineReading(conn, readingId, reading);
                        System.out.println("Inserted reading with ID: " + readingId);
                    }
                    rs.close();
                } else {
                    // No rows inserted
                    System.out.println("No new data added for logged_at: " + reading.getFormattedLoggedAt());
                }

                // Close
                ps.close();

            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void insertMonthReading(Api api) {
        // Get new readings from the API in a list
        List<Reading> readings = api.getNewReadingLastMonth();

        // Try to connect
        try (Connection conn = getConnection()) {
            System.out.println("Connected to the database successfully");

            // INSERT INTO DATABASE
            for (Reading reading : readings) {

                // Insert reading into the readings table
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO dbo.readings_lastmonth (date, daily_wind_total) " +
                                "SELECT ?, ? WHERE NOT EXISTS (" +
                                "SELECT 1 FROM dbo.readings_lastmonth WHERE date = ?)");

                // Set the variables
                ps.setString(1, reading.getDate());
                ps.setFloat(2, reading.getDailyWindTotal());
                ps.setString(3, reading.getDate());
                ps.executeUpdate();

                // Close
                ps.close();

            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Future<List<Reading>> getMonthReading(Api api) {
        return executorService.submit(() -> {
            try (Connection conn = getConnection()) {
                System.out.println("Connected to the database successfully");
                List<Reading> readings = new ArrayList<>();

                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "SELECT * FROM readings_lastmonth ORDER BY date ASC"
                );

                while (rs.next()) {
                    Reading reading = new Reading();
                    reading.setDate(rs.getString("date"));
                    reading.setDailyWindTotal(rs.getFloat("daily_wind_total"));
                    readings.add(reading);
                }

                rs.close();
                stmt.close();
                return readings;

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void insertTurbineReading(Connection conn, int readingId, Reading reading) {

        // For each turbine entry
        for (Map.Entry<String, Integer> turbineEntry : reading.getData().getTurbines().entrySet()) {
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

    public Reading getTurbineReading() {
        Future<Reading> future = executorService.submit(() -> {
        try (Connection conn = getConnection()) {
            System.out.println("Connected to the database successfully");
            Reading reading = new Reading();
            // Initialize data
            reading.data = new Reading.Data();
            // Initialize the map
            reading.data.turbines = new HashMap<>();

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT TOP 6 turbine_name, output FROM turbine_readings ORDER BY reading_id DESC"
            );

            // Use while to read multiple rows
            while (rs.next()) {
                String turbineName = rs.getString("turbine_name");
                int output = rs.getInt("output");

                reading.data.getTurbines().put(turbineName, output);
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

    public Reading getLatestReading() {
        Future<Reading> future = executorService.submit(() -> {
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

    public List<Reading> getAllReadings() {
        Future<List<Reading>> future = executorService.submit(() -> {
        try (Connection conn = getConnection()) {
            System.out.println("Connected to the database successfully");

            List<Reading> readings = new ArrayList<>();

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT * FROM readings ORDER BY logged_at ASC"
            );

            while (rs.next()) {
                Reading reading = new Reading();
                reading.setLoggedAt(rs.getString("logged_at"));
                reading.setWindSpeed(rs.getFloat("wind_speed"));
                reading.setWindEffect(rs.getInt("wind_effect"));
                readings.add(reading);
            }

            rs.close();
            stmt.close();
            return readings;

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
