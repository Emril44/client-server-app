package org.example.frontend.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.models.Product;
import org.example.models.ProductGroup;
import org.example.network.tcp.StoreClientTCP;
import org.example.services.ProductService;

import java.sql.SQLException;

public class EditController {
    @FXML
    private TabPane editPane;
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
    private ChoiceBox<String> productGroupChoiceBox;
    @FXML
    private TextField groupNameField;
    @FXML
    private TextField groupDescriptionField;
    @FXML
    private Tab productTab;
    @FXML
    private Tab groupTab;
    private ProductService productService;
    private Stage editWindowStage;
    private Runnable onProductEdited;
    private StoreClientTCP clientTCP;
    private Product productToEdit;
    private ProductGroup groupToEdit;

    public void setClientTCP(StoreClientTCP clientTCP) {
        this.clientTCP = clientTCP;
    }

    public void setEditWindowStage(Stage stage) {
        this.editWindowStage = stage;
    }

    public void setProductService(ProductService service) {
        this.productService = service;
    }

    public void setOnProductCreated(Runnable loadAllProducts) {
        this.onProductEdited = loadAllProducts;
    }

    public void setProductToEdit(Product product) {
        this.productToEdit = product;
        populateProductFields();
        populateGroupFields();
    }

    private void populateProductFields() {
        if (productToEdit != null) {
            productNameField.setText(productToEdit.getName());
            productDescriptionField.setText(productToEdit.getDescription());
            productProducerField.setText(productToEdit.getProducer());
            productAmountField.setText(String.valueOf(productToEdit.getAmount()));
            productPriceField.setText(String.valueOf(productToEdit.getPrice()));
            try {
                productGroupChoiceBox.setItems(FXCollections.observableArrayList(productService.getAllGroupNames()));
                productGroupChoiceBox.setValue(productService.getGroupName(productToEdit.getGroupID()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void populateGroupFields() {
        try {
            int groupId = productToEdit.getGroupID();
            groupToEdit = new ProductGroup(groupId, productService.getGroupName(groupId), productService.getGroupDescription(groupId));
            groupNameField.setText(groupToEdit.getName());
            groupDescriptionField.setText(groupToEdit.getDescription());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleConfirmEdit() {
        try {
            String response = "";

            if(editPane.getSelectionModel().getSelectedItem() == productTab && productToEdit != null) {
                String name = productNameField.getText();
                String description = productDescriptionField.getText();
                String producer = productProducerField.getText();
                int amount = Integer.parseInt(productAmountField.getText());
                double price = Double.parseDouble(productPriceField.getText());
                int groupId = productService.getGroupID(productGroupChoiceBox.getValue());

                Product updatedProduct = new Product(productToEdit.getId(), name, description, producer, amount, price, groupId);
                response = clientTCP.communicateWithServer("EDIT_PRODUCT:" + updatedProduct.getId() + ":" + name + ":" + description + ":" + producer + ":" + amount + ":" + price + ":" + groupId);
            } else if(editPane.getSelectionModel().getSelectedItem() == groupTab && groupToEdit != null) {
                String name = groupNameField.getText();
                String description = groupDescriptionField.getText();
                response = clientTCP.communicateWithServer("EDIT_GROUP:" + groupToEdit.getId() + ":" + name + ":" + description);
            }

            if(response.equals("Product edited successfully.") || response.equals("Group edited successfully.")) {
                showAlert(Alert.AlertType.INFORMATION, "Edit Successful", "The item was successfully edited.");
                if (onProductEdited != null) {
                    onProductEdited.run();
                }
                editWindowStage.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Edit Failed", "Failed to edit the item.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Error editing product.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
