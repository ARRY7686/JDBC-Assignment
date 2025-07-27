package main.java.com.recruitment.dao;

import main.java.com.recruitment.model.Offer;
import main.java.com.recruitment.util.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class OfferDAO {
    private static final Logger logger = Logger.getLogger(OfferDAO.class.getName());
    
    // Offer status enum values based on schema
    public enum OfferStatus {
        PENDING("pending"),
        ACCEPTED("accepted"),
        DECLINED("declined");
        
        private final String value;
        
        OfferStatus(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
    }
    
    /**
     * Create a new job offer
     */
    public long createOffer(long applicationId, double salaryOffered, OfferStatus status) {
        String sql = """
            INSERT INTO offer (application_id, salary_offered, status, offer_date, updated_at) 
            VALUES (?, ?, ?, NOW(), NOW())
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setLong(1, applicationId);
            stmt.setDouble(2, salaryOffered);
            stmt.setString(3, status.getValue());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long offerId = generatedKeys.getLong(1);
                        logger.info("Offer created successfully with ID: " + offerId);
                        return offerId;
                    }
                }
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error creating offer", e);
        }
        
        return -1;
    }
    
    /**
     * Update offer details
     */
    public boolean updateOffer(long offerId, double salaryOffered, OfferStatus status) {
        String sql = """
            UPDATE offer 
            SET salary_offered = ?, status = ?, updated_at = NOW() 
            WHERE offer_id = ?
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, salaryOffered);
            stmt.setString(2, status.getValue());
            stmt.setLong(3, offerId);
            
            int rowsAffected = stmt.executeUpdate();
            logger.info("Offer updated successfully: " + offerId);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating offer: " + offerId, e);
            return false;
        }
    }
    
    /**
     * Update offer status
     */
    public boolean updateOfferStatus(long offerId, OfferStatus status) {
        String sql = "UPDATE offer SET status = ?, updated_at = NOW() WHERE offer_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.getValue());
            stmt.setLong(2, offerId);
            
            int rowsAffected = stmt.executeUpdate();
            logger.info("Offer status updated to: " + status.getValue() + " for offer: " + offerId);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating offer status", e);
            return false;
        }
    }
    
    /**
     * Get offer by ID
     */
    public Offer getOfferById(long offerId) {
        String sql = """
            SELECT o.*, a.candidate_id, a.job_id, j.title as job_title,
                   c.first_name as candidate_first_name, c.last_name as candidate_last_name,
                   c.email as candidate_email, comp.company_name
            FROM offer o
            JOIN Applications a ON o.application_id = a.application_id
            JOIN Job j ON a.job_id = j.job_id
            JOIN company comp ON j.company_id = comp.company_id
            JOIN Candidate cand ON a.candidate_id = cand.candidate_id
            JOIN User c ON cand.user_id = c.user_id
            WHERE o.offer_id = ?
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, offerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createOfferFromResultSet(rs);
                }
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving offer: " + offerId, e);
        }
        
        return null;
    }
    
    /**
     * Get offers by candidate ID
     */
    public List<Offer> getOffersByCandidate(long candidateId) {
        String sql = """
            SELECT o.*, a.candidate_id, a.job_id, j.title as job_title,
                   comp.company_name
            FROM offer o
            JOIN Applications a ON o.application_id = a.application_id
            JOIN Job j ON a.job_id = j.job_id
            JOIN company comp ON j.company_id = comp.company_id
            WHERE a.candidate_id = ?
            ORDER BY o.offer_date DESC
            """;
        
        List<Offer> offers = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, candidateId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Offer offer = createOfferFromResultSet(rs);
                    offers.add(offer);
                }
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving offers for candidate: " + candidateId, e);
        }
        
        return offers;
    }
    
    /**
     * Get pending offers
     */
    public List<Offer> getPendingOffers() {
        String sql = """
            SELECT o.*, a.candidate_id, a.job_id, j.title as job_title,
                   comp.company_name, c.first_name as candidate_first_name, 
                   c.last_name as candidate_last_name
            FROM offer o
            JOIN Applications a ON o.application_id = a.application_id
            JOIN Job j ON a.job_id = j.job_id
            JOIN company comp ON j.company_id = comp.company_id
            JOIN Candidate cand ON a.candidate_id = cand.candidate_id
            JOIN User c ON cand.user_id = c.user_id
            WHERE o.status = 'pending'
            ORDER BY o.offer_date DESC
            """;
        
        List<Offer> offers = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Offer offer = createOfferFromResultSet(rs);
                    offers.add(offer);
                }
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving pending offers", e);
        }
        
        return offers;
    }
    
    /**
     * Get offer statistics for a company
     */
    public OfferStatistics getOfferStatistics(long companyId) {
        String sql = """
            SELECT 
                COUNT(*) as total_offers,
                SUM(CASE WHEN o.status = 'pending' THEN 1 ELSE 0 END) as pending_offers,
                SUM(CASE WHEN o.status = 'accepted' THEN 1 ELSE 0 END) as accepted_offers,
                SUM(CASE WHEN o.status = 'declined' THEN 1 ELSE 0 END) as declined_offers,
                AVG(o.salary_offered) as average_salary
            FROM offer o
            JOIN Applications a ON o.application_id = a.application_id
            JOIN Job j ON a.job_id = j.job_id
            WHERE j.company_id = ?
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, companyId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new OfferStatistics(
                        rs.getInt("total_offers"),
                        rs.getInt("pending_offers"),
                        rs.getInt("accepted_offers"),
                        rs.getInt("declined_offers"),
                        rs.getDouble("average_salary")
                    );
                }
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving offer statistics for company: " + companyId, e);
        }
        
        return null;
    }
    
    /**
     * Delete offer
     */
    public boolean deleteOffer(long offerId) {
        String sql = "DELETE FROM offer WHERE offer_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, offerId);
            
            int rowsAffected = stmt.executeUpdate();
            logger.info("Offer deleted: " + offerId);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting offer: " + offerId, e);
            return false;
        }
    }
    
    /**
     * Helper method to create Offer object from ResultSet
     */
    private Offer createOfferFromResultSet(ResultSet rs) throws SQLException {
        Offer offer = new Offer();
        offer.setOfferId(rs.getLong("offer_id"));
        offer.setApplicationId(rs.getLong("application_id"));
        offer.setSalaryOffered(rs.getDouble("salary_offered"));
        offer.setStatus(rs.getString("status"));
        offer.setOfferDate(rs.getTimestamp("offer_date").toLocalDateTime());
        offer.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        
        // Additional fields from joins
        try {
            offer.setCandidateId(rs.getLong("candidate_id"));
            offer.setJobId(rs.getLong("job_id"));
            offer.setJobTitle(rs.getString("job_title"));
            offer.setCompanyName(rs.getString("company_name"));
            offer.setCandidateFirstName(rs.getString("candidate_first_name"));
            offer.setCandidateLastName(rs.getString("candidate_last_name"));
            offer.setCandidateEmail(rs.getString("candidate_email"));
        } catch (SQLException e) {
            // Fields may not be present in all queries
        }
        
        return offer;
    }
    
    // Inner class for Offer Statistics
    public static class OfferStatistics {
        private int totalOffers;
        private int pendingOffers;
        private int acceptedOffers;
        private int declinedOffers;
        private double averageSalary;
        
        public OfferStatistics(int totalOffers, int pendingOffers, int acceptedOffers, 
                              int declinedOffers, double averageSalary) {
            this.totalOffers = totalOffers;
            this.pendingOffers = pendingOffers;
            this.acceptedOffers = acceptedOffers;
            this.declinedOffers = declinedOffers;
            this.averageSalary = averageSalary;
        }
        
        // Getters
        public int getTotalOffers() { return totalOffers; }
        public int getPendingOffers() { return pendingOffers; }
        public int getAcceptedOffers() { return acceptedOffers; }
        public int getDeclinedOffers() { return declinedOffers; }
        public double getAverageSalary() { return averageSalary; }
        
        @Override
        public String toString() {
            return "OfferStatistics{" +
                    "totalOffers=" + totalOffers +
                    ", pendingOffers=" + pendingOffers +
                    ", acceptedOffers=" + acceptedOffers +
                    ", declinedOffers=" + declinedOffers +
                    ", averageSalary=" + String.format("%.2f", averageSalary) +
                    '}';
        }
    }
}
