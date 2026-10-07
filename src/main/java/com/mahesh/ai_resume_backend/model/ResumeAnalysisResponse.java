package com.mahesh.ai_resume_backend.model;


import java.util.List;

public class ResumeAnalysisResponse {

    private List<String> skills;
    private int skillCount;
    private String message;

    public ResumeAnalysisResponse(
            List<String> skills,
            int skillCount,
            String message) {

        this.skills = skills;
        this.skillCount = skillCount;
        this.message = message;
    }

    public List<String> getSkills() {
        return skills;
    }

    public int getSkillCount() {
        return skillCount;
    }

    public String getMessage() {
        return message;
    }
}