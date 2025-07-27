package main.java.com.recruitment;

import main.java.com.recruitment.dao.*;
import main.java.com.recruitment.model.*;
import main.java.com.recruitment.util.DatabaseConnection;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.logging.Logger;

public class recruitmentApp {
    private static final Logger logger = Logger.getLogger(recruitmentApp.class.getName());
    private static Scanner scanner = new Scanner(System.in);
    
    // DAO instances
    private static ApplicationDAO applicationDAO = new ApplicationDAO();
    private static CandidateDAO candidateDAO = new CandidateDAO();
    private static JobDAO jobDAO = new JobDAO();
    private static InterviewDAO interviewDAO = new InterviewDAO();
    private static OfferDAO offerDAO = new OfferDAO();
    
    public static void main(String[] args) {
        logger.info("Starting Recruitment Management System");
        
        // Test database connection
        if (!DatabaseConnection.testConnection()) {
            logger.severe("Failed to connect to database. Exiting...");
            return;
        }
        
        logger.info("Database connection successful");
        
        // Start the application
        showMainMenu();
    }
    
    private static void showMainMenu() {
        while (true) {
            System.out.println("\n=== Recruitment Management System ===");
            System.out.println("1. Manage Applications");
            System.out.println("2. Manage Candidates");
            System.out.println("3. Manage Jobs");
            System.out.println("4. Manage Interviews");
            System.out.println("5. Manage Offers");
            System.out.println("6. Reports & Statistics");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            
            switch (choice) {
                case 1 -> manageApplications();
                case 2 -> manageCandidates();
                case 3 -> manageJobs();
                case 4 -> manageInterviews();
                case 5 -> manageOffers();
                case 6 -> showReports();
                case 0 -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }
    
    private static void manageApplications() {
        System.out.println("\n=== Application Management ===");
        System.out.println("1. View all applications");
        System.out.println("2. View applications by job");
        System.out.println("3. View applications by candidate");
        System.out.println("4. Update application status");
        System.out.println("5. Create new application");
        System.out.println("0. Back to main menu");
        
        int choice = scanner.nextInt();
        scanner.nextLine();
        
        switch (choice) {
            case 1 -> viewAllApplications();
            case 2 -> viewApplicationsByJob();
            case 3 -> viewApplicationsByCandidate();
            case 4 -> updateApplicationStatus();
            case 5 -> createNewApplication();
        }
    }
    
    private static void viewAllApplications() {
        var applications = applicationDAO.getApplicationsByStatus(ApplicationDAO.ApplicationStatus.APPLIED);
        System.out.println("\nAll Applications (Applied status):");
        applications.forEach(System.out::println);
    }
    
    private static void viewApplicationsByJob() {
        System.out.print("Enter Job ID: ");
        long jobId = scanner.nextLong();
        
        var applications = applicationDAO.getApplicationsByJob(jobId);
        System.out.println("\nApplications for Job " + jobId + ":");
        applications.forEach(System.out::println);
    }
    
    private static void viewApplicationsByCandidate() {
        System.out.print("Enter Candidate ID: ");
        long candidateId = scanner.nextLong();
        
        var applications = applicationDAO.getApplicationsByCandidate(candidateId);
        System.out.println("\nApplications by Candidate " + candidateId + ":");
        applications.forEach(System.out::println);
    }
    
    private static void updateApplicationStatus() {
        System.out.print("Enter Application ID: ");
        long applicationId = scanner.nextLong();
        
        System.out.println("Select new status:");
        System.out.println("1. APPLIED");
        System.out.println("2. SCREENED");
        System.out.println("3. INTERVIEW");
        System.out.println("4. OFFER");
        System.out.println("5. REJECTED");
        
        int statusChoice = scanner.nextInt();
        ApplicationDAO.ApplicationStatus[] statuses = ApplicationDAO.ApplicationStatus.values();
        
        if (statusChoice >= 1 && statusChoice <= statuses.length) {
            ApplicationDAO.ApplicationStatus newStatus = statuses[statusChoice - 1];
            boolean success = applicationDAO.updateApplicationStatus(applicationId, newStatus);
            System.out.println(success ? "Status updated successfully!" : "Failed to update status.");
        }
    }
    
    private static void createNewApplication() {
        System.out.print("Enter Job ID: ");
        long jobId = scanner.nextLong();
        
        System.out.print("Enter Candidate ID: ");
        long candidateId = scanner.nextLong();
        
        boolean success = applicationDAO.createApplication(jobId, candidateId, ApplicationDAO.ApplicationStatus.APPLIED);
        System.out.println(success ? "Application created successfully!" : "Failed to create application.");
    }
    
    private static void manageCandidates() {
        System.out.println("\n=== Candidate Management ===");
        System.out.println("1. View all candidates");
        System.out.println("2. Add new candidate");
        System.out.println("3. Update candidate resume URL");
        System.out.println("4. Delete candidate");
        System.out.println("5. Search candidates by name");
        System.out.println("0. Back to main menu");
        
        int choice = scanner.nextInt();
        scanner.nextLine();
        
        switch (choice) {
            case 1 -> viewAllCandidates();
            case 2 -> addNewCandidate();
            case 3 -> updateCandidateInfo();
            case 4 -> deleteCandidate();
            case 5 -> searchCandidatesByName();
        }
    }
    
    private static void viewAllCandidates() {
        var candidates = candidateDAO.getAllCandidates();
        System.out.println("\nAll Candidates:");
        candidates.forEach(System.out::println);
    }
    
    private static void addNewCandidate() {
        System.out.print("Enter User ID (must exist in User table): ");
        long userId = scanner.nextLong();
        scanner.nextLine();
        
        System.out.print("Enter resume URL: ");
        String resumeUrl = scanner.nextLine();
        
        long candidateId = candidateDAO.createCandidate(userId, resumeUrl);
        System.out.println(candidateId > 0 ? "Candidate created successfully with ID: " + candidateId : 
                          "Failed to create candidate. Please ensure User ID exists.");
    }
    
    private static void updateCandidateInfo() {
        System.out.print("Enter Candidate ID: ");
        long candidateId = scanner.nextLong();
        scanner.nextLine();
        
        System.out.print("Enter new resume URL: ");
        String resumeUrl = scanner.nextLine();
        
        boolean success = candidateDAO.updateCandidate(candidateId, resumeUrl);
        System.out.println(success ? "Candidate resume URL updated." : "Failed to update candidate information.");
    }
    
    private static void deleteCandidate() {
        System.out.print("Enter Candidate ID to delete: ");
        long candidateId = scanner.nextLong();
        
        boolean success = candidateDAO.deleteCandidate(candidateId);
        System.out.println(success ? "Candidate deleted successfully." : "Failed to delete candidate.");
    }
    
    private static void searchCandidatesByName() {
        System.out.print("Enter name keywords to search: ");
        String keywords = scanner.nextLine();
        
        var candidates = candidateDAO.searchCandidatesByName(keywords);
        System.out.println("\nSearch Results:");
        candidates.forEach(System.out::println);
    }
    
    private static void manageJobs() {
        System.out.println("\n=== Job Management ===");
        System.out.println("1. View all jobs");
        System.out.println("2. View active jobs");
        System.out.println("3. Add new job");
        System.out.println("4. Update job information");
        System.out.println("5. Delete job");
        System.out.println("6. View jobs by company");
        System.out.println("0. Back to main menu");
        
        int choice = scanner.nextInt();
        scanner.nextLine();
        
        switch (choice) {
            case 1 -> viewAllJobs();
            case 2 -> viewActiveJobs();
            case 3 -> addNewJob();
            case 4 -> updateJobInfo();
            case 5 -> deleteJob();
            case 6 -> viewJobsByCompany();
        }
    }
    
    private static void viewAllJobs() {
        var jobs = jobDAO.getAllJobs();
        System.out.println("\nAll Jobs:");
        jobs.forEach(System.out::println);
    }
    
    private static void viewActiveJobs() {
        var jobs = jobDAO.getActiveJobs();
        System.out.println("\nActive Jobs:");
        jobs.forEach(System.out::println);
    }
    
    private static void addNewJob() {
        System.out.print("Enter company ID: ");
        long companyId = scanner.nextLong();
        
        System.out.print("Enter department ID: ");
        long departmentId = scanner.nextLong();
        scanner.nextLine();
        
        System.out.print("Enter job title: ");
        String title = scanner.nextLine();
        
        System.out.print("Enter job description: ");
        String description = scanner.nextLine();
        
        System.out.println("Select job status:");
        System.out.println("1. OPEN");
        System.out.println("2. CLOSED");
        System.out.println("3. ON_HOLD");
        int statusChoice = scanner.nextInt();
        
        JobDAO.JobStatus[] statuses = JobDAO.JobStatus.values();
        if (statusChoice >= 1 && statusChoice <= statuses.length) {
            JobDAO.JobStatus status = statuses[statusChoice - 1];
            long jobId = jobDAO.createJob(companyId, departmentId, title, description, status);
            System.out.println(jobId > 0 ? "Job created successfully with ID: " + jobId : "Failed to create job.");
        } else {
            System.out.println("Invalid status choice.");
        }
    }
    
    private static void updateJobInfo() {
        System.out.print("Enter Job ID: ");
        long jobId = scanner.nextLong();
        scanner.nextLine();
        
        System.out.print("Enter new title: ");
        String title = scanner.nextLine();
        
        System.out.print("Enter new description: ");
        String description = scanner.nextLine();
        
        System.out.println("Select new status:");
        System.out.println("1. OPEN");
        System.out.println("2. CLOSED");
        System.out.println("3. ON_HOLD");
        int statusChoice = scanner.nextInt();
        
        JobDAO.JobStatus[] statuses = JobDAO.JobStatus.values();
        if (statusChoice >= 1 && statusChoice <= statuses.length) {
            JobDAO.JobStatus status = statuses[statusChoice - 1];
            boolean success = jobDAO.updateJob(jobId, title, description, status);
            System.out.println(success ? "Job updated successfully." : "Failed to update job.");
        } else {
            System.out.println("Invalid status choice.");
        }
    }
    
    private static void deleteJob() {
        System.out.print("Enter Job ID to delete: ");
        long jobId = scanner.nextLong();
        
        boolean success = jobDAO.deleteJob(jobId);
        System.out.println(success ? "Job deleted successfully." : "Failed to delete job.");
    }
    
    private static void viewJobsByCompany() {
        System.out.print("Enter Company ID: ");
        long companyId = scanner.nextLong();
        
        var jobs = jobDAO.getJobsByCompany(companyId);
        System.out.println("\nJobs for Company " + companyId + ":");
        jobs.forEach(System.out::println);
    }
    
    private static void manageInterviews() {
        System.out.println("\n=== Interview Management ===");
        System.out.println("1. View upcoming interviews");
        System.out.println("2. Schedule interview");
        System.out.println("3. Update interview result");
        System.out.println("4. Cancel interview");
        System.out.println("5. View interviews by application");
        System.out.println("0. Back to main menu");
        
        int choice = scanner.nextInt();
        scanner.nextLine();
        
        switch (choice) {
            case 1 -> viewUpcomingInterviews();
            case 2 -> scheduleInterview();
            case 3 -> updateInterviewResult();
            case 4 -> cancelInterview();
            case 5 -> viewInterviewsByApplication();
        }
    }
    
    private static void viewUpcomingInterviews() {
        var interviews = interviewDAO.getUpcomingInterviews();
        System.out.println("\nUpcoming Interviews:");
        interviews.forEach(System.out::println);
    }
    
    private static void scheduleInterview() {
        System.out.print("Enter interview title: ");
        String title = scanner.nextLine();
        
        System.out.print("Enter interviewer ID: ");
        long interviewerId = scanner.nextLong();
        
        System.out.print("Enter application ID: ");
        long applicationId = scanner.nextLong();
        scanner.nextLine();
        
        System.out.println("Select interview stage:");
        System.out.println("1. HR");
        System.out.println("2. TECHNICAL");
        System.out.println("3. MANAGERIAL");
        int stageChoice = scanner.nextInt();
        scanner.nextLine();
        
        System.out.print("Enter interview date and time (YYYY-MM-DD HH:MM:SS): ");
        String dateTimeStr = scanner.nextLine();
        
        try {
            LocalDateTime interviewDate = LocalDateTime.parse(dateTimeStr.replace(" ", "T"));
            InterviewDAO.InterviewStage[] stages = InterviewDAO.InterviewStage.values();
            
            if (stageChoice >= 1 && stageChoice <= stages.length) {
                InterviewDAO.InterviewStage stage = stages[stageChoice - 1];
                long interviewId = interviewDAO.scheduleInterview(title, interviewerId, applicationId, stage, interviewDate);
                System.out.println(interviewId > 0 ? "Interview scheduled successfully with ID: " + interviewId : 
                                 "Failed to schedule interview.");
            } else {
                System.out.println("Invalid stage choice.");
            }
        } catch (Exception e) {
            System.out.println("Invalid date format. Please use YYYY-MM-DD HH:MM:SS");
        }
    }
    
    private static void updateInterviewResult() {
        System.out.print("Enter Interview ID: ");
        long interviewId = scanner.nextLong();
        
        System.out.println("Select interview result:");
        System.out.println("1. PASS");
        System.out.println("2. FAIL");
        System.out.println("3. PENDING");
        int resultChoice = scanner.nextInt();
        
        InterviewDAO.InterviewResult[] results = InterviewDAO.InterviewResult.values();
        if (resultChoice >= 1 && resultChoice <= results.length) {
            InterviewDAO.InterviewResult result = results[resultChoice - 1];
            boolean success = interviewDAO.updateInterviewResult(interviewId, result);
            System.out.println(success ? "Interview result updated successfully." : "Failed to update interview result.");
        } else {
            System.out.println("Invalid result choice.");
        }
    }
    
    private static void cancelInterview() {
        System.out.print("Enter Interview ID to cancel: ");
        long interviewId = scanner.nextLong();
        
        boolean success = interviewDAO.cancelInterview(interviewId);
        System.out.println(success ? "Interview canceled successfully." : "Failed to cancel interview.");
    }
    
    private static void viewInterviewsByApplication() {
        System.out.print("Enter Application ID: ");
        long applicationId = scanner.nextLong();
        
        var interviews = interviewDAO.getInterviewsByApplication(applicationId);
        System.out.println("\nInterviews for Application " + applicationId + ":");
        interviews.forEach(System.out::println);
    }
    
    private static void manageOffers() {
        System.out.println("\n=== Offer Management ===");
        System.out.println("1. View pending offers");
        System.out.println("2. Create offer");
        System.out.println("3. Update offer");
        System.out.println("4. Update offer status");
        System.out.println("5. View offers by candidate");
        System.out.println("0. Back to main menu");
        
        int choice = scanner.nextInt();
        scanner.nextLine();
        
        switch (choice) {
            case 1 -> viewPendingOffers();
            case 2 -> createOffer();
            case 3 -> updateOffer();
            case 4 -> updateOfferStatus();
            case 5 -> viewOffersByCandidate();
        }
    }
    
    private static void viewPendingOffers() {
        var offers = offerDAO.getPendingOffers();
        System.out.println("\nPending Offers:");
        offers.forEach(System.out::println);
    }
    
    private static void createOffer() {
        System.out.print("Enter Application ID: ");
        long applicationId = scanner.nextLong();
        
        System.out.print("Enter salary offered: ");
        double salary = scanner.nextDouble();
        
        System.out.println("Select offer status:");
        System.out.println("1. PENDING");
        System.out.println("2. ACCEPTED");
        System.out.println("3. DECLINED");
        int statusChoice = scanner.nextInt();
        
        OfferDAO.OfferStatus[] statuses = OfferDAO.OfferStatus.values();
        if (statusChoice >= 1 && statusChoice <= statuses.length) {
            OfferDAO.OfferStatus status = statuses[statusChoice - 1];
            long offerId = offerDAO.createOffer(applicationId, salary, status);
            System.out.println(offerId > 0 ? "Offer created successfully with ID: " + offerId : "Failed to create offer.");
        } else {
            System.out.println("Invalid status choice.");
        }
    }
    
    private static void updateOffer() {
        System.out.print("Enter Offer ID: ");
        long offerId = scanner.nextLong();
        
        System.out.print("Enter new salary: ");
        double salary = scanner.nextDouble();
        
        System.out.println("Select new status:");
        System.out.println("1. PENDING");
        System.out.println("2. ACCEPTED");
        System.out.println("3. DECLINED");
        int statusChoice = scanner.nextInt();
        
        OfferDAO.OfferStatus[] statuses = OfferDAO.OfferStatus.values();
        if (statusChoice >= 1 && statusChoice <= statuses.length) {
            OfferDAO.OfferStatus status = statuses[statusChoice - 1];
            boolean success = offerDAO.updateOffer(offerId, salary, status);
            System.out.println(success ? "Offer updated successfully." : "Failed to update offer.");
        } else {
            System.out.println("Invalid status choice.");
        }
    }
    
    private static void updateOfferStatus() {
        System.out.print("Enter Offer ID: ");
        long offerId = scanner.nextLong();
        
        System.out.println("Select new status:");
        System.out.println("1. PENDING");
        System.out.println("2. ACCEPTED");
        System.out.println("3. DECLINED");
        int statusChoice = scanner.nextInt();
        
        OfferDAO.OfferStatus[] statuses = OfferDAO.OfferStatus.values();
        if (statusChoice >= 1 && statusChoice <= statuses.length) {
            OfferDAO.OfferStatus status = statuses[statusChoice - 1];
            boolean success = offerDAO.updateOfferStatus(offerId, status);
            System.out.println(success ? "Offer status updated successfully." : "Failed to update offer status.");
        } else {
            System.out.println("Invalid status choice.");
        }
    }
    
    private static void viewOffersByCandidate() {
        System.out.print("Enter Candidate ID: ");
        long candidateId = scanner.nextLong();
        
        var offers = offerDAO.getOffersByCandidate(candidateId);
        System.out.println("\nOffers for Candidate " + candidateId + ":");
        offers.forEach(System.out::println);
    }
    
    private static void showReports() {
        System.out.println("\n=== Reports & Statistics ===");
        System.out.println("1. Candidate Statistics");
        System.out.println("2. Job Statistics");
        System.out.println("3. Offer Statistics");
        System.out.println("0. Back to main menu");
        
        int choice = scanner.nextInt();
        scanner.nextLine();
        
        switch (choice) {
            case 1 -> showCandidateStatistics();
            case 2 -> showJobStatistics();
            case 3 -> showOfferStatistics();
        }
    }
    
    private static void showCandidateStatistics() {
        System.out.print("Enter Candidate ID: ");
        long candidateId = scanner.nextLong();
        
        var stats = candidateDAO.getCandidateStatistics(candidateId);
        if (stats != null) {
            System.out.println("\nCandidate Statistics:");
            System.out.println(stats);
        } else {
            System.out.println("No statistics found for candidate " + candidateId);
        }
    }
    
    private static void showJobStatistics() {
        System.out.print("Enter Company ID: ");
        long companyId = scanner.nextLong();
        
        var stats = jobDAO.getJobStatistics(companyId);
        if (stats != null) {
            System.out.println("\nJob Statistics:");
            System.out.println(stats);
        } else {
            System.out.println("No statistics found for company " + companyId);
        }
    }
    
    private static void showOfferStatistics() {
        System.out.print("Enter Company ID: ");
        long companyId = scanner.nextLong();
        
        var stats = offerDAO.getOfferStatistics(companyId);
        if (stats != null) {
            System.out.println("\nOffer Statistics:");
            System.out.println(stats);
        } else {
            System.out.println("No statistics found for company " + companyId);
        }
    }
}
