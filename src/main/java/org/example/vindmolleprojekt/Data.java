package org.example.vindmolleprojekt;

import java.lang.reflect.Array;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Data {

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
                PreparedStatement ps = conn.prepareStatement("MERGE INTO dbo.readings AS target\n" +
                                "USING (SELECT ? AS wind_effect, ? AS wind_speed, ? AS logged_at) AS source\n" +
                                "ON target.logged_at = source.logged_at\n" +
                                "WHEN NOT MATCHED THEN\n" +
                                "    INSERT (wind_effect, wind_speed, logged_at)\n" +
                                "    VALUES (source.wind_effect, source.wind_speed, source.logged_at);\n",

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
                        "MERGE INTO turbine_readings AS target\n" +
                                "USING (SELECT ? AS reading_id, ? AS turbine_name, ? AS output) AS source\n" +
                                "ON target.reading_id = source.reading_id AND target.turbine_name = source.turbine_name\n" +
                                "WHEN NOT MATCHED THEN\n" +
                                "    INSERT (reading_id, turbine_name, output)\n" +
                                "    VALUES (source.reading_id, source.turbine_name, source.output);\n"
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


}
