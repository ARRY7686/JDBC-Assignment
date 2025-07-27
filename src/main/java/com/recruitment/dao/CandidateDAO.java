package main.java.com.recruitment.dao;

import main.java.com.recruitment.model.Candidate;
import main.java.com.recruitment.util.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class CandidateDAO {
    private static final Logger logger = Logger.getLogger(CandidateDAO.class.getName());
    
    /**
     * Create a new candidate profile
     */
    public long createCandidate(long userId, String resumeUrl) {
        String sql = """
            INSERT INTO Candidate (user_id, resume_url, created_at) 
            VALUES (?, ?, NOW())
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setLong(1, userId);
            stmt.setString(2, resumeUrl);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long candidateId = generatedKeys.getLong(1);
                        logger.info("Candidate profile created successfully with ID: " + candidateId);
                        return candidateId;
                    }
                }
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error creating candidate profile", e);
        }
        
        return -1;
    }
    
    /**
     * Update candidate profile
     */
    public boolean updateCandidate(long candidateId, String resumeUrl) {
        String sql = "UPDATE Candidate SET resume_url = ? WHERE candidate_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, resumeUrl);
            stmt.setLong(2, candidateId);
            
            int rowsAffected = stmt.executeUpdate();
            logger.info("Candidate profile updated successfully: " + candidateId);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating candidate profile: " + candidateId, e);
            return false;
        }
    }
    
    /**
     * Get candidate by ID with user details
     */
    public Candidate getCandidateById(long candidateId) {
        String sql = """
            SELECT c.candidate_id, c.user_id, c.resume_url, c.created_at,
                   u.first_name, u.last_name, u.email, u.phone_no,
                   COUNT(a.application_id) as application_count
            FROM Candidate c
            JOIN User u ON c.user_id = u.user_id
            LEFT JOIN Applications a ON c.candidate_id = a.candidate_id
            WHERE c.candidate_id = ?
            GROUP BY c.candidate_id
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, candidateId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createCandidateFromResultSet(rs);
                }
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving candidate: " + candidateId, e);
        }
        
        return null;
    }
    
    /**
     * Search candidates by name
     */
    public List<Candidate> searchCandidatesByName(String nameKeywords) {
        String sql = """
            SELECT c.candidate_id, c.user_id, c.resume_url, c.created_at,
                   u.first_name, u.last_name, u.email, u.phone_no,
                   COUNT(a.application_id) as application_count
            FROM Candidate c
            JOIN User u ON c.user_id = u.user_id
            LEFT JOIN Applications a ON c.candidate_id = a.candidate_id
            WHERE u.first_name LIKE ? OR u.last_name LIKE ?
            GROUP BY c.candidate_id
            ORDER BY c.created_at DESC
            """;
        
        List<Candidate> candidates = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String pattern = "%" + nameKeywords + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Candidate candidate = createCandidateFromResultSet(rs);
                    candidates.add(candidate);
                }
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error searching candidates by name: " + nameKeywords, e);
        }
        
        return candidates;
    }
    
    /**
     * Get all candidates
     */
    public List<Candidate> getAllCandidates() {
        String sql = """
            SELECT c.candidate_id, c.user_id, c.resume_url, c.created_at,
                   u.first_name, u.last_name, u.email, u.phone_no,
                   COUNT(a.application_id) as application_count
            FROM Candidate c
            JOIN User u ON c.user_id = u.user_id
            LEFT JOIN Applications a ON c.candidate_id = a.candidate_id
            GROUP BY c.candidate_id
            ORDER BY c.created_at DESC
            """;
        
        List<Candidate> candidates = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Candidate candidate = createCandidateFromResultSet(rs);
                candidates.add(candidate);
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving all candidates", e);
        }
        
        return candidates;
    }
    
    /**
     * Get candidate statistics
     */
    public CandidateStatistics getCandidateStatistics(long candidateId) {
        String sql = """
            SELECT 
                COUNT(a.application_id) as total_applications,
                SUM(CASE WHEN a.current_status = 'applied' THEN 1 ELSE 0 END) as pending_applications,
                SUM(CASE WHEN a.current_status = 'interview' THEN 1 ELSE 0 END) as interview_applications,
                SUM(CASE WHEN a.current_status = 'offer' THEN 1 ELSE 0 END) as offer_applications,
                SUM(CASE WHEN a.current_status = 'rejected' THEN 1 ELSE 0 END) as rejected_applications,
                COUNT(DISTINCT i.interview_id) as total_interviews,
                COUNT(DISTINCT o.offer_id) as total_offers
            FROM Candidate c
            LEFT JOIN Applications a ON c.candidate_id = a.candidate_id
            LEFT JOIN Interview i ON a.application_id = i.application_id
            LEFT JOIN offer o ON a.application_id = o.application_id
            WHERE c.candidate_id = ?
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, candidateId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new CandidateStatistics(
                        rs.getInt("total_applications"),
                        rs.getInt("pending_applications"),
                        rs.getInt("interview_applications"),
                        rs.getInt("offer_applications"),
                        rs.getInt("rejected_applications"),
                        rs.getInt("total_interviews"),
                        rs.getInt("total_offers")
                    );
                }
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving candidate statistics: " + candidateId, e);
        }
        
        return null;
    }
    
    /**
     * Delete candidate profile
     */
    public boolean deleteCandidate(long candidateId) {
        String sql = "DELETE FROM Candidate WHERE candidate_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, candidateId);
            
            int rowsAffected = stmt.executeUpdate();
            logger.info("Candidate profile deleted: " + candidateId);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting candidate profile: " + candidateId, e);
            return false;
        }
    }
    
    /**
     * Helper method to create Candidate object from ResultSet
     */
    private Candidate createCandidateFromResultSet(ResultSet rs) throws SQLException {
        Candidate candidate = new Candidate();
        candidate.setCandidateId(rs.getLong("candidate_id"));
        candidate.setUserId(rs.getLong("user_id"));
        candidate.setResumeUrl(rs.getString("resume_url"));
        candidate.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        
        // User details
        candidate.setFirstName(rs.getString("first_name"));
        candidate.setLastName(rs.getString("last_name"));
        candidate.setEmail(rs.getString("email"));
        candidate.setPhone(rs.getString("phone_no"));
        
        // Application count
        candidate.setApplicationCount(rs.getInt("application_count"));
        
        return candidate;
    }
    
    // Inner class for Candidate Statistics
    public static class CandidateStatistics {
        private int totalApplications;
        private int pendingApplications;
        private int interviewApplications;
        private int offerApplications;
        private int rejectedApplications;
        private int totalInterviews;
        private int totalOffers;
        
        public CandidateStatistics(int totalApplications, int pendingApplications, 
                                  int interviewApplications, int offerApplications, 
                                  int rejectedApplications, int totalInterviews, int totalOffers) {
            this.totalApplications = totalApplications;
            this.pendingApplications = pendingApplications;
            this.interviewApplications = interviewApplications;
            this.offerApplications = offerApplications;
            this.rejectedApplications = rejectedApplications;
            this.totalInterviews = totalInterviews;
            this.totalOffers = totalOffers;
        }
        
        // Getters
        public int getTotalApplications() { return totalApplications; }
        public int getPendingApplications() { return pendingApplications; }
        public int getInterviewApplications() { return interviewApplications; }
        public int getOfferApplications() { return offerApplications; }
        public int getRejectedApplications() { return rejectedApplications; }
        public int getTotalInterviews() { return totalInterviews; }
        public int getTotalOffers() { return totalOffers; }
        
        public double getSuccessRate() {
            return totalApplications > 0 ? (double) offerApplications / totalApplications * 100 : 0;
        }
        
        @Override
        public String toString() {
            return "CandidateStatistics{" +
                    "totalApplications=" + totalApplications +
                    ", pendingApplications=" + pendingApplications +
                    ", interviewApplications=" + interviewApplications +
                    ", offerApplications=" + offerApplications +
                    ", rejectedApplications=" + rejectedApplications +
                    ", totalInterviews=" + totalInterviews +
                    ", totalOffers=" + totalOffers +
                    ", successRate=" + String.format("%.2f", getSuccessRate()) + "%" +
                    '}';
        }
    }
}
