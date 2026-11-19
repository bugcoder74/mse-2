# Student Profile and Skill Development Management System

Spring Boot 3 + Spring Data JPA + MySQL (Maven, Java 17)

## Run in IntelliJ
1. File > Open > select this folder (pom.xml). Wait for Maven to import.
2. Set Project SDK to JDK 17 or higher.
3. Edit `src/main/resources/application.properties` -> your MySQL username/password.
4. Run `StudentSystemApplication`. The `student_db` database and tables are created automatically.

## Package layout
- model       -> Student entity (Q1)
- repository  -> StudentRepository (Spring Data JPA)
- service     -> StudentService (all business rules, collections)
- controller  -> StudentController (REST APIs)
- exception   -> custom exceptions + global handler
- dto         -> StudentUpdateRequest

## API summary
| Method | URL | Purpose |
|--------|-----|---------|
| POST   | /api/students | Add student |
| GET    | /api/students | All students |
| GET    | /api/students/{id} | Find by ID |
| PUT    | /api/students/{id} | Update scores / skills |
| DELETE | /api/students/{id} | Delete |
| GET    | /api/students/merit-list | Merit list (avg desc) |
| GET    | /api/students/search?id=&email=&course= | Search |
| GET    | /api/students/same-skills | Students sharing a skill |
| GET    | /api/students/needs-improvement | Programming < 50 and Maths >= 75 |

## Sample POST body
{
  "studentId": "S106",
  "name": "Ananya Rao",
  "email": "ananya@mail.com",
  "courseName": "CSE",
  "programmingScore": 88,
  "mathematicsScore": 76,
  "technicalSkills": ["Java", "MySQL"]
}

## Sample PUT body
{ "programmingScore": 70, "technicalSkills": ["Java", "Docker"] }
