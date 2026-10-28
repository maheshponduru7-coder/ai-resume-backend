package com.mahesh.ai_resume_backend.model;

import java.util.List;

public class ResumeImprovementResponse {

    private String summary;
    private int overallHealthScore;
    private String mode;
    private String modeLabel;
    private List<String> strengths;
    private List<ImprovementCategory> categories;
    private List<String> priorityActionItems;
    private List<String> weakVerbsFound;
    private List<String> suggestedPowerVerbs;
    private boolean hasQuantifiableMetrics;
    private int metricsCount;
    private List<String> missingKeywords;
    private List<String> matchingKeywords;
    private String disclaimer;

    public ResumeImprovementResponse() {
    }

    public static class ImprovementCategory {
        private String id;
        private String title;
        private String priority; // "HIGH", "MEDIUM", "LOW"
        private String badge;
        private List<String> suggestions;
        private List<String> examples;

        public ImprovementCategory() {
        }

        public ImprovementCategory(String id, String title, String priority, String badge,
                                   List<String> suggestions, List<String> examples) {
            this.id = id;
            this.title = title;
            this.priority = priority;
            this.badge = badge;
            this.suggestions = suggestions;
            this.examples = examples;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPriority() {
            return priority;
        }

        public void setPriority(String priority) {
            this.priority = priority;
        }

        public String getBadge() {
            return badge;
        }

        public void setBadge(String badge) {
            this.badge = badge;
        }

        public List<String> getSuggestions() {
            return suggestions;
        }

        public void setSuggestions(List<String> suggestions) {
            this.suggestions = suggestions;
        }

        public List<String> getExamples() {
            return examples;
        }

        public void setExamples(List<String> examples) {
            this.examples = examples;
        }
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int getOverallHealthScore() {
        return overallHealthScore;
    }

    public void setOverallHealthScore(int overallHealthScore) {
        this.overallHealthScore = overallHealthScore;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getModeLabel() {
        return modeLabel;
    }

    public void setModeLabel(String modeLabel) {
        this.modeLabel = modeLabel;
    }

    public List<String> getStrengths() {
        return strengths;
    }

    public void setStrengths(List<String> strengths) {
        this.strengths = strengths;
    }

    public List<ImprovementCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<ImprovementCategory> categories) {
        this.categories = categories;
    }

    public List<String> getPriorityActionItems() {
        return priorityActionItems;
    }

    public void setPriorityActionItems(List<String> priorityActionItems) {
        this.priorityActionItems = priorityActionItems;
    }

    public List<String> getWeakVerbsFound() {
        return weakVerbsFound;
    }

    public void setWeakVerbsFound(List<String> weakVerbsFound) {
        this.weakVerbsFound = weakVerbsFound;
    }

    public List<String> getSuggestedPowerVerbs() {
        return suggestedPowerVerbs;
    }

    public void setSuggestedPowerVerbs(List<String> suggestedPowerVerbs) {
        this.suggestedPowerVerbs = suggestedPowerVerbs;
    }

    public boolean isHasQuantifiableMetrics() {
        return hasQuantifiableMetrics;
    }

    public void setHasQuantifiableMetrics(boolean hasQuantifiableMetrics) {
        this.hasQuantifiableMetrics = hasQuantifiableMetrics;
    }

    public int getMetricsCount() {
        return metricsCount;
    }

    public void setMetricsCount(int metricsCount) {
        this.metricsCount = metricsCount;
    }

    public List<String> getMissingKeywords() {
        return missingKeywords;
    }

    public void setMissingKeywords(List<String> missingKeywords) {
        this.missingKeywords = missingKeywords;
    }

    public List<String> getMatchingKeywords() {
        return matchingKeywords;
    }

    public void setMatchingKeywords(List<String> matchingKeywords) {
        this.matchingKeywords = matchingKeywords;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }
}
