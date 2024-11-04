package com.etextbook.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    private static final HikariDataSource dataSource;
    
    static {
        try {
            dataSource = initializeDataSource();
        } catch (IOException e) {
            logger.error("Failed to initialize database connection pool", e);
            throw new RuntimeException("Database initialization failed", e);
        }
    }
    
    private static HikariDataSource initializeDataSource() throws IOException {
        Properties props = loadProperties();
        HikariConfig config = new HikariConfig();
        
        // Set basic properties
        config.setJdbcUrl(props.getProperty("db.url"));
        config.setUsername(props.getProperty("db.username"));
        config.setPassword(props.getProperty("db.password"));
        config.setDriverClassName(props.getProperty("db.driverClassName"));
        
        // Set pool configuration
        config.setMaximumPoolSize(Integer.parseInt(props.getProperty("db.poolSize", "10")));
        config.setConnectionTimeout(Long.parseLong(props.getProperty("db.connectionTimeout", "30000")));
        config.setIdleTimeout(Long.parseLong(props.getProperty("db.idleTimeout", "600000")));
        config.setMaxLifetime(Long.parseLong(props.getProperty("db.maxLifetime", "1800000")));
        
        // Set additional properties for better performance
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        
        return new HikariDataSource(config);
    }
    
    private static Properties loadProperties() throws IOException {
        Properties props = new Properties();
        try (InputStream input = DatabaseConfig.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new IOException("Unable to find application.properties");
            }
            props.load(input);
        }
        return props;
    }
    
    public static Connection getConnection() throws SQLException {
        try {
            Connection connection = dataSource.getConnection();
            logger.debug("Database connection obtained successfully");
            return connection;
        } catch (SQLException e) {
            logger.error("Failed to obtain database connection", e);
            throw e;
        }
    }
    
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                logger.debug("Database connection closed successfully");
            } catch (SQLException e) {
                logger.error("Error closing database connection", e);
            }
        }
    }
    
    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Database connection pool shut down successfully");
        }
    }
}