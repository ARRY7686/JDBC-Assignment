package main.java.com.recruitment.model;

import java.time.LocalDateTime;

public class Application {
    private long applicationId;
    private long jobId;
    private long candidateId;
    private String currentStatus;
    private LocalDateTime appliedDate;
    private LocalDateTime updatedAt;
    
    // Additional fields for joined data
    private String resumeUrl;
    private String candidateFirstName;
    private String candidateLastName;
    private String candidateEmail;
    private String jobTitle;
    private String jobDescription;
    private String companyName;
    
    // Constructors
    public Application() {}
    
    public Application(long applicationId, long jobId, long candidateId, 
                      String currentStatus, LocalDateTime appliedDate, 
                      LocalDateTime updatedAt) {
        this.applicationId = applicationId;
        this.jobId = jobId;
        this.candidateId = candidateId;
        this.currentStatus = currentStatus;
        this.appliedDate = appliedDate;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public long getApplicationId() { return applicationId; }
    public void setApplicationId(long applicationId) { this.applicationId = applicationId; }
    
    public long getJobId() { return jobId; }
    public void setJobId(long jobId) { this.jobId = jobId; }
    
    public long getCandidateId() { return candidateId; }
    public void setCandidateId(long candidateId) { this.candidateId = candidateId; }
    
    public String getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(String currentStatus) { this.currentStatus = currentStatus; }
    
    public LocalDateTime getAppliedDate() { return appliedDate; }
    public void setAppliedDate(LocalDateTime appliedDate) { this.appliedDate = appliedDate; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public String getResumeUrl() { return resumeUrl; }
    public void setResumeUrl(String resumeUrl) { this.resumeUrl = resumeUrl; }
    
    public String getCandidateFirstName() { return candidateFirstName; }
    public void setCandidateFirstName(String candidateFirstName) { this.candidateFirstName = candidateFirstName; }
    
    public String getCandidateLastName() { return candidateLastName; }
    public void setCandidateLastName(String candidateLastName) { this.candidateLastName = candidateLastName; }
    
    public String getCandidateEmail() { return candidateEmail; }
    public void setCandidateEmail(String candidateEmail) { this.candidateEmail = candidateEmail; }
    
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    
    public String getJobDescription() { return jobDescription; }
    public void setJobDescription(String jobDescription) { this.jobDescription = jobDescription; }
    
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    
    @Override
    public String toString() {
        return "Application{" +
                "applicationId=" + applicationId +
                ", jobId=" + jobId +
                ", candidateId=" + candidateId +
                ", currentStatus='" + currentStatus + '\'' +
                ", appliedDate=" + appliedDate +
                '}';
    }
}
