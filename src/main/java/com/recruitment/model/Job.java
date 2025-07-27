package main.java.com.recruitment.model;

import java.time.LocalDateTime;

public class Job {
    private long jobId;
    private String title;
    private long departmentId;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private long companyId;
    
    // Additional fields from joins
    private String companyName;
    private String departmentName;
    private int applicationCount;
    
    // Constructors
    public Job() {}
    
    // Getters and Setters
    public long getJobId() { return jobId; }
    public void setJobId(long jobId) { this.jobId = jobId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public long getDepartmentId() { return departmentId; }
    public void setDepartmentId(long departmentId) { this.departmentId = departmentId; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public long getCompanyId() { return companyId; }
    public void setCompanyId(long companyId) { this.companyId = companyId; }
    
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    
    public int getApplicationCount() { return applicationCount; }
    public void setApplicationCount(int applicationCount) { this.applicationCount = applicationCount; }
    
    @Override
    public String toString() {
        return "Job{" +
                "jobId=" + jobId +
                ", title='" + title + '\'' +
                ", companyName='" + companyName + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", status='" + status + '\'' +
                ", applicationCount=" + applicationCount +
                '}';
    }
}
