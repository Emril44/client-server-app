package org.example.frontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.frontend.controllers.MainController;

import java.net.URL;
import java.util.Enumeration;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
        StackPane root = loader.load();

        MainController controller = loader.getController();
        controller.setOwner(primaryStage); // Pass the main window to the controller

        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("Storage App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
