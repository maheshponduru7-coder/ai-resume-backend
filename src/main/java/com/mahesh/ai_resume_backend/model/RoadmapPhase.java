package com.mahesh.ai_resume_backend.model;

import java.util.ArrayList;
import java.util.List;

public class RoadmapPhase {

    private int phaseNumber;
    private String title;
    private String description;
    private String estimatedDuration;
    private List<String> targetSkills = new ArrayList<>();
    private List<String> actionItems = new ArrayList<>();

    public RoadmapPhase() {
    }

    public RoadmapPhase(int phaseNumber, String title, String description, String estimatedDuration,
                        List<String> targetSkills, List<String> actionItems) {
        this.phaseNumber = phaseNumber;
        this.title = title;
        this.description = description;
        this.estimatedDuration = estimatedDuration;
        this.targetSkills = targetSkills != null ? targetSkills : new ArrayList<>();
        this.actionItems = actionItems != null ? actionItems : new ArrayList<>();
    }

    public int getPhaseNumber() {
        return phaseNumber;
    }

    public void setPhaseNumber(int phaseNumber) {
        this.phaseNumber = phaseNumber;
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

    public String getEstimatedDuration() {
        return estimatedDuration;
    }

    public void setEstimatedDuration(String estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
    }

    public List<String> getTargetSkills() {
        return targetSkills;
    }

    public void setTargetSkills(List<String> targetSkills) {
        this.targetSkills = targetSkills;
    }

    public List<String> getActionItems() {
        return actionItems;
    }

    public void setActionItems(List<String> actionItems) {
        this.actionItems = actionItems;
    }
}
