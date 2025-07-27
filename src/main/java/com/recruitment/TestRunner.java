package main.java.com.recruitment;

import main.java.com.recruitment.util.DatabaseConnection;

public class TestRunner {
    public static void main(String[] args) {
        System.out.println("Testing Recruitment System Setup...");
        
        // Test database connection
        if (DatabaseConnection.testConnection()) {
            System.out.println("✓ Database connection successful");
            
            // Start the main application
            recruitmentApp.main(args);
        } else {
            System.out.println("✗ Database connection failed");
            System.out.println("Please check your database.properties file and ensure MySQL is running");
        }
    }
}
