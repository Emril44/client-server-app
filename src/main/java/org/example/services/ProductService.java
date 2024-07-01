package org.example.services;

import org.example.daos.ProductDAO;
import org.example.daos.ProductGroupDAO;
import org.example.models.Product;

import java.sql.SQLException;
import java.util.List;

public class ProductService {
    private final ProductDAO productDAO = new ProductDAO();
    private final ProductGroupDAO groupDAO = new ProductGroupDAO();

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

    public void createGroup(String name, String description) throws SQLException {
        groupDAO.createGroup(name, description);
    }

    public void assignProductToGroup(int productID, int groupID) throws SQLException {
        groupDAO.assignProductToGroup(productID, groupID);
    }

    public List<Product> listProducts(String criteria, String query) throws SQLException {
        return productDAO.listProductsByCriteria(criteria, query);
    }

    public String getGroupName(int groupID) throws SQLException {
        return groupDAO.getGroupName(groupID);
    }
}
