package org.example.frontend.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.example.models.Product;

public class ProductOutputController {
    @FXML
    private TableView<Product> productTable;
    @FXML
    private TableColumn<Product, String> groupColumn;
    @FXML
    private TableColumn<Product, String> nameColumn;
    @FXML
    private TableColumn<Product, String> producerColumn;
    @FXML
    private TableColumn<Product, Integer> amountColumn;
    @FXML
    private TableColumn<Product, Double> costPerUnitColumn;
    @FXML
    private TableColumn<Product, Double> totalCostColumn;

    private Window owner;

    public void setOwner(Window owner) {
        this.owner = owner;
    }

    @FXML
    public void initialize() {
        groupColumn.setCellValueFactory(new PropertyValueFactory<>("group"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        producerColumn.setCellValueFactory(new PropertyValueFactory<>("producer"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        costPerUnitColumn.setCellValueFactory(new PropertyValueFactory<>("costPerUnit"));
        totalCostColumn.setCellValueFactory(new PropertyValueFactory<>("totalCost"));

        // Populate the table with sample data
        productTable.setItems(getProductData());
    }

    private ObservableList<Product> getProductData() {
        // Replace with actual data fetching logic
        return FXCollections.observableArrayList(
                new Product(1, "Milk", "Milky", "bueubf",5, 12),
                new Product(2, "Ice Cream", "Bear", "8", 15, 120),
                new Product(3, "Cayenne Pepper", "Spic", "13", 5, 65),
                new Product(4, "Dove", "Dove", "2", 2, 4),
                new Product(5, "A4", "White", "100", 1, 100),
                new Product(6, "Skirt", "Zara", "15", 30, 450),
                new Product(7, "Sneakers", "Zara", "10", 50, 500)
        );
    }

    @FXML
    private void handleTotalPricingPerGroup() {
        // Implement the logic for total pricing per group
    }

    @FXML
    private void handleAlphabeticProductList() {
        // Implement the logic for alphabetic product list
    }
}
