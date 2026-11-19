package com.example.studentsystem.service;

import com.example.studentsystem.dto.StudentUpdateRequest;
import com.example.studentsystem.exception.DuplicateStudentException;
import com.example.studentsystem.exception.InvalidScoreException;
import com.example.studentsystem.exception.StudentNotFoundException;
import com.example.studentsystem.model.Student;
import com.example.studentsystem.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student addStudent(Student student) {
        if (studentRepository.existsById(student.getStudentId())) {
            throw new DuplicateStudentException("Student ID already exists: " + student.getStudentId());
        }
        if (studentRepository.existsByEmailIgnoreCase(student.getEmail())) {
            throw new DuplicateStudentException("Email already registered: " + student.getEmail());
        }
        validateScore(student.getProgrammingScore(), "Programming");
        validateScore(student.getMathematicsScore(), "Mathematics");
        student.setTechnicalSkills(cleanSkills(student.getTechnicalSkills()));
        return studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(String id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
    }

    public Student updateScoresAndSkills(String id, StudentUpdateRequest request) {
        Student student = getStudentById(id);

        if (request.getProgrammingScore() != null) {
            validateScore(request.getProgrammingScore(), "Programming");
            student.setProgrammingScore(request.getProgrammingScore());
        }
        if (request.getMathematicsScore() != null) {
            validateScore(request.getMathematicsScore(), "Mathematics");
            student.setMathematicsScore(request.getMathematicsScore());
        }
        if (request.getTechnicalSkills() != null) {
            student.setTechnicalSkills(cleanSkills(request.getTechnicalSkills()));
        }
        return studentRepository.save(student);
    }

    public void deleteStudent(String id) {
        Student student = getStudentById(id);   // throws 404 if missing
        studentRepository.delete(student);
    }

    public List<Student> search(String id, String email, String course) {
        Map<String, Student> results = new LinkedHashMap<>();

        if (id != null && !id.isBlank()) {
            studentRepository.findById(id.trim())
                    .ifPresent(s -> results.put(s.getStudentId(), s));
        }
        if (email != null && !email.isBlank()) {
            studentRepository.findByEmailIgnoreCase(email.trim())
                    .ifPresent(s -> results.put(s.getStudentId(), s));
        }
        if (course != null && !course.isBlank()) {
            for (Student s : studentRepository.findByCourseNameIgnoreCase(course.trim())) {
                results.put(s.getStudentId(), s);
            }
        }
        return new ArrayList<>(results.values());
    }

    public List<Student> getMeritList() {
        List<Student> students = new ArrayList<>(studentRepository.findAll());
        students.sort(Comparator.comparingDouble(Student::getAverageScore).reversed()
                .thenComparing(Student::getName));      // tie-breaker: name A-Z
        return students;
    }
    public Map<String, List<String>> getStudentsWithSameSkills() {
        Map<String, List<String>> skillMap = new TreeMap<>();

        for (Student s : studentRepository.findAll()) {
            for (String skill : s.getTechnicalSkills()) {
                skillMap.computeIfAbsent(skill.toLowerCase(), k -> new ArrayList<>())
                        .add(s.getName() + " (" + s.getStudentId() + ")");
            }
        }

        // keep only the skills shared by 2 or more students
        return skillMap.entrySet().stream()
                .filter(e -> e.getValue().size() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (a, b) -> a, TreeMap::new));
    }

    public List<Student> getNeedsImprovementInProgramming() {
        List<Student> result = new ArrayList<>();
        for (Student s : studentRepository.findAll()) {
            if (s.getProgrammingScore() < 50 && s.getMathematicsScore() >= 75) {
                result.add(s);
            }
        }
        return result;
    }


    private void validateScore(Double score, String subject) {
        if (score == null || score < 0 || score > 100) {
            throw new InvalidScoreException(subject + " score must be between 0 and 100");
        }
    }

    /** Trims skills, drops blanks and removes duplicates while preserving order. */
    private List<String> cleanSkills(List<String> skills) {
        if (skills == null) {
            return new ArrayList<>();
        }
        Set<String> unique = new LinkedHashSet<>();
        for (String skill : skills) {
            if (skill != null && !skill.isBlank()) {
                unique.add(skill.trim());
            }
        }
        return new ArrayList<>(unique);
    }
}
