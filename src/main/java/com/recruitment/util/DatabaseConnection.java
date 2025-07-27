package main.java.com.recruitment.util;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Database connection utility class using DriverManager
 */
public class DatabaseConnection {
    private static final Logger logger = Logger.getLogger(DatabaseConnection.class.getName());
    private static String dbUrl;
    private static String dbUsername;
    private static String dbPassword;
    private static String dbDriver;

    static {
        try {
            initializeConnectionProperties();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize database connection properties", e);
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    /**
     * Initializing the database connection properties
     */
    private static void initializeConnectionProperties() throws IOException {
        Properties props = new Properties();

        try (InputStream input = DatabaseConnection.class.getClassLoader()
                .getResourceAsStream("database.properties")) {

            if (input == null) {
                throw new IOException("Unable to find database.properties");
            }

            props.load(input);
        }

        dbUrl = props.getProperty("db.url");
        dbUsername = props.getProperty("db.username");
        dbPassword = props.getProperty("db.password");
        dbDriver = props.getProperty("db.driver");

        try {
            Class.forName(dbDriver);
        } catch (ClassNotFoundException e) {
            throw new IOException("Database driver not found: " + dbDriver, e);
        }
        logger.info("Database connection properties initialized successfully");
    }

    /**
     * Get a database connection
     * @return Connection object
     * @throws SQLException if connection cannot be obtained
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
    }


    /**
     * Test database connectivity
     * @return true if connection is successful
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database connection test failed", e);
            return false;
        }
    }
} 