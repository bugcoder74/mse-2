package com.example.studentsystem.repository;

import com.example.studentsystem.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {

    Optional<Student> findByEmailIgnoreCase(String email);

    List<Student> findByCourseNameIgnoreCase(String courseName);

    boolean existsByEmailIgnoreCase(String email);
}
