package org.example.frontend.controllers;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import org.example.network.tcp.StoreClientTCP;

import java.io.IOException;
import java.util.function.Consumer;


public class MainController {
    @FXML
    private StackPane stackPane;
    @FXML
    private BorderPane mainPane;
    @FXML
    private VBox welcomeVBox;
    @FXML
    private HBox mainMenuHBox;
    private Window owner;
    private StoreClientTCP clientTCP;
    private ProductSearchController searchController;

    @FXML
    private void initialize() {
        try {
            clientTCP = new StoreClientTCP();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleProductSearch() {
        loadView("/fxml/ProductSearch.fxml", controller -> {
            if(controller instanceof ProductSearchController) {
                searchController = (ProductSearchController) controller;
                searchController.setClient(clientTCP);
            }
        });
    }

    public void setOwner(Window owner) {
        this.owner = owner;
    }

    private void loadView(String fxmlPath, Consumer<Object> controllerConsumer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            Object controller = loader.getController();

            if(controller instanceof ProductSearchController) {
                ((ProductSearchController) controller).setOwner(owner);
                controllerConsumer.accept(controller);
            }

            // Hide main menu components and show the new view
            mainPane.setVisible(false);
            view.setVisible(true);

            Button backButton = new Button("Back");
            backButton.setPadding(new Insets(5));
            backButton.setPrefWidth(70);
            backButton.setPrefHeight(5);
            backButton.setOnAction(e -> handleBack());
            StackPane.setAlignment(backButton, Pos.BOTTOM_CENTER);
            StackPane.setMargin(backButton, new Insets(10, 0, 10, 0));

            StackPane stackPane = new StackPane(view, backButton);
            this.stackPane.getChildren().add(stackPane);
        } catch (IOException e) {
            showAlert("Error", "Could not load view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleBack() {
        mainPane.setVisible(true);
        for (Node node : stackPane.getChildren()) {
            if (node != mainPane) {
                node.setVisible(false);
            }
        }
        stackPane.getChildren().removeIf(node -> node != mainPane);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.showAndWait();
    }
}
