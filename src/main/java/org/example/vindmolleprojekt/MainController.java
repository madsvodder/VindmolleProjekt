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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainController {

    Api api = new Api();

    Data data = new Data();

    @FXML
    private AnchorPane monthPane;

    @FXML
    private AnchorPane overviewPane;

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

    @FXML
    private HBox hbox_overview;

    @FXML
    private AnchorPane turbinesPane;

    @FXML
    private GridPane turbineGaugesGrid;

    @FXML
    private VBox vbox_lineChartDay;

    Gauge windSpeedGauge;

    Gauge windEffectGauge;

    Gauge turbine1Gauge;
    Gauge turbine2Gauge;
    Gauge turbine3Gauge;
    Gauge turbine4Gauge;
    Gauge turbine5Gauge;
    Gauge turbine6Gauge;

    Timer myTimer = new Timer();

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    // Overview Chart
    CategoryAxis xAxisD = new CategoryAxis();
    NumberAxis yAxisD = new NumberAxis();
    LineChart<String, Number> lineChartDay = new LineChart<>(xAxisD, yAxisD);
    XYChart.Series<String, Number> seriesD = new XYChart.Series<>();

    // Month chart
    CategoryAxis xAxisM = new CategoryAxis();
    NumberAxis yAxisM = new NumberAxis();
    LineChart<String, Number> lineChartMonth = new LineChart<>(xAxisM, yAxisM);
    XYChart.Series<String, Number> seriesM = new XYChart.Series<>();

    public Timer getTimer() {
        return myTimer;
    }
    public Data getData() {
        return data;
    }

    public void initialize() {
        // Run timer every minute
        myTimer.scheduleAtFixedRate(myTimerTask, 1000l, 11 * (60 * 1000));
        setupGauges();
        setupLineCharts();
        progress_indicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
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
                    progress_indicator.setVisible(true);
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
                        Reading turbineReading = data.getTurbineReading();

                        label_loggedat.setText("Logged at: " + reading.getFormattedLoggedAt());

                        updateGauges(reading.getWindSpeed(), reading.getWindEffect());
                        updateTurbineGauges(turbineReading);
                        updateLineChartDay();
                        updateLineChartMonth();
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
                .prefSize(175, 175)
                .title("Wind Speed")
                .unit("m/s")
                .minValue(0)
                .maxValue(35)
                .value(0)
                .animated(true)
                .build();

        windEffectGauge = GaugeBuilder.create()
                .skinType(Gauge.SkinType.MODERN)
                .prefSize(175, 175)
                .title("Wind Effect")
                .unit("KW")
                .minorTickSpace(100)
                .minValue(-500)
                .maxValue(500)
                .value(0)
                .animated(true)
                .build();

        turbine1Gauge = GaugeBuilder.create()
                .skinType(Gauge.SkinType.MODERN)
                .prefSize(175, 175)
                .title("Turbine 1")
                .unit("KW")
                .minorTickSpace(100)
                .minValue(-500)
                .maxValue(500)
                .value(0)
                .animated(true)
                .build();

        turbine2Gauge = GaugeBuilder.create()
                .skinType(Gauge.SkinType.MODERN)
                .prefSize(175, 175)
                .title("Turbine 2")
                .unit("KW")
                .minorTickSpace(100)
                .minValue(-500)
                .maxValue(500)
                .value(0)
                .animated(true)
                .build();

        turbine3Gauge = GaugeBuilder.create()
                .skinType(Gauge.SkinType.MODERN)
                .prefSize(175, 175)
                .title("Turbine 3")
                .unit("KW")
                .minorTickSpace(100)
                .minValue(-500)
                .maxValue(500)
                .value(0)
                .animated(true)
                .build();

        turbine4Gauge = GaugeBuilder.create()
                .skinType(Gauge.SkinType.MODERN)
                .prefSize(175, 175)
                .title("Turbine 4")
                .unit("KW")
                .minorTickSpace(100)
                .minValue(-500)
                .maxValue(500)
                .value(0)
                .animated(true)
                .build();

        turbine5Gauge = GaugeBuilder.create()
                .skinType(Gauge.SkinType.MODERN)
                .prefSize(175, 175)
                .title("Turbine 5")
                .unit("KW")
                .minorTickSpace(100)
                .minValue(-500)
                .maxValue(500)
                .value(0)
                .animated(true)
                .build();

        turbine6Gauge = GaugeBuilder.create()
                .skinType(Gauge.SkinType.MODERN)
                .prefSize(175, 175)
                .title("Turbine 6")
                .unit("KW")
                .minorTickSpace(100)
                .minValue(-500)
                .maxValue(500)
                .value(0)
                .animated(true)
                .build();

        turbineGaugesGrid.add(turbine1Gauge, 0, 0);
        turbineGaugesGrid.add(turbine2Gauge, 1, 0);
        turbineGaugesGrid.add(turbine3Gauge, 2, 0);
        turbineGaugesGrid.add(turbine4Gauge, 0, 1);
        turbineGaugesGrid.add(turbine5Gauge, 1, 1);
        turbineGaugesGrid.add(turbine6Gauge, 2, 1);

        vbox_windSpeed.getChildren().add(windSpeedGauge);
        vbox_windEffect.getChildren().add(windEffectGauge);
    }

    private void updateGauges(float windSpeed, int windEffect) {
        windSpeedGauge.setValue(windSpeed);
        windEffectGauge.setValue(windEffect);
    }

    private void updateTurbineGauges(Reading reading) {
        turbine1Gauge.setValue(reading.getData().getTurbines().get("wtg01"));
        turbine2Gauge.setValue(reading.getData().getTurbines().get("wtg02"));
        turbine3Gauge.setValue(reading.getData().getTurbines().get("wtg03"));
        turbine4Gauge.setValue(reading.getData().getTurbines().get("wtg04"));
        turbine5Gauge.setValue(reading.getData().getTurbines().get("wtg05"));
        turbine6Gauge.setValue(reading.getData().getTurbines().get("wtg06"));
    }

    private void setupLineCharts() {

        // LineChart Day
        lineChartDay.setAnimated(false);
        yAxisD.setLabel("KW Produced");
        xAxisD.setLabel("Day");
        seriesD.setName("KW Produced");
        lineChartDay.setTitle("Windmill KW Production");
        vbox_lineChartDay.getChildren().add(lineChartDay);
        lineChartDay.setPrefWidth(1000);

        // LineChart Month
        lineChartMonth.setAnimated(false);
        lineChartMonth.setPrefWidth(1000);
        yAxisM.setLabel("KW Produces");
        xAxisM.setLabel("Day");
        seriesM.setName("KW Produced");
        lineChartMonth.setTitle("Daily production - Month overview");
        hbox_overview.getChildren().add(lineChartMonth);
    }

    private void updateLineChartDay() {

        lineChartDay.getData().remove(seriesD);
        seriesD.getData().clear();

        List<Reading> latestReadings = data.getAllReadings();

        int counter = 0;

        for (Reading reading : latestReadings) {
            if (counter % 10 == 0) {
                seriesD.getData().add(new XYChart.Data<>(reading.getFormattedLoggedAt(), reading.getWindEffect())); // Day 1, 150 KW
            }
            counter++;
        }

        // Add the seriesD to the chart
        lineChartDay.getData().add(seriesD);
    }

    private void updateLineChartMonth() {

        executorService.submit(() -> {
            try {
                List<Reading> latestReadings = data.getMonthReading(api).get();

                Platform.runLater(() -> {
                    lineChartMonth.getData().remove(seriesM);
                    seriesM.getData().clear();

                    for (Reading reading : latestReadings) {
                        seriesM.getData().add(new XYChart.Data<>(reading.getDate(), reading.getDailyWindTotal()));
                    }

                    lineChartMonth.getData().add(seriesM);
                });

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    private void switchToOverview() {
        overviewPane.setVisible(true);
        monthPane.setVisible(false);
        turbinesPane.setVisible(false);
    }

    @FXML
    private void switchToMonthView() {
        overviewPane.setVisible(false);
        monthPane.setVisible(true);
        turbinesPane.setVisible(false);
    }

    @FXML
    private void switchToTurbinesView() {
        overviewPane.setVisible(false);
        monthPane.setVisible(false);
        turbinesPane.setVisible(true);
    }

}