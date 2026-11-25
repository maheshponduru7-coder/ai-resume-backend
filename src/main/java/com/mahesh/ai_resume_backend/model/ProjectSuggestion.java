package com.mahesh.ai_resume_backend.model;

import java.util.ArrayList;
import java.util.List;

public class ProjectSuggestion {

    private String title;
    private String description;
    private String difficulty; // Beginner, Intermediate, Advanced
    private List<String> targetedSkills = new ArrayList<>();
    private List<String> keyFeatures = new ArrayList<>();

    public ProjectSuggestion() {
    }

    public ProjectSuggestion(String title, String description, String difficulty,
                             List<String> targetedSkills, List<String> keyFeatures) {
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        this.targetedSkills = targetedSkills != null ? targetedSkills : new ArrayList<>();
        this.keyFeatures = keyFeatures != null ? keyFeatures : new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public List<String> getTargetedSkills() {
        return targetedSkills;
    }

    public void setTargetedSkills(List<String> targetedSkills) {
        this.targetedSkills = targetedSkills;
    }

    public List<String> getKeyFeatures() {
        return keyFeatures;
    }

    public void setKeyFeatures(List<String> keyFeatures) {
        this.keyFeatures = keyFeatures;
    }
}
