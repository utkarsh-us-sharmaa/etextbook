package com.etextbook.util;

import org.junit.Test;
import java.sql.Connection;
import java.sql.SQLException;

public class DBConnectionTest {
    
    @Test
    public void testDatabaseConnection() {
        try (Connection conn = DBConnection.getConnection()) {
            boolean isValid = conn.isValid(5); // timeout in seconds
            assert isValid : "Database connection is not valid";
            System.out.println("Database connection successful!");
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            throw new AssertionError("Database connection test failed", e);
        }
    }
}