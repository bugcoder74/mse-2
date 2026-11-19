-- Q2: MySQL database and tables
-- (Hibernate also creates these automatically via ddl-auto=update;
--  this script is for documentation / manual setup.)

CREATE DATABASE IF NOT EXISTS student_db;
USE student_db;

CREATE TABLE IF NOT EXISTS students (
    student_id        VARCHAR(20)  PRIMARY KEY,               -- prevents duplicate IDs
    name              VARCHAR(100) NOT NULL,
    email             VARCHAR(150) NOT NULL UNIQUE,           -- prevents duplicate emails
    course_name       VARCHAR(100) NOT NULL,
    programming_score DOUBLE       NOT NULL CHECK (programming_score BETWEEN 0 AND 100),
    mathematics_score DOUBLE       NOT NULL CHECK (mathematics_score BETWEEN 0 AND 100)
);

CREATE TABLE IF NOT EXISTS student_skills (
    student_id VARCHAR(20)  NOT NULL,
    skill      VARCHAR(100) NOT NULL,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);
