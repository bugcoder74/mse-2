package com.example.studentsystem.exception;

public class StudentNotFoundException extends RuntimeException {
    public StudentNotFoundException(String id) {
        super("Student not found with ID: " + id);
    }
}
