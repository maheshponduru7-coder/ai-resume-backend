package com.mahesh.ai_resume_backend.model;

import java.util.ArrayList;
import java.util.List;

public class JobRoleRecommendation {

    private String roleName;
    private int matchPercentage;
    private String fitLevel;
    private String description;
    private List<String> matchingSkills = new ArrayList<>();
    private List<String> missingSkills = new ArrayList<>();
    private List<String> requiredSkills = new ArrayList<>();
    private int readinessScore;
    private List<String> keyStrengths = new ArrayList<>();
    private List<String> actionableSuggestions = new ArrayList<>();

    public JobRoleRecommendation() {
    }

    public JobRoleRecommendation(String roleName, int matchPercentage, String fitLevel, String description) {
        this.roleName = roleName;
        this.matchPercentage = matchPercentage;
        this.fitLevel = fitLevel;
        this.description = description;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public int getMatchPercentage() {
        return matchPercentage;
    }

    public void setMatchPercentage(int matchPercentage) {
        this.matchPercentage = matchPercentage;
    }

    public String getFitLevel() {
        return fitLevel;
    }

    public void setFitLevel(String fitLevel) {
        this.fitLevel = fitLevel;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getMatchingSkills() {
        return matchingSkills;
    }

    public void setMatchingSkills(List<String> matchingSkills) {
        this.matchingSkills = matchingSkills;
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(List<String> missingSkills) {
        this.missingSkills = missingSkills;
    }

    public List<String> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<String> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public int getReadinessScore() {
        return readinessScore;
    }

    public void setReadinessScore(int readinessScore) {
        this.readinessScore = readinessScore;
    }

    public List<String> getKeyStrengths() {
        return keyStrengths;
    }

    public void setKeyStrengths(List<String> keyStrengths) {
        this.keyStrengths = keyStrengths;
    }

    public List<String> getActionableSuggestions() {
        return actionableSuggestions;
    }

    public void setActionableSuggestions(List<String> actionableSuggestions) {
        this.actionableSuggestions = actionableSuggestions;
    }
}
