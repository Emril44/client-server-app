package org.example.daos;

import org.example.utils.DBConnection;

import javax.xml.transform.Result;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductGroupDAO {
    public void createGroup(String name, String description) throws SQLException {
        String query = "INSERT INTO product_groups (name, description) VALUES (?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setString(2, description);
            statement.executeUpdate();
        }
    }

    public void assignProductToGroup(int productID, int groupID) throws SQLException {
        String query = "UPDATE products SET group_id = ? WHERE id = ?";
        try(Connection con = DBConnection.getConnection();
            PreparedStatement statement = con.prepareStatement(query)) {
            statement.setInt(1, groupID);
            statement.setInt(2, productID);
            statement.executeUpdate();
        }
    }

    public String getGroupName(int groupID) throws SQLException {
        String query = "SELECT name FROM product_groups WHERE id = ?";
        try(Connection con = DBConnection.getConnection();
        PreparedStatement statement = con.prepareStatement(query)) {
            statement.setInt(1, groupID);
            ResultSet res = statement.executeQuery();
            if(res.next()) {
                return res.getString("name");
            }
        }
        return null;
    }
}
