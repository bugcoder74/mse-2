package com.example.studentsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Q1: Student entity - encapsulation (private fields + getters/setters),
 * constructors, and object methods (average, category, stronger subject).
 */
@Entity
@Table(name = "students")
public class Student {

    @Id
    @Column(name = "student_id", length = 20)
    @NotBlank(message = "Student ID is required")
    private String studentId;

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Course name is required")
    @Column(name = "course_name", nullable = false)
    private String courseName;

    @NotNull(message = "Programming score is required")
    @DecimalMin(value = "0.0", message = "Programming score must be between 0 and 100")
    @DecimalMax(value = "100.0", message = "Programming score must be between 0 and 100")
    @Column(name = "programming_score", nullable = false)
    private Double programmingScore;

    @NotNull(message = "Mathematics score is required")
    @DecimalMin(value = "0.0", message = "Mathematics score must be between 0 and 100")
    @DecimalMax(value = "100.0", message = "Mathematics score must be between 0 and 100")
    @Column(name = "mathematics_score", nullable = false)
    private Double mathematicsScore;

    // Stored in a separate table: student_skills(student_id, skill)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "student_skills", joinColumns = @JoinColumn(name = "student_id"))
    @Column(name = "skill")
    private List<String> technicalSkills = new ArrayList<>();

    // ---------- Constructors ----------
    public Student() {
    }

    public Student(String studentId, String name, String email, String courseName,
                   Double programmingScore, Double mathematicsScore, List<String> technicalSkills) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.courseName = courseName;
        this.programmingScore = programmingScore;
        this.mathematicsScore = mathematicsScore;
        this.technicalSkills = technicalSkills != null ? technicalSkills : new ArrayList<>();
    }

    // ---------- Object methods (business rules on the object itself) ----------

    /** Average of programming and mathematics scores. */
    public double getAverageScore() {
        if (programmingScore == null || mathematicsScore == null) {
            return 0.0;
        }
        return (programmingScore + mathematicsScore) / 2.0;
    }

    /** Performance category based on the average score. */
    public String getPerformanceCategory() {
        double avg = getAverageScore();
        if (avg >= 85) {
            return "Excellent";
        } else if (avg >= 70) {
            return "Good";
        } else if (avg >= 50) {
            return "Satisfactory";
        } else {
            return "Needs Improvement";
        }
    }

    /** Subject in which the student scored higher. */
    public String getStrongerSubject() {
        if (programmingScore == null || mathematicsScore == null) {
            return "N/A";
        }
        if (programmingScore > mathematicsScore) {
            return "Programming";
        } else if (mathematicsScore > programmingScore) {
            return "Mathematics";
        }
        return "Both are equal";
    }

    // ---------- Getters and Setters ----------
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public Double getProgrammingScore() { return programmingScore; }
    public void setProgrammingScore(Double programmingScore) { this.programmingScore = programmingScore; }

    public Double getMathematicsScore() { return mathematicsScore; }
    public void setMathematicsScore(Double mathematicsScore) { this.mathematicsScore = mathematicsScore; }

    public List<String> getTechnicalSkills() { return technicalSkills; }
    public void setTechnicalSkills(List<String> technicalSkills) {
        this.technicalSkills = technicalSkills != null ? technicalSkills : new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Student{id='" + studentId + "', name='" + name + "', course='" + courseName
                + "', avg=" + getAverageScore() + "}";
    }
}
