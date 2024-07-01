package org.example.frontend.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Window;
import org.example.models.Product;

public class ProductSearchController {
    @FXML
    private TextField searchField;
    @FXML
    private TableView<Product> searchResultTable;
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

        // Initialize with all products
        searchResultTable.setItems(getProductData());
    }

    private ObservableList<Product> getProductData() {
        // Replace with actual data fetching logic
        return FXCollections.observableArrayList(
                new Product(1, "Milk", "Milky", "bueubf",5, 12, 1),
                new Product(2, "Ice Cream", "Bear", "8", 15, 120, 1),
                new Product(3, "Cayenne Pepper", "Spic", "13", 5, 65, 1),
                new Product(4, "Dove", "Dove", "2", 2, 4, 1),
                new Product(5, "A4", "White", "100", 1, 100, 1),
                new Product(6, "Skirt", "Zara", "15", 30, 450, 1),
                new Product(7, "Sneakers", "Zara", "10", 50, 500, 1)
        );
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().toLowerCase();
        if (query.isEmpty()) {
            searchResultTable.setItems(getProductData());
            return;
        }

        ObservableList<Product> filteredList = FXCollections.observableArrayList();
        for (Product product : getProductData()) {
            if (product.getName().toLowerCase().contains(query) || product.getProducer().toLowerCase().contains(query)) {
                filteredList.add(product);
            }
        }

        searchResultTable.setItems(filteredList);
    }
}
