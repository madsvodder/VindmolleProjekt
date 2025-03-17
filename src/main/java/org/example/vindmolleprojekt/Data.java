package org.example.vindmolleprojekt;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Data {

    private Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlserver://10.176.111.34;Database=RM_Windmills; User=CSt2023_t_2; Password=CSt2023T2!24; TrustServerCertificate = true");
            return conn;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void refreshData(Api api) {

        List<Reading> readings = api.getNewReading();

        try (Connection conn = getConnection()) {

            System.out.println("Connected to the database successfully");

            // INSERT INTO DATABASE
            for (Reading reading : readings) {
                PreparedStatement ps = conn.prepareStatement(
                        "MERGE INTO dbo.readings AS target " +
                                "USING (SELECT ? AS wind_effect, ? AS wind_speed, ? AS logged_at) AS source " +
                                "ON target.logged_at = source.logged_at " +
                                "WHEN NOT MATCHED BY TARGET THEN " +
                                "INSERT (wind_effect, wind_speed, logged_at) " +
                                "VALUES (source.wind_effect, source.wind_speed, source.logged_at);"
                );
                ps.setInt(1, reading.windEffect);
                ps.setDouble(2, reading.windSpeed);
                ps.setString(3, reading.loggedAt);
                ps.executeUpdate();
            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
