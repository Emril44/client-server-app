package org.example.frontend.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.models.Product;
import org.example.network.tcp.StoreClientTCP;
import org.example.services.ProductService;

public class CreateController {
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

    private ProductService productService;
    private Stage createWindowStage;
    private Runnable onProductCreated;
    private StoreClientTCP clientTCP;

    public void setClientTCP(StoreClientTCP clientTCP) {
        this.clientTCP = clientTCP;
    }

    public void setCreateWindowStage(Stage createWindowStage) {
        this.createWindowStage = createWindowStage;
    }

    public void setProductService(ProductService service) {
        this.productService = service;
        loadGroupNames();
    }

    public void setOnProductCreated(Runnable onProductCreated) {
        this.onProductCreated = onProductCreated;
    }

    public void loadGroupNames() {
        try {
            productGroupChoiceBox.setItems(FXCollections.observableArrayList(productService.getAllGroupNames()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCreateProduct() {
        try {
            String name = productNameField.getText();
            String description = productDescriptionField.getText();
            String producer = productProducerField.getText();
            int amount = Integer.parseInt(productAmountField.getText());
            double price = Double.parseDouble(productPriceField.getText());
            int groupId = productService.getGroupID(productGroupChoiceBox.getValue());

            String response = clientTCP.communicateWithServer("CREATE_PRODUCT:" + name + ":" + description + ":" + producer + ":" + amount + ":" + price + ":" + groupId);

            if(response.equals("Product created successfully.")) {
                if(onProductCreated != null) {
                    onProductCreated.run();
                }
                showAlert(Alert.AlertType.INFORMATION, "Product Created", "Product created successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Error creating product.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Error creating product.");
        }
    }

    @FXML
    private void handleCreateGroup() {
        try {
            String name = groupNameField.getText();
            String description = groupDescriptionField.getText();

            String response = clientTCP.communicateWithServer("CREATE_GROUP:" + name + ":" + description);

            if(response.equals("Group added: " + name)) {
                if(onProductCreated != null) {
                    onProductCreated.run();
                }
                showAlert(Alert.AlertType.INFORMATION, "Group Created", "Group created successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Error creating group1.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Error creating group2.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait().ifPresent(response -> {
            if(alertType == Alert.AlertType.INFORMATION) {
                createWindowStage.close();
            }
        });
    }
}
