Recruitment Management System
============================

SETUP INSTRUCTIONS:
1. Ensure MySQL is running on localhost:3306
2. Create database: CREATE DATABASE recruitment_db;
3. Run schema.sql to create tables
4. Run sample_data.sql to insert sample data
5. Update database.properties with your MySQL credentials

RUNNING THE APPLICATION:
java -jar RecruitmentManagementSystem.jar

OR if database.properties is in a different location:
java -Dconfig.path=/path/to/database.properties -jar RecruitmentManagementSystem.jar

FEATURES:
- Application Management
- Candidate Management  
- Job Management
- Interview Scheduling
- Offer Management
- Reports & Statistics

SAMPLE DATA:
- 3 Companies (TechCorp, InnoSoft, DataWiz)
- 10 Users with different roles
- 5 Job postings
- 10 Candidates
- 10 Applications
- 3 Interviews
- 2 Offers

DEFAULT CREDENTIALS:
The system uses the sample data provided in sample_data.sql

REQUIREMENTS:
- Java 8 or higher
- MySQL 5.7 or higher
- MySQL Connector/J (included in JAR)

TROUBLESHOOTING:
- Ensure MySQL service is running
- Check database credentials in database.properties
- Verify database 'recruitment_db' exists
- Check that sample data is loaded

Contact: Aadi Gupta
