package org.example.frontend.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Map;

public class TotalCostPerGroupController {
    @FXML
    private TableView<GroupCost> totalCostTable;
    @FXML
    private TableColumn<GroupCost, String> groupNameColumn;
    @FXML
    private TableColumn<GroupCost, Double> totalCostColumn;

    @FXML
    public void initialize() {
        groupNameColumn.setCellValueFactory(new PropertyValueFactory<>("groupName"));
        totalCostColumn.setCellValueFactory(new PropertyValueFactory<>("totalCost"));
    }

    public void populateTable(Map<String, Double> totalCostPerGroup) {
        ObservableList<GroupCost> data = FXCollections.observableArrayList();
        totalCostPerGroup.forEach((groupName, totalCost) -> data.add(new GroupCost(groupName, totalCost)));
        totalCostTable.setItems(data);
        totalCostTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public static class GroupCost {
        private final String groupName;
        private final Double totalCost;

        public GroupCost(String groupName, Double totalCost) {
            this.groupName = groupName;
            this.totalCost = totalCost;
        }

        public String getGroupName() {
            return groupName;
        }

        public Double getTotalCost() {
            return totalCost;
        }
    }
}
