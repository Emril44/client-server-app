package org.example.frontend.controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.example.models.Product;
import org.example.network.tcp.StoreClientTCP;
import org.example.services.ProductService;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ProductSearchController {
    @FXML
    public ComboBox<String> criteriaChoiceBox;
    @FXML
    public TableColumn<Product, String> descColumn;
    @FXML
    private TextField searchField;
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
    private TableColumn<Product, Double> priceColumn;
    @FXML
    private TableColumn<Product, Double> totalColumn;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;

    private StoreClientTCP clientTCP;
    private final ProductService productService = new ProductService();

    private Window owner;

    public void setOwner(Window owner) {
        this.owner = owner;
    }

    public void setClient(StoreClientTCP clientTCP) {
        this.clientTCP = clientTCP;
        loadAllProducts();
    }

    @FXML
    public void initialize() {
        productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean isSelected = newSelection != null;
            editButton.setDisable(!isSelected);
            deleteButton.setDisable(!isSelected);
        });

        groupColumn.setCellValueFactory(cellData -> {
                Product product = cellData.getValue();
                try {
                    String groupName = productService.getGroupName(product.getGroupID());
                    return new SimpleStringProperty(groupName);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return new SimpleStringProperty("Unknown");
                }
        });

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        producerColumn.setCellValueFactory(new PropertyValueFactory<>("producer"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        totalColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getAmount() * cellData.getValue().getPrice()));

        criteriaChoiceBox.getItems().addAll("Name", "Description", "Producer", "Amount", "Price", "Group");

        productTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    @FXML
    private void searchProducts() {
        String criteria = criteriaChoiceBox.getValue();
        String query = searchField.getText();
        try {
            List<Product> products = listProductsByCriteria(criteria, query);
            productTable.getItems().setAll(products);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAllProducts() {
        try {
            List<Product> products = listProductsByCriteria("name", " ");
            productTable.getItems().setAll(products);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Product> listProductsByCriteria(String criteria, String query) throws Exception {
        if(criteria.equals("Group")) {
            if(query.isEmpty()) {
                return sendCommunication(criteria, query);
            }
            String idQuery = productService.getGroupID(query).toString();
            String idCriteria = "group_id";
            return sendCommunication(idCriteria, idQuery);
        }

        return sendCommunication(criteria, query);
    }

    private List<Product> sendCommunication(String criteria, String query) throws Exception {
        String response = clientTCP.communicateWithServer("LIST_PRODUCTS_BY_CRITERIA:" + criteria + ":" + query);
        Gson gson = new Gson();
        Type productListType = new TypeToken<List<Product>>(){}.getType();
        return gson.fromJson(response, productListType);
    }

    public void createItem() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CreateWindow.fxml"));
            Parent createView = loader.load();

            CreateController controller = loader.getController();
            controller.setProductService(productService);
            controller.setClientTCP(clientTCP);
            controller.setOnProductCreated(this::loadAllProducts);

            Stage stage = new Stage();
            stage.setTitle("Create Product/Group");
            stage.setScene(new Scene(createView));
            controller.setCreateWindowStage(stage);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void editItem() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if(selectedProduct != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EditWindow.fxml"));
                Parent editView = loader.load();

                EditController controller = loader.getController();
                controller.setProductService(productService);
                controller.setClientTCP(clientTCP);
                controller.setProductToEdit(selectedProduct);
                controller.setOnProductCreated(this::loadAllProducts);

                Stage stage = new Stage();
                stage.setTitle("Create Product/Group");
                stage.setScene(new Scene(editView));
                controller.setEditWindowStage(stage);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteItem() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DeleteWindow.fxml"));
                Parent deleteView = loader.load();

                DeleteController controller = loader.getController();
                controller.setProductService(productService);
                controller.setClientTCP(clientTCP);
                controller.setProductToDelete(selectedProduct);
                controller.setOnItemDeleted(this::loadAllProducts);

                Stage stage = new Stage();
                stage.setTitle("Delete Product");
                stage.setScene(new Scene(deleteView));
                controller.setDeleteWinStage(stage);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void calculateTotalCost() {
        try {
            String response = clientTCP.communicateWithServer("CALCULATE_TOTAL_COST");
            showAlert("Total Cost of All Products", response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void calculateTotalCostPerGroup() {
        try {
            String response = clientTCP.communicateWithServer("CALCULATE_TOTAL_COST_PER_GROUP");
            Gson gson = new Gson();
            Type mapType = new TypeToken<Map<String, Double>>() {}.getType();
            Map<String, Double> totalCostPerGroup = gson.fromJson(response, mapType);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TotalCostPerGroup.fxml"));
            Parent totalCostView = loader.load();

            TotalCostPerGroupController controller = loader.getController();

            controller.populateTable(totalCostPerGroup);

            Stage stage = new Stage();
            stage.setTitle("Total Cost per Group");
            stage.setScene(new Scene(totalCostView));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
