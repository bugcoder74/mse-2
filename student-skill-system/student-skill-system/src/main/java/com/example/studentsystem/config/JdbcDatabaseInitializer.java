package com.example.studentsystem.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class JdbcDatabaseInitializer implements CommandLineRunner {

    // ---------- DDL ----------
    private static final String CREATE_STUDENTS = """
            CREATE TABLE IF NOT EXISTS students (
                student_id        VARCHAR(20)  PRIMARY KEY,
                name              VARCHAR(100) NOT NULL,
                email             VARCHAR(150) NOT NULL UNIQUE,
                course_name       VARCHAR(100) NOT NULL,
                programming_score DOUBLE       NOT NULL CHECK (programming_score BETWEEN 0 AND 100),
                mathematics_score DOUBLE       NOT NULL CHECK (mathematics_score BETWEEN 0 AND 100)
            )
            """;

    private static final String CREATE_SKILLS = """
            CREATE TABLE IF NOT EXISTS student_skills (
                student_id VARCHAR(20)  NOT NULL,
                skill      VARCHAR(100) NOT NULL,
                FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
            )
            """;

    // ---------- DML ----------
    private static final String COUNT_STUDENTS = "SELECT COUNT(*) FROM students";

    private static final String INSERT_STUDENT =
            "INSERT INTO students (student_id, name, email, course_name, programming_score, mathematics_score) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String INSERT_SKILL =
            "INSERT INTO student_skills (student_id, skill) VALUES (?, ?)";

    private static final Object[][] STUDENTS = {
            {"S001", "Aditya Mehta",  "aditya@mail.com", "CSE",    90.0, 85.0},
            {"S002", "Priya Nair",    "priya@mail.com",  "CSE AI", 42.0, 88.0},
            {"S003", "Karan Malhotra","karan@mail.com",  "IT",     65.0, 58.0},
            {"S004", "Sneha Iyer",    "sneha@mail.com",  "CSE",    35.0, 45.0},
            {"S005", "Vikram Joshi",  "vikram@mail.com", "IT",     80.0, 92.0}
    };

    // {student id, skill}
    private static final String[][] SKILLS = {
            {"S001", "Java"}, {"S001", "Spring Boot"},
            {"S002", "Python"}, {"S002", "Java"},
            {"S003", "HTML"}, {"S003", "Python"},
            {"S004", "C++"},
            {"S005", "Java"}, {"S005", "Spring Boot"}, {"S005", "MySQL"}
    };

    private final DataSource dataSource;

    public JdbcDatabaseInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) {
        // try-with-resources closes the connection automatically
        try (Connection con = dataSource.getConnection()) {
            createTables(con);
            seedData(con);
        } catch (SQLException e) {
            throw new IllegalStateException("JDBC database initialization failed: " + e.getMessage(), e);
        }
    }

    /** Creates both tables using a plain Statement. */
    private void createTables(Connection con) throws SQLException {
        try (Statement st = con.createStatement()) {
            st.executeUpdate(CREATE_STUDENTS);
            st.executeUpdate(CREATE_SKILLS);
        }
        System.out.println("[JDBC] Tables students and student_skills are ready.");
    }

    /** Inserts default data only when the students table is empty. */
    private void seedData(Connection con) throws SQLException {
        if (countStudents(con) > 0) {
            System.out.println("[JDBC] Seed skipped - students table already has data.");
            return;
        }

        con.setAutoCommit(false);                       // run all inserts as one transaction
        try (PreparedStatement studentPs = con.prepareStatement(INSERT_STUDENT);
             PreparedStatement skillPs = con.prepareStatement(INSERT_SKILL)) {

            for (Object[] s : STUDENTS) {
                studentPs.setString(1, (String) s[0]);
                studentPs.setString(2, (String) s[1]);
                studentPs.setString(3, (String) s[2]);
                studentPs.setString(4, (String) s[3]);
                studentPs.setDouble(5, (Double) s[4]);
                studentPs.setDouble(6, (Double) s[5]);
                studentPs.addBatch();
            }
            studentPs.executeBatch();

            for (String[] sk : SKILLS) {
                skillPs.setString(1, sk[0]);
                skillPs.setString(2, sk[1]);
                skillPs.addBatch();
            }
            skillPs.executeBatch();

            con.commit();
            System.out.println("[JDBC] Seeded " + STUDENTS.length + " students and "
                    + SKILLS.length + " skill rows.");
        } catch (SQLException e) {
            con.rollback();                             // undo everything if any insert fails
            throw e;
        } finally {
            con.setAutoCommit(true);
        }
    }

    private int countStudents(Connection con) throws SQLException {
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(COUNT_STUDENTS)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}
