package main.java.com.recruitment.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.logging.Level;

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
        InputStream input = null;

        try {
            // Try multiple paths to find the properties file
            logger.info("Attempting to load database.properties...");
            
            // Method 1: Try classpath resource (for compiled resources)
            input = DatabaseConnection.class.getClassLoader()
                    .getResourceAsStream("main/resources/database.properties");
            
            if (input != null) {
                logger.info("Found database.properties in classpath: main/resources/database.properties");
            }
            
            // Method 2: Try without main/resources prefix
            if (input == null) {
                input = DatabaseConnection.class.getClassLoader()
                        .getResourceAsStream("database.properties");
                if (input != null) {
                    logger.info("Found database.properties in classpath root");
                }
            }
            
            // Method 3: Try relative file path from source
            if (input == null) {
                try {
                    input = new FileInputStream("src/main/resources/database.properties");
                    logger.info("Found database.properties in src/main/resources/");
                } catch (Exception e) {
                    logger.info("Could not find properties file in src/main/resources/");
                }
            }
            
            // Method 4: Try from bin directory
            if (input == null) {
                try {
                    input = new FileInputStream("bin/main/resources/database.properties");
                    logger.info("Found database.properties in bin/main/resources/");
                } catch (Exception e) {
                    logger.info("Could not find properties file in bin/main/resources/");
                }
            }
            
            // Method 5: Try current directory
            if (input == null) {
                try {
                    input = new FileInputStream("database.properties");
                    logger.info("Found database.properties in current directory");
                } catch (Exception e) {
                    logger.info("Could not find properties file in current directory");
                }
            }

            if (input == null) {
                // List current directory contents for debugging
                logger.severe("Unable to find database.properties in any location. Current working directory: " 
                    + System.getProperty("user.dir"));
                throw new IOException("Unable to find database.properties in any expected location");
            }

            props.load(input);
            logger.info("Successfully loaded database.properties");
            
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    logger.log(Level.WARNING, "Failed to close properties file", e);
                }
            }
        }

        dbUrl = props.getProperty("db.url");
        dbUsername = props.getProperty("db.username");
        dbPassword = props.getProperty("db.password");
        dbDriver = props.getProperty("db.driver");

        // Validate that all properties are loaded
        if (dbUrl == null || dbUsername == null || dbPassword == null || dbDriver == null) {
            logger.severe("Missing required database properties. Found: url=" + (dbUrl != null) 
                + ", username=" + (dbUsername != null) + ", password=" + (dbPassword != null) 
                + ", driver=" + (dbDriver != null));
            throw new IOException("Missing required database properties");
        }

        try {
            Class.forName(dbDriver);
            logger.info("Database driver loaded successfully: " + dbDriver);
        } catch (ClassNotFoundException e) {
            throw new IOException("Database driver not found: " + dbDriver, e);
        }
        
        logger.info("Database connection properties initialized successfully");
        logger.info("Connecting to: " + dbUrl + " as user: " + dbUsername);
    }

    /**
     * Get a database connection
     * @return Connection object
     * @throws SQLException if connection cannot be obtained
     */
    public static Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            logger.info("Database connection established successfully");
            return conn;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to establish database connection", e);
            throw e;
        }
    }

    /**
     * Test database connectivity
     * @return true if connection is successful
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            boolean isValid = conn != null && !conn.isClosed();
            if (isValid) {
                logger.info("Database connection test successful");
                // Test with a simple query
                try (var stmt = conn.createStatement()) {
                    var rs = stmt.executeQuery("SELECT 1");
                    if (rs.next()) {
                        logger.info("Database query test successful");
                    }
                }
            }
            return isValid;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database connection test failed: " + e.getMessage(), e);
            return false;
        }
    }
}