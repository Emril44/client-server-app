package org.example.frontend.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.models.Product;
import org.example.models.ProductGroup;
import org.example.network.tcp.StoreClientTCP;
import org.example.services.ProductService;

import java.sql.SQLException;
import java.util.List;

public class DeleteController {
    @FXML
    private TabPane deleteTabPane;
    @FXML
    private Tab productTab;
    @FXML
    private Tab groupTab;
    @FXML
    private TextField productNameField;
    @FXML
    private TextField productDescriptionField;
    @FXML
    private TextField productProducerField;
    @FXML
    private TextField productAmountField;
    @FXML
    private TextField productPriceField;
    @FXML
    private TextField productGroupField;
    @FXML
    private TextField groupNameField;
    @FXML
    private TextField groupDescriptionField;
    @FXML
    private ListView<String> groupProductsListView;

    private Stage deleteWinStage;
    private ProductService productService;
    private Product productToDelete;
    private ProductGroup groupToDelete;
    private Runnable onItemDeleted;
    private StoreClientTCP clientTCP;

    public void setClientTCP(StoreClientTCP clientTCP) {
        this.clientTCP = clientTCP;
    }

    public void setDeleteWinStage(Stage deleteWinStage) {
        this.deleteWinStage = deleteWinStage;
    }

    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    public void setProductToDelete(Product productToDelete) {
        this.productToDelete = productToDelete;
        populateProductFields();
        populateGroupFields();
    }

    public void setOnItemDeleted(Runnable onItemDeleted) {
        this.onItemDeleted = onItemDeleted;
    }

    private void populateProductFields() {
        productNameField.setText(productToDelete.getName());
        productDescriptionField.setText(productToDelete.getDescription());
        productProducerField.setText(productToDelete.getProducer());
        productAmountField.setText(String.valueOf(productToDelete.getAmount()));
        productPriceField.setText(String.valueOf(productToDelete.getPrice()));
        try {
            productGroupField.setText(productService.getGroupName(productToDelete.getGroupID()));
        } catch (SQLException e) {
            e.printStackTrace();
            productGroupField.setText("Unknown");
        }
    }

    private void populateGroupFields() {
        try {
            int groupId = productToDelete.getGroupID();
            groupToDelete = new ProductGroup(groupId, productService.getGroupName(groupId), productService.getGroupDescription(groupId));
            groupNameField.setText(groupToDelete.getName());
            groupDescriptionField.setText(groupToDelete.getDescription());
            List<Product> productsInGroup = productService.getProductsByGroup(groupId);
            for (Product product : productsInGroup) {
                groupProductsListView.getItems().add(product.getName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleConfirmDelete() {
        try {
            String response = "";

            if (deleteTabPane.getSelectionModel().getSelectedItem() == productTab && productToDelete != null) {
                response = clientTCP.communicateWithServer("DELETE_PRODUCT:" + productToDelete.getId());
            } else if (deleteTabPane.getSelectionModel().getSelectedItem() == groupTab && groupToDelete != null) {
                response = clientTCP.communicateWithServer("DELETE_GROUP:" + groupToDelete.getId());
            }

            if (response.equals("Product deleted successfully.") || response.equals("Group deleted successfully.")) {
                showAlert(Alert.AlertType.INFORMATION, "Delete Successful", "The item was successfully deleted.");
                if (onItemDeleted != null) {
                    onItemDeleted.run();
                }
                deleteWinStage.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Delete Failed", "Failed to delete the item.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Delete Failed", "Failed to delete the item.");
        }
    }

    @FXML
    private void handleCancelDelete() {
        deleteWinStage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
