package com.example.studentsystem.dto;

import java.util.List;

/** Request body for updating scores and skills. Fields left null are not changed. */
public class StudentUpdateRequest {

    private Double programmingScore;
    private Double mathematicsScore;
    private List<String> technicalSkills;

    public Double getProgrammingScore() { return programmingScore; }
    public void setProgrammingScore(Double programmingScore) { this.programmingScore = programmingScore; }

    public Double getMathematicsScore() { return mathematicsScore; }
    public void setMathematicsScore(Double mathematicsScore) { this.mathematicsScore = mathematicsScore; }

    public List<String> getTechnicalSkills() { return technicalSkills; }
    public void setTechnicalSkills(List<String> technicalSkills) { this.technicalSkills = technicalSkills; }
}
