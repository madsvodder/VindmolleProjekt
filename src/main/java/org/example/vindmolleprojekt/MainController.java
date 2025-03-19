package org.example.vindmolleprojekt;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainController {

    Api api = new Api();

    Data data = new Data();

    // First Hbox for gauges
    @FXML
    private HBox hbox1;

    @FXML
    private VBox vbox_windSpeed;

    @FXML
    private VBox vbox_windEffect;

    @FXML
    private ProgressIndicator progress_indicator;

    @FXML
    private Label label_loggedat;

    private boolean isInitialized = false;

    Gauge windSpeedGauge;

    Gauge windEffectGauge;

    Timer myTimer = new Timer();

    // Creating the x and y axes for the chart
    CategoryAxis xAxis = new CategoryAxis();
    NumberAxis yAxis = new NumberAxis();
    // Creating the line chart with x and y axes
    LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
    // Prepare the data for the chart
    XYChart.Series<String, Number> series = new XYChart.Series<>();

    public void initialize() {
        // Run timer every minute
        myTimer.scheduleAtFixedRate(myTimerTask , 1000l, 11 * (60*1000));
        setupGauges();
        setupLinechart();
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
                    data.insertReading(api);
                    data.insertMonthReading(api);

                    return null;
                }

                @Override
                protected void succeeded() {
                    // UI updates must be done on the FX Application thread
                    Platform.runLater(() -> {
                        progress_indicator.setVisible(false);
                        Reading reading = data.getLatestReading();
                        updateGauges(reading.windSpeed, reading.windEffect);
                        updateLineChart();
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


    public void setupGauges() {

        windSpeedGauge = GaugeBuilder.create()
                .skinType(Gauge.SkinType.MODERN)
                .prefSize(175,175)
                .title("Wind Speed")
                .unit("m/s")
                .minValue(0)
                .maxValue(35)
                .value(0)
                .animated(true)
                .build();

        windEffectGauge = GaugeBuilder.create()
                .skinType(Gauge.SkinType.MODERN)
                .prefSize(175,175)
                .title("Wind Effect")
                .unit("KW")
                .minorTickSpace(100)
                .minValue(-500)
                .maxValue(500)
                .value(0)
                .animated(true)
                .build();

            vbox_windSpeed.getChildren().add(windSpeedGauge);
            vbox_windEffect.getChildren().add(windEffectGauge);
    }

    private void updateGauges(float windSpeed, int windEffect) {
        windSpeedGauge.setValue(windSpeed);
        windEffectGauge.setValue(windEffect);
    }

    private void setupLinechart() {
        lineChart.setAnimated(false);
        yAxis.setLabel("KW Produced");
        xAxis.setLabel("Day");
        series.setName("KW Produced");
        lineChart.setTitle("Windmill KW Production");
        hbox1.getChildren().add(lineChart);
    }

    private void updateLineChart() {

        List<Reading> latestReadings = data.getAllReadings();

        int counter = 0;

        for (Reading reading : latestReadings) {
            if (counter % 10 == 0) {
                series.getData().add(new XYChart.Data<>(reading.getFormattedLoggedAt(), reading.windEffect)); // Day 1, 150 KW
                System.out.println(reading.getFormattedLoggedAt());
            }
            counter++;
        }

        // Add the series to the chart
        lineChart.getData().add(series);
    }

}