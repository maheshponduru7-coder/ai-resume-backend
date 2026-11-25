package com.mahesh.ai_resume_backend.model;

import java.util.ArrayList;
import java.util.List;

public class JobRecommendationResponse {

    private List<String> candidateSkills = new ArrayList<>();
    private String detectedExperienceLevel;
    private String topRecommendedRole;
    private int topMatchPercentage;
    private List<JobRoleRecommendation> recommendations = new ArrayList<>();
    private String overallAdvice;
    private String disclaimer;

    public JobRecommendationResponse() {
    }

    public List<String> getCandidateSkills() {
        return candidateSkills;
    }

    public void setCandidateSkills(List<String> candidateSkills) {
        this.candidateSkills = candidateSkills;
    }

    public String getDetectedExperienceLevel() {
        return detectedExperienceLevel;
    }

    public void setDetectedExperienceLevel(String detectedExperienceLevel) {
        this.detectedExperienceLevel = detectedExperienceLevel;
    }

    public String getTopRecommendedRole() {
        return topRecommendedRole;
    }

    public void setTopRecommendedRole(String topRecommendedRole) {
        this.topRecommendedRole = topRecommendedRole;
    }

    public int getTopMatchPercentage() {
        return topMatchPercentage;
    }

    public void setTopMatchPercentage(int topMatchPercentage) {
        this.topMatchPercentage = topMatchPercentage;
    }

    public List<JobRoleRecommendation> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<JobRoleRecommendation> recommendations) {
        this.recommendations = recommendations;
    }

    public String getOverallAdvice() {
        return overallAdvice;
    }

    public void setOverallAdvice(String overallAdvice) {
        this.overallAdvice = overallAdvice;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }
}
