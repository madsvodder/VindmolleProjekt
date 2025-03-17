package org.example.vindmolleprojekt;

import java.lang.reflect.Array;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Data {

    private final String CONNECTION_STRING = "jdbc:sqlserver://10.176.111.34;Database=RM_Windmills; User=CSt2023_t_2; Password=CSt2023T2!24; TrustServerCertificate = true";

    private Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(CONNECTION_STRING);
            return conn;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void refreshData(Api api) {
        List<Reading> readings = api.getNewReading();

        try (Connection conn = getConnection()) {
            System.out.println("Connected to the database successfully");
            int readingId = 0;

            // INSERT INTO DATABASE
            for (Reading reading : readings) {

                // Insert reading into the readings table
                PreparedStatement ps = conn.prepareStatement("INSERT INTO dbo.readings (wind_effect, wind_speed, logged_at) VALUES (?, ?, ?)",
                        PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setInt(1, reading.windEffect);
                ps.setDouble(2, reading.windSpeed);
                ps.setString(3, reading.loggedAt);
                ps.executeUpdate();

                // Retrieve the generated reading_id
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    readingId = rs.getInt(1);
                }
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
        for (Map.Entry<String, Integer> turbineEntry : reading.data.turbines.entrySet()) {
            System.out.println("Inserting turbine: " + turbineEntry.getKey());

            try {
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO turbine_readings (reading_id, turbine_name, output) VALUES (?, ?, ?)"
                );

                ps.setInt(1, readingId);
                ps.setString(2, turbineEntry.getKey());
                ps.setInt(3, turbineEntry.getValue());

                ps.executeUpdate();
                ps.close();

                System.out.println("Inserted turbine: " + turbineEntry.getKey());

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
