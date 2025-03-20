package org.example.vindmolleprojekt;

import atlantafx.base.theme.PrimerDark;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {

    MainController mainController;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
        Scene scene = new Scene(fxmlLoader.load());
        mainController = fxmlLoader.getController();
        stage.setTitle("Vindmøller");
        stage.setOnCloseRequest(event -> {
            stopThreads();
            Platform.exit();
            System.exit(0);
        });
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        stopThreads();
        Platform.exit();
        System.exit(0);
    }
    private void stopThreads() {
        mainController.getTimer().cancel();
        mainController.getTimer().purge();
        mainController.getData().getExecutorService().shutdownNow();
    }


    public static void main(String[] args) {
        launch();
    }
}