package main.java.com.recruitment.dao;

import main.java.com.recruitment.model.Interview;
import main.java.com.recruitment.util.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class InterviewDAO {
    private static final Logger logger = Logger.getLogger(InterviewDAO.class.getName());
    
    // Interview stage enum values based on schema
    public enum InterviewStage {
        HR("HR"),
        TECHNICAL("Technical"),
        MANAGERIAL("Managerial");
        
        private final String value;
        
        InterviewStage(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
    }
    
    // Interview result enum values based on schema
    public enum InterviewResult {
        PASS("pass"),
        FAIL("fail"),
        PENDING("pending");
        
        private final String value;
        
        InterviewResult(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
    }
    
    /**
     * Schedule a new interview
     */
    public long scheduleInterview(String interviewTitle, long interviewerId, long applicationId, 
                                 InterviewStage stage, LocalDateTime interviewDate) {
        String sql = """
            INSERT INTO Interview (interview_title, interviewer_id, application_id, 
                                 interview_stage, interview_date, result, created_at) 
            VALUES (?, ?, ?, ?, ?, ?, NOW())
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, interviewTitle);
            stmt.setLong(2, interviewerId);
            stmt.setLong(3, applicationId);
            stmt.setString(4, stage.getValue());
            stmt.setTimestamp(5, Timestamp.valueOf(interviewDate));
            stmt.setString(6, InterviewResult.PENDING.getValue());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long interviewId = generatedKeys.getLong(1);
                        logger.info("Interview scheduled successfully with ID: " + interviewId);
                        return interviewId;
                    }
                }
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error scheduling interview", e);
        }
        
        return -1;
    }
    
    /**
     * Update interview result
     */
    public boolean updateInterviewResult(long interviewId, InterviewResult result) {
        String sql = "UPDATE Interview SET result = ? WHERE interview_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, result.getValue());
            stmt.setLong(2, interviewId);
            
            int rowsAffected = stmt.executeUpdate();
            logger.info("Interview result updated to: " + result.getValue() + " for interview: " + interviewId);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating interview result", e);
            return false;
        }
    }
    
    /**
     * Get interviews by application ID
     */
    public List<Interview> getInterviewsByApplication(long applicationId) {
        String sql = """
            SELECT i.*, u.first_name as interviewer_first_name, u.last_name as interviewer_last_name
            FROM Interview i
            LEFT JOIN User u ON i.interviewer_id = u.user_id
            WHERE i.application_id = ?
            ORDER BY i.interview_date DESC
            """;
        
        List<Interview> interviews = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, applicationId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Interview interview = createInterviewFromResultSet(rs);
                    interviews.add(interview);
                }
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving interviews for application: " + applicationId, e);
        }
        
        return interviews;
    }
    
    /**
     * Get upcoming interviews
     */
    public List<Interview> getUpcomingInterviews() {
        String sql = """
            SELECT i.*, u.first_name as interviewer_first_name, u.last_name as interviewer_last_name,
                   c.first_name as candidate_first_name, c.last_name as candidate_last_name,
                   j.title as job_title
            FROM Interview i
            LEFT JOIN User u ON i.interviewer_id = u.user_id
            JOIN Applications a ON i.application_id = a.application_id
            JOIN Candidate cand ON a.candidate_id = cand.candidate_id
            JOIN User c ON cand.user_id = c.user_id
            JOIN Job j ON a.job_id = j.job_id
            WHERE i.interview_date >= NOW() AND i.result = 'pending'
            ORDER BY i.interview_date ASC
            """;
        
        List<Interview> interviews = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Interview interview = createInterviewFromResultSet(rs);
                    interviews.add(interview);
                }
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving upcoming interviews", e);
        }
        
        return interviews;
    }
    
    /**
     * Get interview by ID
     */
    public Interview getInterviewById(long interviewId) {
        String sql = """
            SELECT i.*, u.first_name as interviewer_first_name, u.last_name as interviewer_last_name
            FROM Interview i
            LEFT JOIN User u ON i.interviewer_id = u.user_id
            WHERE i.interview_id = ?
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, interviewId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createInterviewFromResultSet(rs);
                }
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving interview: " + interviewId, e);
        }
        
        return null;
    }
    
    /**
     * Cancel interview (set result to fail)
     */
    public boolean cancelInterview(long interviewId) {
        return updateInterviewResult(interviewId, InterviewResult.FAIL);
    }
    
    /**
     * Delete interview
     */
    public boolean deleteInterview(long interviewId) {
        String sql = "DELETE FROM Interview WHERE interview_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, interviewId);
            
            int rowsAffected = stmt.executeUpdate();
            logger.info("Interview deleted: " + interviewId);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting interview: " + interviewId, e);
            return false;
        }
    }
    
    /**
     * Helper method to create Interview object from ResultSet
     */
    private Interview createInterviewFromResultSet(ResultSet rs) throws SQLException {
        Interview interview = new Interview();
        interview.setInterviewId(rs.getLong("interview_id"));
        interview.setInterviewTitle(rs.getString("interview_title"));
        interview.setInterviewerId(rs.getLong("interviewer_id"));
        interview.setApplicationId(rs.getLong("application_id"));
        interview.setInterviewStage(rs.getString("interview_stage"));
        interview.setInterviewDate(rs.getTimestamp("interview_date").toLocalDateTime());
        interview.setResult(rs.getString("result"));
        interview.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        
        // Optional fields from joins
        try {
            interview.setInterviewerFirstName(rs.getString("interviewer_first_name"));
            interview.setInterviewerLastName(rs.getString("interviewer_last_name"));
            interview.setCandidateFirstName(rs.getString("candidate_first_name"));
            interview.setCandidateLastName(rs.getString("candidate_last_name"));
            interview.setJobTitle(rs.getString("job_title"));
        } catch (SQLException e) {
            // Fields may not be present in all queries
        }
        
        return interview;
    }
}
