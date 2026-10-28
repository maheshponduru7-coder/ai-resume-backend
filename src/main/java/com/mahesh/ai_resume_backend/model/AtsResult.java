package com.mahesh.ai_resume_backend.model;

import java.util.List;
import java.util.Map;

public class AtsResult {

    private int score;
    private String rating;
    private String mode;
    private String modeLabel;
    private int wordCount;
    private Map<String, CategoryScore> categoryScores;
    private List<String> detectedSections;
    private List<String> missingSections;
    private Map<String, Object> contactDetails;
    private List<String> matchingKeywords;
    private List<String> missingKeywords;
    private List<String> jobKeywords;
    private List<String> formattingConcerns;
    private List<String> suggestions;
    private String disclaimer;

    public AtsResult() {
    }

    public static class CategoryScore {
        private int score;
        private int maxScore;
        private String feedback;

        public CategoryScore() {
        }

        public CategoryScore(int score, int maxScore, String feedback) {
            this.score = score;
            this.maxScore = maxScore;
            this.feedback = feedback;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public int getMaxScore() {
            return maxScore;
        }

        public void setMaxScore(int maxScore) {
            this.maxScore = maxScore;
        }

        public String getFeedback() {
            return feedback;
        }

        public void setFeedback(String feedback) {
            this.feedback = feedback;
        }
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
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

    public int getWordCount() {
        return wordCount;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }

    public Map<String, CategoryScore> getCategoryScores() {
        return categoryScores;
    }

    public void setCategoryScores(Map<String, CategoryScore> categoryScores) {
        this.categoryScores = categoryScores;
    }

    public List<String> getDetectedSections() {
        return detectedSections;
    }

    public void setDetectedSections(List<String> detectedSections) {
        this.detectedSections = detectedSections;
    }

    public List<String> getMissingSections() {
        return missingSections;
    }

    public void setMissingSections(List<String> missingSections) {
        this.missingSections = missingSections;
    }

    public Map<String, Object> getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(Map<String, Object> contactDetails) {
        this.contactDetails = contactDetails;
    }

    public List<String> getMatchingKeywords() {
        return matchingKeywords;
    }

    public void setMatchingKeywords(List<String> matchingKeywords) {
        this.matchingKeywords = matchingKeywords;
    }

    public List<String> getMissingKeywords() {
        return missingKeywords;
    }

    public void setMissingKeywords(List<String> missingKeywords) {
        this.missingKeywords = missingKeywords;
    }

    public List<String> getJobKeywords() {
        return jobKeywords;
    }

    public void setJobKeywords(List<String> jobKeywords) {
        this.jobKeywords = jobKeywords;
    }

    public List<String> getFormattingConcerns() {
        return formattingConcerns;
    }

    public void setFormattingConcerns(List<String> formattingConcerns) {
        this.formattingConcerns = formattingConcerns;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }
}
