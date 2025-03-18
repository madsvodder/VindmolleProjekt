package org.example.vindmolleprojekt;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;

public class MainController {

    Api api = new Api();

    Data data = new Data();

    ReadingFromDatabase readingFromDatabase = new ReadingFromDatabase();

    // First Hbox for gauges
    @FXML
    private HBox hbox1;

    @FXML
    private ProgressIndicator progress_indicator;

    @FXML
    private Label label_loggedat;

    public void initialize() {
        thread1.start();

    }
    public void setupGauges(float windSpeed, int windEffect) {

        Gauge windSpeedGauge = GaugeBuilder.create()
                .skinType(Gauge.SkinType.QUARTER)
                .title("Wind Speed")
                .unit("km/h")
                .minValue(0)
                .maxValue(50)
                .value(windSpeed)
                .animated(true)
                .build();

        Gauge windEffectGauge = GaugeBuilder.create()
                .skinType(Gauge.SkinType.QUARTER)
                .title("Wind Effect")
                .unit("km/h")
                .minValue(-300)
                .maxValue(300)
                .value(windEffect)
                .animated(true)
                .build();

        hbox1.getChildren().addAll(windSpeedGauge, windEffectGauge);
    }

    Task<Void> task_refreshData = new Task<>() {
        @Override
        protected Void call() throws Exception {
            progress_indicator.setVisible(true);
            progress_indicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
            data.refreshData(api);
            return null;
        }

        @Override
        protected void succeeded() {
            progress_indicator.setVisible(false);
            setupVisuals();
        }

        @Override
        protected void failed() {
            System.out.println(task_refreshData.getException().getMessage());
        }
    };

    Thread thread1 = new Thread(task_refreshData);

    private void setupVisuals() {

        // get latest readings
        ReadingFromDatabase readingFromDatabase = data.getLatestReading();

        label_loggedat.setText("Logged at: " + readingFromDatabase.getLoggedAt());

        setupGauges(readingFromDatabase.getWindSpeed(), readingFromDatabase.getWindEffect());
    }

}