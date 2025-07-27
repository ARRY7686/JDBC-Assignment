package main.java.com.recruitment.dao;

import main.java.com.recruitment.model.Job;
import main.java.com.recruitment.util.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class JobDAO {
    private static final Logger logger = Logger.getLogger(JobDAO.class.getName());
    
    // Job status enum values based on schema
    public enum JobStatus {
        OPEN("open"),
        CLOSED("closed"),
        ON_HOLD("on_hold");
        
        private final String value;
        
        JobStatus(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
    }
    
    /**
     * Create a new job posting
     */
    public long createJob(long companyId, long departmentId, String title, String description, JobStatus status) {
        String sql = """
            INSERT INTO Job (company_id, department_id, title, description, status, created_at) 
            VALUES (?, ?, ?, ?, ?, NOW())
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setLong(1, companyId);
            stmt.setLong(2, departmentId);
            stmt.setString(3, title);
            stmt.setString(4, description);
            stmt.setString(5, status.getValue());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long jobId = generatedKeys.getLong(1);
                        logger.info("Job created successfully with ID: " + jobId);
                        return jobId;
                    }
                }
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error creating job", e);
        }
        
        return -1;
    }
    
    /**
     * Update job details
     */
    public boolean updateJob(long jobId, String title, String description, JobStatus status) {
        String sql = """
            UPDATE Job 
            SET title = ?, description = ?, status = ?
            WHERE job_id = ?
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setString(3, status.getValue());
            stmt.setLong(4, jobId);
            
            int rowsAffected = stmt.executeUpdate();
            logger.info("Job updated successfully: " + jobId);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating job: " + jobId, e);
            return false;
        }
    }
    
    /**
     * Get job by ID
     */
    public Job getJobById(long jobId) {
        String sql = """
            SELECT j.*, c.company_name, d.name as department_name,
                   COUNT(a.application_id) as application_count
            FROM Job j
            JOIN company c ON j.company_id = c.company_id
            JOIN department d ON j.department_id = d.department_id
            LEFT JOIN Applications a ON j.job_id = a.job_id
            WHERE j.job_id = ?
            GROUP BY j.job_id
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, jobId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createJobFromResultSet(rs);
                }
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving job: " + jobId, e);
        }
        
        return null;
    }
    
    /**
     * Get all active jobs
     */
    public List<Job> getActiveJobs() {
        String sql = """
            SELECT j.*, c.company_name, d.name as department_name,
                   COUNT(a.application_id) as application_count
            FROM Job j
            JOIN company c ON j.company_id = c.company_id
            JOIN department d ON j.department_id = d.department_id
            LEFT JOIN Applications a ON j.job_id = a.job_id
            WHERE j.status = 'open'
            GROUP BY j.job_id
            ORDER BY j.created_at DESC
            """;
        
        return getJobsByQuery(sql);
    }
    
    /**
     * Get all jobs
     */
    public List<Job> getAllJobs() {
        String sql = """
            SELECT j.*, c.company_name, d.name as department_name,
                   COUNT(a.application_id) as application_count
            FROM Job j
            JOIN company c ON j.company_id = c.company_id
            JOIN department d ON j.department_id = d.department_id
            LEFT JOIN Applications a ON j.job_id = a.job_id
            GROUP BY j.job_id
            ORDER BY j.created_at DESC
            """;
        
        return getJobsByQuery(sql);
    }
    
    /**
     * Get jobs by company ID
     */
    public List<Job> getJobsByCompany(long companyId) {
        String sql = """
            SELECT j.*, c.company_name, d.name as department_name,
                   COUNT(a.application_id) as application_count
            FROM Job j
            JOIN company c ON j.company_id = c.company_id
            JOIN department d ON j.department_id = d.department_id
            LEFT JOIN Applications a ON j.job_id = a.job_id
            WHERE j.company_id = ?
            GROUP BY j.job_id
            ORDER BY j.created_at DESC
            """;
        
        List<Job> jobs = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, companyId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Job job = createJobFromResultSet(rs);
                    jobs.add(job);
                }
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving jobs for company: " + companyId, e);
        }
        
        return jobs;
    }
    
    /**
     * Get job statistics for a company
     */
    public JobStatistics getJobStatistics(long companyId) {
        String sql = """
            SELECT 
                COUNT(*) as total_jobs,
                SUM(CASE WHEN status = 'open' THEN 1 ELSE 0 END) as open_jobs,
                SUM(CASE WHEN status = 'closed' THEN 1 ELSE 0 END) as closed_jobs,
                SUM(CASE WHEN status = 'on_hold' THEN 1 ELSE 0 END) as on_hold_jobs,
                COUNT(DISTINCT a.application_id) as total_applications
            FROM Job j
            LEFT JOIN Applications a ON j.job_id = a.job_id
            WHERE j.company_id = ?
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, companyId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new JobStatistics(
                        rs.getInt("total_jobs"),
                        rs.getInt("open_jobs"),
                        rs.getInt("closed_jobs"),
                        rs.getInt("on_hold_jobs"),
                        rs.getInt("total_applications")
                    );
                }
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving job statistics for company: " + companyId, e);
        }
        
        return null;
    }
    
    /**
     * Delete job
     */
    public boolean deleteJob(long jobId) {
        String sql = "DELETE FROM Job WHERE job_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, jobId);
            
            int rowsAffected = stmt.executeUpdate();
            logger.info("Job deleted: " + jobId);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting job: " + jobId, e);
            return false;
        }
    }
    
    /**
     * Helper method to execute job queries
     */
    private List<Job> getJobsByQuery(String sql) {
        List<Job> jobs = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Job job = createJobFromResultSet(rs);
                jobs.add(job);
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error executing job query", e);
        }
        
        return jobs;
    }
    
    /**
     * Helper method to create Job object from ResultSet
     */
    private Job createJobFromResultSet(ResultSet rs) throws SQLException {
        Job job = new Job();
        job.setJobId(rs.getLong("job_id"));
        job.setTitle(rs.getString("title"));
        job.setDepartmentId(rs.getLong("department_id"));
        job.setDescription(rs.getString("description"));
        job.setStatus(rs.getString("status"));
        job.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        job.setCompanyId(rs.getLong("company_id"));
        
        // Additional fields from joins
        job.setCompanyName(rs.getString("company_name"));
        job.setDepartmentName(rs.getString("department_name"));
        job.setApplicationCount(rs.getInt("application_count"));
        
        return job;
    }
    
    // Inner class for Job Statistics
    public static class JobStatistics {
        private int totalJobs;
        private int openJobs;
        private int closedJobs;
        private int onHoldJobs;
        private int totalApplications;
        
        public JobStatistics(int totalJobs, int openJobs, int closedJobs, int onHoldJobs, int totalApplications) {
            this.totalJobs = totalJobs;
            this.openJobs = openJobs;
            this.closedJobs = closedJobs;
            this.onHoldJobs = onHoldJobs;
            this.totalApplications = totalApplications;
        }
        
        // Getters
        public int getTotalJobs() { return totalJobs; }
        public int getOpenJobs() { return openJobs; }
        public int getClosedJobs() { return closedJobs; }
        public int getOnHoldJobs() { return onHoldJobs; }
        public int getTotalApplications() { return totalApplications; }
        
        @Override
        public String toString() {
            return "JobStatistics{" +
                    "totalJobs=" + totalJobs +
                    ", openJobs=" + openJobs +
                    ", closedJobs=" + closedJobs +
                    ", onHoldJobs=" + onHoldJobs +
                    ", totalApplications=" + totalApplications +
                    '}';
        }
    }
}
