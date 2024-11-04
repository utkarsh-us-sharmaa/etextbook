package com.etextbook.dao;

import java.sql.Connection;
import java.sql.SQLException;

import com.etextbook.util.DBConnection;

public class DBConnectionTest {
    public static void main(String[] args) {
        try {
            Connection connection = DBConnection.getConnection();
            if (connection != null) {
                System.out.println("Connection successful!");
                connection.close();
            } else {
                System.out.println("Failed to make connection!");
            }
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }
}
