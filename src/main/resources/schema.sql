CREATE TABLE department (
    department_id BIGINT PRIMARY KEY,
    name ENUM('HR', 'Engineering', 'Marketing', 'Sales') NOT NULL
);

CREATE TABLE company (
    company_id BIGINT PRIMARY KEY,
    company_name VARCHAR(255) NOT NULL
);

CREATE TABLE Job (
    job_id BIGINT PRIMARY KEY,
    title VARCHAR(255),
    department_id BIGINT,
    description VARCHAR(1000),
    status ENUM('open', 'closed', 'on_hold'),
    created_at DATETIME,
    company_id BIGINT,
    FOREIGN KEY (department_id) REFERENCES department(department_id),
    FOREIGN KEY (company_id) REFERENCES company(company_id)
);

CREATE TABLE Candidate (
    candidate_id BIGINT PRIMARY KEY,
    user_id BIGINT,
    resume_url VARCHAR(255),
    created_at DATETIME
);

CREATE TABLE Applications (
    application_id BIGINT PRIMARY KEY,
    job_id BIGINT,
    candidate_id BIGINT,
    current_status ENUM('applied', 'screened', 'interview', 'offer', 'rejected'),
    applied_date DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (job_id) REFERENCES Job(job_id),
    FOREIGN KEY (candidate_id) REFERENCES Candidate(candidate_id)
);

CREATE TABLE Application_stage (
    stage_id BIGINT PRIMARY KEY,
    application_id BIGINT,
    stage_name ENUM('applied', 'screened', 'interview', 'offer', 'rejected'),
    stage_datetime DATETIME,
    FOREIGN KEY (application_id) REFERENCES Applications(application_id)
);

CREATE TABLE Interview (
    interview_id BIGINT PRIMARY KEY,
    interview_title VARCHAR(255),
    interviewer_id BIGINT,
    application_id BIGINT,
    interview_stage ENUM('HR', 'Technical', 'Managerial'),
    interview_date DATETIME,
    result ENUM('pass', 'fail', 'pending'),
    created_at DATETIME,
    FOREIGN KEY (application_id) REFERENCES Applications(application_id)
);

CREATE TABLE offer (
    offer_id BIGINT PRIMARY KEY,
    application_id BIGINT UNIQUE,
    salary_offered DOUBLE,
    status ENUM('pending', 'accepted', 'declined'),
    offer_date DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (application_id) REFERENCES Applications(application_id)
);

CREATE TABLE User (
    user_id BIGINT PRIMARY KEY,
    email VARCHAR(255),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    password_hash VARCHAR(255),
    created_at DATETIME,
    phone_no VARCHAR(20),
    company_id BIGINT,
    FOREIGN KEY (company_id) REFERENCES company(company_id)
);

CREATE TABLE role (
    role_id BIGINT PRIMARY KEY,
    name ENUM('admin', 'recruiter', 'interviewer')
);

CREATE TABLE user_role (
    user_id BIGINT,
    role_id BIGINT,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES User(user_id),
    FOREIGN KEY (role_id) REFERENCES role(role_id)
);

CREATE TABLE Recruiter_Activity (
    recruiter_id BIGINT,
    activity_type ENUM('interview_scheduled', 'offer_sent'),
    timestamp TIMESTAMP,
    interviewer_id BIGINT,
    FOREIGN KEY (interviewer_id) REFERENCES User(user_id)
);
