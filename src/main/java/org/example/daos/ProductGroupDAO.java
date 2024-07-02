package org.example.daos;

import org.example.utils.DBConnection;

import javax.xml.transform.Result;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    public int getGroupID(String name) throws SQLException {
        String query = "SELECT id FROM product_groups WHERE name LIKE ?";
        try(Connection con = DBConnection.getConnection();
        PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, name);
            ResultSet res = statement.executeQuery();
            if(res.next()) {
                return res.getInt("id");
            }
        }
        return 0;
    }

    public List<String> getAllGroupNames() throws SQLException {
        List<String> groupNames = new ArrayList<>();
        String query = "SELECT name FROM product_groups";
        try(Connection con = DBConnection.getConnection();
        PreparedStatement statement = con.prepareStatement(query);
        ResultSet res = statement.executeQuery()) {
            while(res.next()) {
                groupNames.add(res.getString("name"));
            }
        }
        return groupNames;
    }
}
