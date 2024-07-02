package org.example.services;

import org.example.daos.ProductDAO;
import org.example.daos.ProductGroupDAO;
import org.example.models.Product;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ProductService {
    private final ProductGroupDAO groupDAO = new ProductGroupDAO();
    private final ProductDAO productDAO = new ProductDAO(groupDAO);

    public void createProduct(Product product) throws SQLException {
        productDAO.createProduct(product);
    }

    public Product getProduct(int id) throws SQLException {
        return productDAO.getProduct(id);
    }

    public void updateProduct(Product product) throws SQLException {
        productDAO.updateProduct(product);
    }

    public void deleteProduct(int id) throws SQLException {
        productDAO.deleteProduct(id);
    }

    public List<Product> listProducts(String criteria, String query) throws SQLException {
        return productDAO.listProductsByCriteria(criteria, query);
    }

    public double calculateTotalCost() throws SQLException {
        return productDAO.calculateTotalCost();
    }

    public Map<String, Double> calculateTotalCostPerGroup() throws SQLException {
        return productDAO.calculateTotalCostPerGroup();
    }

    public void createGroup(String name, String description) throws SQLException {
        groupDAO.createGroup(name, description);
    }

    public String getGroupName(int groupID) throws SQLException {
        return groupDAO.getGroupName(groupID);
    }

    public Integer getGroupID(String name) throws SQLException {
        return groupDAO.getGroupID(name);
    }

    public List<String> getAllGroupNames() throws SQLException {
        return groupDAO.getAllGroupNames();
    }
}
