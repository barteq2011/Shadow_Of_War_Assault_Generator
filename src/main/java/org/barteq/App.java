package org.barteq;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.barteq.alerting.Alerts;

import java.io.IOException;
import java.net.URISyntaxException;


public class App extends Application {
    // Controller instance used for handle draggable window by menubar
    MainWindowController controller;
    // Offsets used for create draggable window
    private double XOffset, YOffset;
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("mainWindow.fxml"));
        Parent root = loader.load();
        // Get controller from main file
        controller = loader.getController();
        // Menu bar used for create draggable window
        MenuBar menuBar = controller.getMenuBar();
        menuBar.setOnMouseDragged(e -> {
            primaryStage.setX(e.getScreenX() - XOffset);
            primaryStage.setY(e.getScreenY() - YOffset);
        });
        menuBar.setOnMousePressed(e -> {
            XOffset = e.getSceneX();
            YOffset = e.getSceneY();
        });
        try {
            primaryStage.getIcons().add(new Image(getClass().getResource("styles/img/icon.png").toURI().toString()));
        } catch (URISyntaxException e) {
            Alerts.showError("Some of content is not loaded.");
        }
        // Make sure that progress will be saved when exiting program
        primaryStage.setOnCloseRequest(e -> controller.handleExitMenuItem());
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Middle Earth: Shadow Of War Random Assault Generator");
        primaryStage.setResizable(false);
        primaryStage.setOpacity(0.9);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}