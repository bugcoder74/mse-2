package com.example.studentsystem.controller;

import com.example.studentsystem.dto.StudentUpdateRequest;
import com.example.studentsystem.model.Student;
import com.example.studentsystem.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // 1. Add a student
    @PostMapping
    public ResponseEntity<Student> addStudent(@Valid @RequestBody Student student) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.addStudent(student));
    }

    // 2. Retrieve all students
    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    // 3. Search a student by ID
    @GetMapping("/{id}")
    public Student getStudentById(@PathVariable String id) {
        return studentService.getStudentById(id);
    }

    // 4. Update scores and skills
    @PutMapping("/{id}")
    public Student updateStudent(@PathVariable String id, @RequestBody StudentUpdateRequest request) {
        return studentService.updateScoresAndSkills(id, request);
    }

    // 5. Delete a student record
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteStudent(@PathVariable String id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok(Map.of("message", "Student " + id + " deleted successfully"));
    }

    // 6. Merit list
    @GetMapping("/merit-list")
    public List<Student> getMeritList() {
        return studentService.getMeritList();
    }

    // ---------- Extra endpoints for Q2 requirements ----------

    // Search by id / email / course:  /api/students/search?email=...  or  ?course=...
    @GetMapping("/search")
    public List<Student> search(@RequestParam(required = false) String id,
                                @RequestParam(required = false) String email,
                                @RequestParam(required = false) String course) {
        return studentService.search(id, email, course);
    }

    // Students sharing the same technical skills
    @GetMapping("/same-skills")
    public Map<String, List<String>> sameSkills() {
        return studentService.getStudentsWithSameSkills();
    }

    // Weak in programming (<50) but maths >= 75
    @GetMapping("/needs-improvement")
    public List<Student> needsImprovement() {
        return studentService.getNeedsImprovementInProgramming();
    }
}
