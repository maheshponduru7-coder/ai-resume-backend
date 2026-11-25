package com.mahesh.ai_resume_backend.model;

import java.util.ArrayList;
import java.util.List;

public class SkillGapResponse {

    private String targetRole;
    private String roleDescription;
    private int coveragePercentage;
    private String readinessLevel;
    private int totalRequiredSkills;
    private int matchedCount;
    private int partialCount;
    private int missingCount;

    private List<String> candidateSkills = new ArrayList<>();
    private List<SkillItem> matchedSkills = new ArrayList<>();
    private List<SkillItem> partiallyCoveredSkills = new ArrayList<>();
    private List<SkillItem> missingSkills = new ArrayList<>();

    private List<RoadmapPhase> roadmapPhases = new ArrayList<>();
    private List<ProjectSuggestion> projectSuggestions = new ArrayList<>();
    private List<RoleGapComparison> roleComparisons = new ArrayList<>();
    private List<String> availableRoles = new ArrayList<>();

    private String advice;
    private String disclaimer;

    public SkillGapResponse() {
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public String getRoleDescription() {
        return roleDescription;
    }

    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
    }

    public int getCoveragePercentage() {
        return coveragePercentage;
    }

    public void setCoveragePercentage(int coveragePercentage) {
        this.coveragePercentage = coveragePercentage;
    }

    public String getReadinessLevel() {
        return readinessLevel;
    }

    public void setReadinessLevel(String readinessLevel) {
        this.readinessLevel = readinessLevel;
    }

    public int getTotalRequiredSkills() {
        return totalRequiredSkills;
    }

    public void setTotalRequiredSkills(int totalRequiredSkills) {
        this.totalRequiredSkills = totalRequiredSkills;
    }

    public int getMatchedCount() {
        return matchedCount;
    }

    public void setMatchedCount(int matchedCount) {
        this.matchedCount = matchedCount;
    }

    public int getPartialCount() {
        return partialCount;
    }

    public void setPartialCount(int partialCount) {
        this.partialCount = partialCount;
    }

    public int getMissingCount() {
        return missingCount;
    }

    public void setMissingCount(int missingCount) {
        this.missingCount = missingCount;
    }

    public List<String> getCandidateSkills() {
        return candidateSkills;
    }

    public void setCandidateSkills(List<String> candidateSkills) {
        this.candidateSkills = candidateSkills;
    }

    public List<SkillItem> getMatchedSkills() {
        return matchedSkills;
    }

    public void setMatchedSkills(List<SkillItem> matchedSkills) {
        this.matchedSkills = matchedSkills;
    }

    public List<SkillItem> getPartiallyCoveredSkills() {
        return partiallyCoveredSkills;
    }

    public void setPartiallyCoveredSkills(List<SkillItem> partiallyCoveredSkills) {
        this.partiallyCoveredSkills = partiallyCoveredSkills;
    }

    public List<SkillItem> getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(List<SkillItem> missingSkills) {
        this.missingSkills = missingSkills;
    }

    public List<RoadmapPhase> getRoadmapPhases() {
        return roadmapPhases;
    }

    public void setRoadmapPhases(List<RoadmapPhase> roadmapPhases) {
        this.roadmapPhases = roadmapPhases;
    }

    public List<ProjectSuggestion> getProjectSuggestions() {
        return projectSuggestions;
    }

    public void setProjectSuggestions(List<ProjectSuggestion> projectSuggestions) {
        this.projectSuggestions = projectSuggestions;
    }

    public List<RoleGapComparison> getRoleComparisons() {
        return roleComparisons;
    }

    public void setRoleComparisons(List<RoleGapComparison> roleComparisons) {
        this.roleComparisons = roleComparisons;
    }

    public List<String> getAvailableRoles() {
        return availableRoles;
    }

    public void setAvailableRoles(List<String> availableRoles) {
        this.availableRoles = availableRoles;
    }

    public String getAdvice() {
        return advice;
    }

    public void setAdvice(String advice) {
        this.advice = advice;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }
}
