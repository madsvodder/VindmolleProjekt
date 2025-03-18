package org.example.vindmolleprojekt;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;

import java.util.Timer;
import java.util.TimerTask;

public class MainController {

    Api api = new Api();

    Data data = new Data();

    // First Hbox for gauges
    @FXML
    private HBox hbox1;

    @FXML
    private ProgressIndicator progress_indicator;

    @FXML
    private Label label_loggedat;

    private boolean isInitialized = false;

    Gauge windSpeedGauge;

    Gauge windEffectGauge;

    Timer myTimer = new Timer();

    public void initialize() {
        // Run timer every minute
        myTimer.scheduleAtFixedRate(myTimerTask , 1000l, 1 * (40*1000));
    }

    TimerTask myTimerTask = new TimerTask() {
        @Override
        public void run() {
            System.out.println("TimerTask...");

            // Run the background task on a new thread
            Thread newThread = new Thread(new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    // Refresh data in the background
                    data.refreshData(api);
                    return null;
                }

                @Override
                protected void succeeded() {
                    // UI updates must be done on the FX Application thread
                    Platform.runLater(() -> {
                        progress_indicator.setVisible(false);
                        setupVisuals();
                    });
                }

                @Override
                protected void failed() {
                    // Handle task failure
                    Platform.runLater(() -> {
                        System.out.println(getException().getMessage());
                        progress_indicator.setVisible(false);

                        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                        alert.setTitle("Application Error");
                        alert.setHeaderText("An error has occurred");
                        alert.setContentText("The application encountered an error and cannot continue. Please restart the application, and ensure that the database connection is working correctly.");
                        alert.showAndWait();
                    });
                }
            });
            newThread.start();
        }
    };


    public void setupGauges(float windSpeed, int windEffect) {

        windSpeedGauge = GaugeBuilder.create()
                .skinType(Gauge.SkinType.QUARTER)
                .title("Wind Speed")
                .unit("km/h")
                .minValue(0)
                .maxValue(50)
                .value(windSpeed)
                .animated(true)
                .build();

        windEffectGauge = GaugeBuilder.create()
                .skinType(Gauge.SkinType.QUARTER)
                .title("Wind Effect")
                .unit("km/h")
                .minValue(-300)
                .maxValue(300)
                .value(windEffect)
                .animated(true)
                .build();

        hbox1.getChildren().addAll(windSpeedGauge, windEffectGauge);

        isInitialized = true;
    }

    private void updateGauges(float windSpeed, int windEffect) {
        windSpeedGauge.setValue(windSpeed);
        windEffectGauge.setValue(windEffect);
    }


    private void setupVisuals() {

        // get latest readings
        Reading reading = data.getLatestReading();

        label_loggedat.setText("Logged at: " + reading.getLoggedAt());

        if (!isInitialized) {
            System.out.println("Setting up gauges...");
            setupGauges(reading.getWindSpeed(), reading.getWindEffect());
        } else {
            System.out.println("Updating gauges...");
            updateGauges(reading.getWindSpeed(), reading.getWindEffect());
        }

    }

}