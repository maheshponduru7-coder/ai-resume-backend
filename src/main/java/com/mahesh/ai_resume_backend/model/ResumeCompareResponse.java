package com.mahesh.ai_resume_backend.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ResumeCompareResponse {

    private String oldResumeName;
    private String newResumeName;
    private String mode;
    private String modeLabel;
    private boolean hasJobDescription;

    private String verdict;
    private String summary;

    // Scores & Deltas
    private int oldAtsScore;
    private int newAtsScore;
    private int atsScoreDelta;

    private int oldHealthScore;
    private int newHealthScore;
    private int healthScoreDelta;

    private String oldRating;
    private String newRating;

    private int oldWordCount;
    private int newWordCount;
    private int wordCountDelta;

    // Category Deltas
    private Map<String, CategoryDelta> categoryDeltas = new LinkedHashMap<>();

    // Skills
    private List<String> oldSkills = new ArrayList<>();
    private List<String> newSkills = new ArrayList<>();
    private List<String> addedSkills = new ArrayList<>();
    private List<String> removedSkills = new ArrayList<>();
    private List<String> retainedSkills = new ArrayList<>();

    // Sections
    private List<String> oldDetectedSections = new ArrayList<>();
    private List<String> newDetectedSections = new ArrayList<>();
    private List<String> newlyDetectedSections = new ArrayList<>();
    private List<String> missingSectionsResolved = new ArrayList<>();
    private List<String> remainingMissingSections = new ArrayList<>();

    // Job Keywords
    private List<String> targetJobKeywords = new ArrayList<>();
    private List<String> oldMatchingKeywords = new ArrayList<>();
    private List<String> newMatchingKeywords = new ArrayList<>();
    private List<String> newlyMatchedKeywords = new ArrayList<>();
    private List<String> stillMissingKeywords = new ArrayList<>();
    private int oldMatchPercentage;
    private int newMatchPercentage;
    private int matchPercentageDelta;

    // Verbs & Metrics
    private int oldWeakVerbsCount;
    private int newWeakVerbsCount;
    private List<String> weakVerbsResolved = new ArrayList<>();
    private List<String> remainingWeakVerbs = new ArrayList<>();
    private int oldMetricsCount;
    private int newMetricsCount;
    private int metricsDelta;

    // Actionable Takeaways
    private List<String> keyImprovements = new ArrayList<>();
    private List<String> actionableSuggestions = new ArrayList<>();
    private String disclaimer;

    public ResumeCompareResponse() {
    }

    public static class CategoryDelta {
        private int oldScore;
        private int newScore;
        private int delta;
        private int maxScore;

        public CategoryDelta() {
        }

        public CategoryDelta(int oldScore, int newScore, int maxScore) {
            this.oldScore = oldScore;
            this.newScore = newScore;
            this.delta = newScore - oldScore;
            this.maxScore = maxScore;
        }

        public int getOldScore() {
            return oldScore;
        }

        public void setOldScore(int oldScore) {
            this.oldScore = oldScore;
        }

        public int getNewScore() {
            return newScore;
        }

        public void setNewScore(int newScore) {
            this.newScore = newScore;
        }

        public int getDelta() {
            return delta;
        }

        public void setDelta(int delta) {
            this.delta = delta;
        }

        public int getMaxScore() {
            return maxScore;
        }

        public void setMaxScore(int maxScore) {
            this.maxScore = maxScore;
        }
    }

    // Getters and Setters

    public String getOldResumeName() {
        return oldResumeName;
    }

    public void setOldResumeName(String oldResumeName) {
        this.oldResumeName = oldResumeName;
    }

    public String getNewResumeName() {
        return newResumeName;
    }

    public void setNewResumeName(String newResumeName) {
        this.newResumeName = newResumeName;
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

    public boolean isHasJobDescription() {
        return hasJobDescription;
    }

    public void setHasJobDescription(boolean hasJobDescription) {
        this.hasJobDescription = hasJobDescription;
    }

    public String getVerdict() {
        return verdict;
    }

    public void setVerdict(String verdict) {
        this.verdict = verdict;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int getOldAtsScore() {
        return oldAtsScore;
    }

    public void setOldAtsScore(int oldAtsScore) {
        this.oldAtsScore = oldAtsScore;
    }

    public int getNewAtsScore() {
        return newAtsScore;
    }

    public void setNewAtsScore(int newAtsScore) {
        this.newAtsScore = newAtsScore;
    }

    public int getAtsScoreDelta() {
        return atsScoreDelta;
    }

    public void setAtsScoreDelta(int atsScoreDelta) {
        this.atsScoreDelta = atsScoreDelta;
    }

    public int getOldHealthScore() {
        return oldHealthScore;
    }

    public void setOldHealthScore(int oldHealthScore) {
        this.oldHealthScore = oldHealthScore;
    }

    public int getNewHealthScore() {
        return newHealthScore;
    }

    public void setNewHealthScore(int newHealthScore) {
        this.newHealthScore = newHealthScore;
    }

    public int getHealthScoreDelta() {
        return healthScoreDelta;
    }

    public void setHealthScoreDelta(int healthScoreDelta) {
        this.healthScoreDelta = healthScoreDelta;
    }

    public String getOldRating() {
        return oldRating;
    }

    public void setOldRating(String oldRating) {
        this.oldRating = oldRating;
    }

    public String getNewRating() {
        return newRating;
    }

    public void setNewRating(String newRating) {
        this.newRating = newRating;
    }

    public int getOldWordCount() {
        return oldWordCount;
    }

    public void setOldWordCount(int oldWordCount) {
        this.oldWordCount = oldWordCount;
    }

    public int getNewWordCount() {
        return newWordCount;
    }

    public void setNewWordCount(int newWordCount) {
        this.newWordCount = newWordCount;
    }

    public int getWordCountDelta() {
        return wordCountDelta;
    }

    public void setWordCountDelta(int wordCountDelta) {
        this.wordCountDelta = wordCountDelta;
    }

    public Map<String, CategoryDelta> getCategoryDeltas() {
        return categoryDeltas;
    }

    public void setCategoryDeltas(Map<String, CategoryDelta> categoryDeltas) {
        this.categoryDeltas = categoryDeltas;
    }

    public List<String> getOldSkills() {
        return oldSkills;
    }

    public void setOldSkills(List<String> oldSkills) {
        this.oldSkills = oldSkills;
    }

    public List<String> getNewSkills() {
        return newSkills;
    }

    public void setNewSkills(List<String> newSkills) {
        this.newSkills = newSkills;
    }

    public List<String> getAddedSkills() {
        return addedSkills;
    }

    public void setAddedSkills(List<String> addedSkills) {
        this.addedSkills = addedSkills;
    }

    public List<String> getRemovedSkills() {
        return removedSkills;
    }

    public void setRemovedSkills(List<String> removedSkills) {
        this.removedSkills = removedSkills;
    }

    public List<String> getRetainedSkills() {
        return retainedSkills;
    }

    public void setRetainedSkills(List<String> retainedSkills) {
        this.retainedSkills = retainedSkills;
    }

    public List<String> getOldDetectedSections() {
        return oldDetectedSections;
    }

    public void setOldDetectedSections(List<String> oldDetectedSections) {
        this.oldDetectedSections = oldDetectedSections;
    }

    public List<String> getNewDetectedSections() {
        return newDetectedSections;
    }

    public void setNewDetectedSections(List<String> newDetectedSections) {
        this.newDetectedSections = newDetectedSections;
    }

    public List<String> getNewlyDetectedSections() {
        return newlyDetectedSections;
    }

    public void setNewlyDetectedSections(List<String> newlyDetectedSections) {
        this.newlyDetectedSections = newlyDetectedSections;
    }

    public List<String> getMissingSectionsResolved() {
        return missingSectionsResolved;
    }

    public void setMissingSectionsResolved(List<String> missingSectionsResolved) {
        this.missingSectionsResolved = missingSectionsResolved;
    }

    public List<String> getRemainingMissingSections() {
        return remainingMissingSections;
    }

    public void setRemainingMissingSections(List<String> remainingMissingSections) {
        this.remainingMissingSections = remainingMissingSections;
    }

    public List<String> getTargetJobKeywords() {
        return targetJobKeywords;
    }

    public void setTargetJobKeywords(List<String> targetJobKeywords) {
        this.targetJobKeywords = targetJobKeywords;
    }

    public List<String> getOldMatchingKeywords() {
        return oldMatchingKeywords;
    }

    public void setOldMatchingKeywords(List<String> oldMatchingKeywords) {
        this.oldMatchingKeywords = oldMatchingKeywords;
    }

    public List<String> getNewMatchingKeywords() {
        return newMatchingKeywords;
    }

    public void setNewMatchingKeywords(List<String> newMatchingKeywords) {
        this.newMatchingKeywords = newMatchingKeywords;
    }

    public List<String> getNewlyMatchedKeywords() {
        return newlyMatchedKeywords;
    }

    public void setNewlyMatchedKeywords(List<String> newlyMatchedKeywords) {
        this.newlyMatchedKeywords = newlyMatchedKeywords;
    }

    public List<String> getStillMissingKeywords() {
        return stillMissingKeywords;
    }

    public void setStillMissingKeywords(List<String> stillMissingKeywords) {
        this.stillMissingKeywords = stillMissingKeywords;
    }

    public int getOldMatchPercentage() {
        return oldMatchPercentage;
    }

    public void setOldMatchPercentage(int oldMatchPercentage) {
        this.oldMatchPercentage = oldMatchPercentage;
    }

    public int getNewMatchPercentage() {
        return newMatchPercentage;
    }

    public void setNewMatchPercentage(int newMatchPercentage) {
        this.newMatchPercentage = newMatchPercentage;
    }

    public int getMatchPercentageDelta() {
        return matchPercentageDelta;
    }

    public void setMatchPercentageDelta(int matchPercentageDelta) {
        this.matchPercentageDelta = matchPercentageDelta;
    }

    public int getOldWeakVerbsCount() {
        return oldWeakVerbsCount;
    }

    public void setOldWeakVerbsCount(int oldWeakVerbsCount) {
        this.oldWeakVerbsCount = oldWeakVerbsCount;
    }

    public int getNewWeakVerbsCount() {
        return newWeakVerbsCount;
    }

    public void setNewWeakVerbsCount(int newWeakVerbsCount) {
        this.newWeakVerbsCount = newWeakVerbsCount;
    }

    public List<String> getWeakVerbsResolved() {
        return weakVerbsResolved;
    }

    public void setWeakVerbsResolved(List<String> weakVerbsResolved) {
        this.weakVerbsResolved = weakVerbsResolved;
    }

    public List<String> getRemainingWeakVerbs() {
        return remainingWeakVerbs;
    }

    public void setRemainingWeakVerbs(List<String> remainingWeakVerbs) {
        this.remainingWeakVerbs = remainingWeakVerbs;
    }

    public int getOldMetricsCount() {
        return oldMetricsCount;
    }

    public void setOldMetricsCount(int oldMetricsCount) {
        this.oldMetricsCount = oldMetricsCount;
    }

    public int getNewMetricsCount() {
        return newMetricsCount;
    }

    public void setNewMetricsCount(int newMetricsCount) {
        this.newMetricsCount = newMetricsCount;
    }

    public int getMetricsDelta() {
        return metricsDelta;
    }

    public void setMetricsDelta(int metricsDelta) {
        this.metricsDelta = metricsDelta;
    }

    public List<String> getKeyImprovements() {
        return keyImprovements;
    }

    public void setKeyImprovements(List<String> keyImprovements) {
        this.keyImprovements = keyImprovements;
    }

    public List<String> getActionableSuggestions() {
        return actionableSuggestions;
    }

    public void setActionableSuggestions(List<String> actionableSuggestions) {
        this.actionableSuggestions = actionableSuggestions;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }
}
