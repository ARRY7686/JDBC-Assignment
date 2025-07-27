package main.java.com.recruitment.model;

import java.time.LocalDateTime;

public class Candidate {
    private long candidateId;
    private long userId;
    private String resumeUrl;
    private LocalDateTime createdAt;
    
    // User details
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    
    // Additional fields
    private int applicationCount;
    private String applicationStatus;
    private LocalDateTime appliedDate;
    private long applicationId;
    private LocalDateTime lastApplicationDate;
    
    // Constructors
    public Candidate() {}
    
    public Candidate(String name, String email) {
        String[] parts = name.split(" ", 2);
        this.firstName = parts[0];
        this.lastName = parts.length > 1 ? parts[1] : "";
        this.email = email;
    }
    
    // Getters and Setters
    public long getCandidateId() { return candidateId; }
    public void setCandidateId(long candidateId) { this.candidateId = candidateId; }
    
    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }
    
    public String getResumeUrl() { return resumeUrl; }
    public void setResumeUrl(String resumeUrl) { this.resumeUrl = resumeUrl; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public int getApplicationCount() { return applicationCount; }
    public void setApplicationCount(int applicationCount) { this.applicationCount = applicationCount; }
    
    public String getApplicationStatus() { return applicationStatus; }
    public void setApplicationStatus(String applicationStatus) { this.applicationStatus = applicationStatus; }
    
    public LocalDateTime getAppliedDate() { return appliedDate; }
    public void setAppliedDate(LocalDateTime appliedDate) { this.appliedDate = appliedDate; }
    
    public long getApplicationId() { return applicationId; }
    public void setApplicationId(long applicationId) { this.applicationId = applicationId; }
    
    public LocalDateTime getLastApplicationDate() { return lastApplicationDate; }
    public void setLastApplicationDate(LocalDateTime lastApplicationDate) { this.lastApplicationDate = lastApplicationDate; }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    @Override
    public String toString() {
        return "Candidate{" +
                "candidateId=" + candidateId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", applicationCount=" + applicationCount +
                '}';
    }
}
