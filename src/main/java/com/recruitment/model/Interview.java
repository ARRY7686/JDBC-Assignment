package main.java.com.recruitment.model;

import java.time.LocalDateTime;

public class Interview {
    private long interviewId;
    private String interviewTitle;
    private long interviewerId;
    private long applicationId;
    private String interviewStage;
    private LocalDateTime interviewDate;
    private String result;
    private LocalDateTime createdAt;
    
    // Additional fields from joins
    private String interviewerFirstName;
    private String interviewerLastName;
    private String candidateFirstName;
    private String candidateLastName;
    private String jobTitle;
    
    // Constructors
    public Interview() {}
    
    // Getters and Setters
    public long getInterviewId() { return interviewId; }
    public void setInterviewId(long interviewId) { this.interviewId = interviewId; }
    
    public String getInterviewTitle() { return interviewTitle; }
    public void setInterviewTitle(String interviewTitle) { this.interviewTitle = interviewTitle; }
    
    public long getInterviewerId() { return interviewerId; }
    public void setInterviewerId(long interviewerId) { this.interviewerId = interviewerId; }
    
    public long getApplicationId() { return applicationId; }
    public void setApplicationId(long applicationId) { this.applicationId = applicationId; }
    
    public String getInterviewStage() { return interviewStage; }
    public void setInterviewStage(String interviewStage) { this.interviewStage = interviewStage; }
    
    public LocalDateTime getInterviewDate() { return interviewDate; }
    public void setInterviewDate(LocalDateTime interviewDate) { this.interviewDate = interviewDate; }
    
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public String getInterviewerFirstName() { return interviewerFirstName; }
    public void setInterviewerFirstName(String interviewerFirstName) { this.interviewerFirstName = interviewerFirstName; }
    
    public String getInterviewerLastName() { return interviewerLastName; }
    public void setInterviewerLastName(String interviewerLastName) { this.interviewerLastName = interviewerLastName; }
    
    public String getCandidateFirstName() { return candidateFirstName; }
    public void setCandidateFirstName(String candidateFirstName) { this.candidateFirstName = candidateFirstName; }
    
    public String getCandidateLastName() { return candidateLastName; }
    public void setCandidateLastName(String candidateLastName) { this.candidateLastName = candidateLastName; }
    
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    
    @Override
    public String toString() {
        return "Interview{" +
                "interviewId=" + interviewId +
                ", interviewTitle='" + interviewTitle + '\'' +
                ", applicationId=" + applicationId +
                ", interviewStage='" + interviewStage + '\'' +
                ", interviewDate=" + interviewDate +
                ", result='" + result + '\'' +
                '}';
    }
}
