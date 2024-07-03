package org.example.daos;

import org.example.models.Product;
import org.example.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDAO {
    private final ProductGroupDAO groupDAO;

    public ProductDAO(ProductGroupDAO groupDAO) {
        this.groupDAO = groupDAO;
    }
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
        String query = "UPDATE products SET name = ?, description = ?, producer = ?, amount = ?, price = ?, group_id = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
            PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, product.getName());
            statement.setString(2, product.getDescription());
            statement.setString(3, product.getProducer());
            statement.setInt(4, product.getAmount());
            statement.setDouble(5, product.getPrice());
            statement.setInt(6, product.getGroupID());
            statement.setInt(7, product.getId());
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
                case "amount", "price", "group_id" -> "SELECT * FROM products WHERE " + criteria + " = ?";
                default -> throw new IllegalArgumentException("Invalid search criteria: " + criteria);
            };
        }

        try (Connection con = DBConnection.getConnection();
            PreparedStatement statement = con.prepareStatement(query)) {

            if(!searchQuery.isEmpty()) {
                switch (criteria.toLowerCase()) {
                    case "amount", "group_id":
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

    public double calculateTotalCost() throws SQLException {
        String query = "SELECT SUM(amount * price) AS total_cost FROM products";
        try(Connection con = DBConnection.getConnection();
        Statement statement = con.createStatement();
        ResultSet res = statement.executeQuery(query)) {
            if(res.next()) {
                return res.getDouble("total_cost");
            }
        }
        return 0.0;
    }

    public Map<String, Double> calculateTotalCostPerGroup() throws SQLException {
        Map<String, Double> totalCostPerGroup = new HashMap<>();
        String query = "SELECT group_id, SUM(amount * price) AS total_cost FROM products GROUP BY group_id";
        try(Connection con = DBConnection.getConnection();
        Statement statement = con.createStatement();
        ResultSet res = statement.executeQuery(query)) {
            while (res.next()) {
                int groupID = res.getInt("group_id");
                double totalCost = res.getDouble("total_cost");
                String groupName = groupDAO.getGroupName(groupID);
                totalCostPerGroup.put(groupName, totalCost);
            }
        }

        return totalCostPerGroup;
    }
}
