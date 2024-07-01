package org.example.daos;

import org.example.models.Product;
import org.example.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    public void createProduct(Product product) throws SQLException {
        String query = "INSERT INTO products (name, description, producer, amount, price, group_id) VALUES (?, ?, ?, ?, ?, ?)";
        try(Connection con = DBConnection.getConnection();
            PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, product.getName());
            statement.setString(2, product.getDescription());
            statement.setString(3, product.getProducer());
            statement.setInt(4, product.getAmount());
            statement.setDouble(5, product.getPrice());
            statement.setInt(6, product.getGroupID());
            statement.executeUpdate();
        }
    }

    public Product getProduct(int id) throws SQLException {
        String query = "SELECT * FROM products WHERE id = ?";
        try(Connection con = DBConnection.getConnection();
            PreparedStatement statement = con.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet res = statement.executeQuery();
            if(res.next()) {
                return new Product(
                        res.getInt("id"),
                        res.getString("name"),
                        res.getString("description"),
                        res.getString("producer"),
                        res.getInt("amount"),
                        res.getDouble("price"),
                        res.getInt("group_id")
                        );
            }
        }

        return null;
    }

    public void updateProduct(Product product) throws SQLException {
        String query = "UPDATE products SET name = ?, description = ?, producer = ?, amount = ?, price = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
            PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, product.getName());
            statement.setString(2, product.getDescription());
            statement.setString(3, product.getProducer());
            statement.setInt(4, product.getAmount());
            statement.setDouble(5, product.getPrice());
            statement.setInt(6, product.getId());
            statement.executeUpdate();
        }
    }

    public void deleteProduct(int id) throws SQLException {
        String query = "DELETE FROM products WHERE id = ?";
        try(Connection con = DBConnection.getConnection();
            PreparedStatement statement = con.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    public List<Product> listProductsByCriteria(String criteria, String searchQuery) throws SQLException {
        String query;
        List<Product> products = new ArrayList<>();

        if(searchQuery.isEmpty()) {
            query = "SELECT * FROM products";
        } else {
            query = switch (criteria.toLowerCase()) {
                case "name", "description", "producer" -> "SELECT * FROM products WHERE " + criteria + " LIKE ?";
                case "amount", "price" -> "SELECT * FROM products WHERE " + criteria + " = ?";
                default -> throw new IllegalArgumentException("Invalid search criteria: " + criteria);
            };
        }

        try (Connection con = DBConnection.getConnection();
            PreparedStatement statement = con.prepareStatement(query)) {

            if(!searchQuery.isEmpty()) {
                switch (criteria.toLowerCase()) {
                    case "amount":
                        statement.setInt(1, Integer.parseInt(searchQuery));
                        break;
                    case "price":
                        statement.setDouble(1, Double.parseDouble(searchQuery));
                        break;
                    default:
                        statement.setString(1, "%" + searchQuery + "%");
                        break;
                }
            }

            ResultSet res = statement.executeQuery();
            while(res.next()) {
                products.add(new Product(
                        res.getInt("id"),
                        res.getString("name"),
                        res.getString("description"),
                        res.getString("producer"),
                        res.getInt("amount"),
                        res.getDouble("price"),
                        res.getInt("group_id")));
            }
        }
        return products;
    }
}
