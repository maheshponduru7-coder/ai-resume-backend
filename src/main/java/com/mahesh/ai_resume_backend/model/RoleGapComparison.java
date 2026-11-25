package com.mahesh.ai_resume_backend.model;

public class RoleGapComparison {

    private String roleName;
    private int coveragePercentage;
    private int matchedCount;
    private int partialCount;
    private int missingCount;
    private String readinessLevel; // Job Ready, Near Ready, Developing, Needs Upskilling

    public RoleGapComparison() {
    }

    public RoleGapComparison(String roleName, int coveragePercentage, int matchedCount,
                             int partialCount, int missingCount, String readinessLevel) {
        this.roleName = roleName;
        this.coveragePercentage = coveragePercentage;
        this.matchedCount = matchedCount;
        this.partialCount = partialCount;
        this.missingCount = missingCount;
        this.readinessLevel = readinessLevel;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public int getCoveragePercentage() {
        return coveragePercentage;
    }

    public void setCoveragePercentage(int coveragePercentage) {
        this.coveragePercentage = coveragePercentage;
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

    public String getReadinessLevel() {
        return readinessLevel;
    }

    public void setReadinessLevel(String readinessLevel) {
        this.readinessLevel = readinessLevel;
    }
}
