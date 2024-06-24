package org.example.frontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Enumeration;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("Runtime Classpath:");
        Enumeration<URL> resources = getClass().getClassLoader().getResources("");
        while (resources.hasMoreElements()) {
            System.out.println(resources.nextElement());
        }

        URL resource = getClass().getClassLoader().getResource("fxml/Main.fxml");
        if (resource == null) {
            System.out.println("Resource not found");
            throw new RuntimeException("FXML file not found");
        } else {
            System.out.println("Resource found: " + resource);
        }

        FXMLLoader loader = new FXMLLoader(resource);
        VBox root = loader.load();
        Scene scene = new Scene(root);

        primaryStage.setTitle("JavaFX Application");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
