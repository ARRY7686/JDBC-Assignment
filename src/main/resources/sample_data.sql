-- Companies
INSERT INTO company VALUES 
(1, 'TechCorp'), (2, 'InnoSoft'), (3, 'DataWiz');

-- Departments
INSERT INTO department VALUES 
(101, 'Engineering'), (102, 'HR'), (103, 'Marketing'), (104, 'Sales');

-- Roles
INSERT INTO role VALUES 
(1, 'admin'), (2, 'recruiter'), (3, 'interviewer');

-- Users
INSERT INTO User VALUES
(1, 'john@techcorp.com', 'John', 'Doe', 'hash1', NOW(), '1234567890', 1),
(2, 'jane@techcorp.com', 'Jane', 'Smith', 'hash2', NOW(), '0987654321', 1),
(3, 'ivy@techcorp.com', 'Ivy', 'Lee', 'hash3', NOW(), '1112223333', 1),
(4, 'mike@innosoft.com', 'Mike', 'Brown', 'hash4', NOW(), '4445556666', 2),
(5, 'sara@datawiz.com', 'Sara', 'Khan', 'hash5', NOW(), '7778889999', 3),
(6, 'tom@techcorp.com', 'Tom', 'Hanks', 'hash6', NOW(), '9876543210', 1),
(7, 'lucy@techcorp.com', 'Lucy', 'Liu', 'hash7', NOW(), '8765432109', 1),
(8, 'kevin@techcorp.com', 'Kevin', 'Bacon', 'hash8', NOW(), '7654321098', 1),
(9, 'emma@innosoft.com', 'Emma', 'Stone', 'hash9', NOW(), '6543210987', 2),
(10, 'oliver@datawiz.com', 'Oliver', 'Twist', 'hash10', NOW(), '5432109876', 3);

-- User Roles
INSERT INTO user_role VALUES 
(1, 2), (2, 1), (3, 3), (4, 2), (5, 3),
(6, 2), (7, 2), (8, 2), (9, 2), (10, 3);

-- Jobs
INSERT INTO Job VALUES
(1001, 'Software Engineer', 101, 'Backend systems', 'open', NOW(), 1),
(1002, 'HR Manager', 102, 'Manage people', 'open', NOW(), 1),
(1003, 'Marketing Lead', 103, 'Promote brand', 'closed', NOW(), 2),
(1004, 'Sales Executive', 104, 'Sales outreach', 'on_hold', NOW(), 3),
(1005, 'Frontend Developer', 101, 'React UI', 'open', NOW(), 1);

-- Candidates
INSERT INTO Candidate VALUES
(2001, 2, 'resume_2001.pdf', NOW()),
(2002, 4, 'resume_2002.pdf', NOW()),
(2003, 5, 'resume_2003.pdf', NOW()),
(2004, 6, 'resume_2004.pdf', NOW()),
(2005, 7, 'resume_2005.pdf', NOW()),
(2006, 8, 'resume_2006.pdf', NOW()),
(2007, 9, 'resume_2007.pdf', NOW()),
(2008, 10, 'resume_2008.pdf', NOW()),
(2009, 1, 'resume_2009.pdf', NOW()),
(2010, 3, 'resume_2010.pdf', NOW());

-- Applications
INSERT INTO Applications VALUES
(3001, 1001, 2001, 'interview', NOW(), NOW()),
(3002, 1002, 2002, 'screened', NOW(), NOW()),
(3003, 1003, 2003, 'applied', NOW(), NOW()),
(3004, 1004, 2004, 'offer', NOW(), NOW()),
(3005, 1005, 2005, 'rejected', NOW(), NOW()),
(3006, 1001, 2006, 'applied', NOW(), NOW()),
(3007, 1002, 2007, 'interview', NOW(), NOW()),
(3008, 1003, 2008, 'offer', NOW(), NOW()),
(3009, 1004, 2009, 'applied', NOW(), NOW()),
(3010, 1005, 2010, 'screened', NOW(), NOW());

-- Application Stages
INSERT INTO Application_stage VALUES
(4001, 3001, 'applied', NOW()), (4002, 3001, 'screened', NOW()), (4003, 3001, 'interview', NOW()),
(4004, 3002, 'applied', NOW()), (4005, 3002, 'screened', NOW()),
(4006, 3004, 'offer', NOW()), (4007, 3005, 'rejected', NOW());

-- Interviews
INSERT INTO Interview VALUES
(5001, 'Tech Round', 3, 3001, 'Technical', NOW(), 'pending', NOW()),
(5002, 'HR Round', 5, 3007, 'HR', NOW(), 'pass', NOW()),
(5003, 'Final Discussion', 10, 3008, 'Managerial', NOW(), 'fail', NOW());

-- Offers
INSERT INTO offer VALUES
(6001, 3004, 95000, 'pending', NOW(), NOW()),
(6002, 3008, 105000, 'declined', NOW(), NOW());

-- Recruiter Activities
INSERT INTO Recruiter_Activity VALUES
(1, 'interview_scheduled', NOW(), 3),
(2, 'offer_sent', NOW(), 5),
(3, 'interview_scheduled', NOW(), 10);
