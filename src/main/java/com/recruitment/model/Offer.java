package main.java.com.recruitment.model;

import java.time.LocalDateTime;

public class Offer {
    private long offerId;
    private long applicationId;
    private double salaryOffered;
    private String status;
    private LocalDateTime offerDate;
    private LocalDateTime updatedAt;
    
    // Additional fields from joins
    private long candidateId;
    private long jobId;
    private String jobTitle;
    private String companyName;
    private String candidateFirstName;
    private String candidateLastName;
    private String candidateEmail;
    
    // Constructors
    public Offer() {}
    
    // Getters and Setters
    public long getOfferId() { return offerId; }
    public void setOfferId(long offerId) { this.offerId = offerId; }
    
    public long getApplicationId() { return applicationId; }
    public void setApplicationId(long applicationId) { this.applicationId = applicationId; }
    
    public double getSalaryOffered() { return salaryOffered; }
    public void setSalaryOffered(double salaryOffered) { this.salaryOffered = salaryOffered; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getOfferDate() { return offerDate; }
    public void setOfferDate(LocalDateTime offerDate) { this.offerDate = offerDate; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public long getCandidateId() { return candidateId; }
    public void setCandidateId(long candidateId) { this.candidateId = candidateId; }
    
    public long getJobId() { return jobId; }
    public void setJobId(long jobId) { this.jobId = jobId; }
    
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    
    public String getCandidateFirstName() { return candidateFirstName; }
    public void setCandidateFirstName(String candidateFirstName) { this.candidateFirstName = candidateFirstName; }
    
    public String getCandidateLastName() { return candidateLastName; }
    public void setCandidateLastName(String candidateLastName) { this.candidateLastName = candidateLastName; }
    
    public String getCandidateEmail() { return candidateEmail; }
    public void setCandidateEmail(String candidateEmail) { this.candidateEmail = candidateEmail; }
    
    @Override
    public String toString() {
        return "Offer{" +
                "offerId=" + offerId +
                ", applicationId=" + applicationId +
                ", salaryOffered=" + salaryOffered +
                ", status='" + status + '\'' +
                ", offerDate=" + offerDate +
                ", jobTitle='" + jobTitle + '\'' +
                ", companyName='" + companyName + '\'' +
                '}';
    }
}
