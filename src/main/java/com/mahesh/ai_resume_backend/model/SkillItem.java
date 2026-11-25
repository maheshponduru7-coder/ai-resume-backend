package com.mahesh.ai_resume_backend.model;

public class SkillItem {

    private String name;
    private String status; // MATCHED, PARTIAL, MISSING
    private String priority; // HIGH, MEDIUM, LOW
    private String learningLevel; // BEGINNER, INTERMEDIATE, ADVANCED
    private String reason; // Explanation for partial coverage or importance
    private String estimatedTime; // e.g., "1–2 Weeks"

    public SkillItem() {
    }

    public SkillItem(String name, String status, String priority, String learningLevel, String reason, String estimatedTime) {
        this.name = name;
        this.status = status;
        this.priority = priority;
        this.learningLevel = learningLevel;
        this.reason = reason;
        this.estimatedTime = estimatedTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getLearningLevel() {
        return learningLevel;
    }

    public void setLearningLevel(String learningLevel) {
        this.learningLevel = learningLevel;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(String estimatedTime) {
        this.estimatedTime = estimatedTime;
    }
}
