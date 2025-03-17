package org.example.vindmolleprojekt;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;

import javax.management.monitor.GaugeMonitor;

public class MainController {

    // First Hbox for gauges
    @FXML
    private HBox hbox1;

    public void initialize() {
        setupGauges();
    }
    public void setupGauges() {

        //Gauge gauge = new Gauge();

        Gauge gauge = GaugeBuilder.create()
                .title("Speedometer")
                .unit("km/h")
                .minValue(0)
                .maxValue(200)
                .value(60)
                .animated(true)
                .build();

        hbox1.getChildren().add(gauge);

        Api api = new Api();

        Data data = new Data();

        data.refreshData(api);
    }

}