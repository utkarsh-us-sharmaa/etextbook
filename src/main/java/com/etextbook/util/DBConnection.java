package com.etextbook.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/etextbook";
    private static final String USER = "root";
    private static final String PASSWORD = "Passw0rd#55";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            // Test if connection is valid
            if (conn.isValid(5)) { // timeout of 5 seconds
                // Try a simple query
                stmt.executeQuery("SELECT 1");
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

}
