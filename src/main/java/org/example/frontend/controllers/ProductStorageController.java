package org.example.frontend.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.stage.Window;
import org.example.network.tcp.StoreClientTCP;

public class ProductStorageController {
    @FXML
    private ComboBox<String> groupComboBox;

    private Window owner;
    private StoreClientTCP clientTCP;

    public void setClient(StoreClientTCP clientTCP) {
        this.clientTCP = clientTCP;
    }

    @FXML
    private void initialize() {
        // Initialize combo box with some sample data
        groupComboBox.getItems().addAll(
                "Groceries — Water, bread, sugar, etc.",
                "Electronics — TVs, computers, etc.",
                "Furniture — Tables, chairs, etc."
        );
    }

    public void setOwner(Window owner) {
        this.owner = owner;
    }

    @FXML
    private void handleAddNewGroup() {
        showAlert("Add New Group", "Add New Group functionality is not implemented yet.");
    }

    @FXML
    private void handleEditSelectedGroup() {
        showAlert("Edit Selected Group", "Edit Selected Group functionality is not implemented yet.");
    }

    @FXML
    private void handleRemoveSelectedGroup() {
        showAlert("Remove Selected Group", "Remove Selected Group functionality is not implemented yet.");
    }

    @FXML
    private void handleBrowseGroupProducts() {
        showAlert("Browse Group Products", "Browse Group Products functionality is not implemented yet.");
    }

    @FXML
    private void handleListAllProducts() {
        showAlert("List All Products", "List All Products functionality is not implemented yet.");
    }

    @FXML
    private void handleListTotalProductCost() {
        showAlert("List Total Product Cost", "List Total Product Cost functionality is not implemented yet.");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.showAndWait();
    }
}
