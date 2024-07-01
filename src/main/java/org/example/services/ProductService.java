package org.example.services;

import org.example.daos.ProductDAO;
import org.example.models.Product;

import java.sql.SQLException;
import java.util.List;

public class ProductService {
    private final ProductDAO productDAO = new ProductDAO();

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
        productDAO.createGroup(name, description);
    }

    public void assignProductToGroup(int productID, int groupID) throws SQLException {
        productDAO.assignProductToGroup(productID, groupID);
    }

    public List<Product> listProducts(String criteria, String query) throws SQLException {
        return productDAO.listProductsByCriteria(criteria, query);
    }
}
