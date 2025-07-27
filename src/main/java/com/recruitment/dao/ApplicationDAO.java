package main.java.com.recruitment.dao;

import main.java.com.recruitment.model.Application;
import main.java.com.recruitment.util.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ApplicationDAO {
    private static final Logger logger = Logger.getLogger(ApplicationDAO.class.getName());
    
    // Application status enum values from schema
    public enum ApplicationStatus {
        APPLIED("applied"),
        SCREENED("screened"), 
        INTERVIEW("interview"),
        OFFER("offer"),
        REJECTED("rejected");
        
        private final String value;
        
        ApplicationStatus(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
    }
    
    /**
     * Create a new application
     */
    public boolean createApplication(long jobId, long candidateId, ApplicationStatus status) {
        String sql = "INSERT INTO Applications (job_id, candidate_id, current_status, applied_date, updated_at) VALUES (?, ?, ?, NOW(), NOW())";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, jobId);
            stmt.setLong(2, candidateId);
            stmt.setString(3, status.getValue());
            
            int rowsAffected = stmt.executeUpdate();
            logger.info("Application created successfully for candidate: " + candidateId + " and job: " + jobId);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error creating application", e);
            return false;
        }
    }
    
    /**
     * Update application status
     */
    public boolean updateApplicationStatus(long applicationId, ApplicationStatus newStatus) {
        String sql = "UPDATE Applications SET current_status = ?, updated_at = NOW() WHERE application_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newStatus.getValue());
            stmt.setLong(2, applicationId);
            
            int rowsAffected = stmt.executeUpdate();
            logger.info("Application status updated to: " + newStatus.getValue() + " for application: " + applicationId);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating application status", e);
            return false;
        }
    }
    
    /**
     * Get applications by job ID
     */
    public List<Application> getApplicationsByJob(long jobId) {
        String sql = """
            SELECT a.application_id, a.job_id, a.candidate_id, a.current_status, 
                   a.applied_date, a.updated_at, c.resume_url,
                   u.first_name, u.last_name, u.email
            FROM Applications a
            JOIN Candidate c ON a.candidate_id = c.candidate_id
            JOIN User u ON c.user_id = u.user_id
            WHERE a.job_id = ?
            ORDER BY a.applied_date DESC
            """;
        
        List<Application> applications = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, jobId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Application app = createApplicationFromResultSet(rs);
                    applications.add(app);
                }
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving applications for job: " + jobId, e);
        }
        
        return applications;
    }
    
    /**
     * Get applications by candidate ID
     */
    public List<Application> getApplicationsByCandidate(long candidateId) {
        String sql = """
            SELECT a.application_id, a.job_id, a.candidate_id, a.current_status, 
                   a.applied_date, a.updated_at, j.title, j.description, 
                   c.company_name
            FROM Applications a
            JOIN Job j ON a.job_id = j.job_id
            JOIN company c ON j.company_id = c.company_id
            WHERE a.candidate_id = ?
            ORDER BY a.applied_date DESC
            """;
        
        List<Application> applications = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, candidateId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Application app = createApplicationFromResultSet(rs);
                    applications.add(app);
                }
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving applications for candidate: " + candidateId, e);
        }
        
        return applications;
    }
    
    /**
     * Get application by ID
     */
    public Application getApplicationById(long applicationId) {
        String sql = """
            SELECT a.application_id, a.job_id, a.candidate_id, a.current_status, 
                   a.applied_date, a.updated_at
            FROM Applications a
            WHERE a.application_id = ?
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, applicationId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createApplicationFromResultSet(rs);
                }
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving application: " + applicationId, e);
        }
        
        return null;
    }
    
    /**
     * Get applications by status
     */
    public List<Application> getApplicationsByStatus(ApplicationStatus status) {
        String sql = "SELECT * FROM Applications WHERE current_status = ? ORDER BY applied_date DESC";
        List<Application> applications = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.getValue());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Application app = createApplicationFromResultSet(rs);
                    applications.add(app);
                }
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving applications by status: " + status, e);
        }
        
        return applications;
    }
    
    /**
     * Delete application
     */
    public boolean deleteApplication(long applicationId) {
        String sql = "DELETE FROM Applications WHERE application_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, applicationId);
            
            int rowsAffected = stmt.executeUpdate();
            logger.info("Application deleted: " + applicationId);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting application: " + applicationId, e);
            return false;
        }
    }
    
    /**
     * Helper method to create Application object from ResultSet
     */
    private Application createApplicationFromResultSet(ResultSet rs) throws SQLException {
        Application app = new Application();
        app.setApplicationId(rs.getLong("application_id"));
        app.setJobId(rs.getLong("job_id"));
        app.setCandidateId(rs.getLong("candidate_id"));
        app.setCurrentStatus(rs.getString("current_status"));
        app.setAppliedDate(rs.getTimestamp("applied_date").toLocalDateTime());
        app.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        
        // Optional fields from joins
        try {
            app.setResumeUrl(rs.getString("resume_url"));
            app.setCandidateFirstName(rs.getString("first_name"));
            app.setCandidateLastName(rs.getString("last_name"));
            app.setCandidateEmail(rs.getString("email"));
            app.setJobTitle(rs.getString("title"));
            app.setJobDescription(rs.getString("description"));
            app.setCompanyName(rs.getString("company_name"));
        } catch (SQLException e) {
            // Fields may not be present in all queries
        }
        
        return app;
    }
}
