package org.example.frontend.controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Window;
import org.example.models.Product;
import org.example.network.tcp.StoreClientTCP;
import org.example.services.ProductService;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.List;

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

    private StoreClientTCP clientTCP;
    private ProductService productService = new ProductService();

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
}
