package com.mahesh.ai_resume_backend.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.mahesh.ai_resume_backend.model.AtsResult;
import com.mahesh.ai_resume_backend.model.AtsResult.CategoryScore;
import com.mahesh.ai_resume_backend.model.ResumeCompareRequest;
import com.mahesh.ai_resume_backend.model.ResumeCompareResponse;
import com.mahesh.ai_resume_backend.model.ResumeCompareResponse.CategoryDelta;
import com.mahesh.ai_resume_backend.model.ResumeImprovementResponse;

@Service
public class ResumeCompareService {

    private final AtsService atsService;
    private final ResumeImprovementService resumeImprovementService;
    private final SkillService skillService;

    public ResumeCompareService(
            AtsService atsService,
            ResumeImprovementService resumeImprovementService,
            SkillService skillService) {
        this.atsService = atsService;
        this.resumeImprovementService = resumeImprovementService;
        this.skillService = skillService;
    }

    public ResumeCompareResponse compareResumes(ResumeCompareRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Comparison request cannot be null");
        }
        if (request.getOldResumeText() == null || request.getOldResumeText().trim().isEmpty()) {
            throw new IllegalArgumentException("Original (old) resume text cannot be empty");
        }
        if (request.getNewResumeText() == null || request.getNewResumeText().trim().isEmpty()) {
            throw new IllegalArgumentException("Updated (new) resume text cannot be empty");
        }

        String oldText = request.getOldResumeText().trim();
        String newText = request.getNewResumeText().trim();
        String jobDesc = request.getJobDescription() != null ? request.getJobDescription().trim() : "";
        boolean hasJobDescription = !jobDesc.isEmpty();

        String oldName = (request.getOldResumeName() != null && !request.getOldResumeName().isBlank())
                ? request.getOldResumeName() : "Original Resume";
        String newName = (request.getNewResumeName() != null && !request.getNewResumeName().isBlank())
                ? request.getNewResumeName() : "Updated Resume";

        // 1. Run ATS Analysis on both versions
        AtsResult oldAts = atsService.analyzeAts(oldText, jobDesc);
        AtsResult newAts = atsService.analyzeAts(newText, jobDesc);

        // 2. Run Resume Improvement Analysis on both versions
        ResumeImprovementResponse oldSuggestions = resumeImprovementService.generateSuggestions(oldText, jobDesc);
        ResumeImprovementResponse newSuggestions = resumeImprovementService.generateSuggestions(newText, jobDesc);

        // 3. Extract Skills from both versions
        List<String> oldSkills = skillService.detectSkills(oldText);
        List<String> newSkills = skillService.detectSkills(newText);

        ResumeCompareResponse response = new ResumeCompareResponse();
        response.setOldResumeName(oldName);
        response.setNewResumeName(newName);
        response.setHasJobDescription(hasJobDescription);
        response.setMode(hasJobDescription ? "TARGETED" : "GENERAL");
        response.setModeLabel(hasJobDescription
                ? "Targeted Comparison (Evaluated against Job Description)"
                : "General Quality Comparison (Standard ATS Benchmark)");

        // Scores & Deltas
        response.setOldAtsScore(oldAts.getScore());
        response.setNewAtsScore(newAts.getScore());
        response.setAtsScoreDelta(newAts.getScore() - oldAts.getScore());

        response.setOldHealthScore(oldSuggestions.getOverallHealthScore());
        response.setNewHealthScore(newSuggestions.getOverallHealthScore());
        response.setHealthScoreDelta(newSuggestions.getOverallHealthScore() - oldSuggestions.getOverallHealthScore());

        response.setOldRating(oldAts.getRating());
        response.setNewRating(newAts.getRating());

        response.setOldWordCount(oldAts.getWordCount());
        response.setNewWordCount(newAts.getWordCount());
        response.setWordCountDelta(newAts.getWordCount() - oldAts.getWordCount());

        // Category Score Deltas
        Map<String, CategoryDelta> categoryDeltas = new LinkedHashMap<>();
        Map<String, CategoryScore> oldCatScores = oldAts.getCategoryScores() != null ? oldAts.getCategoryScores() : Map.of();
        Map<String, CategoryScore> newCatScores = newAts.getCategoryScores() != null ? newAts.getCategoryScores() : Map.of();

        for (Map.Entry<String, CategoryScore> entry : newCatScores.entrySet()) {
            String key = entry.getKey();
            CategoryScore newCat = entry.getValue();
            CategoryScore oldCat = oldCatScores.get(key);
            int oldScore = oldCat != null ? oldCat.getScore() : 0;
            categoryDeltas.put(key, new CategoryDelta(oldScore, newCat.getScore(), newCat.getMaxScore()));
        }
        response.setCategoryDeltas(categoryDeltas);

        // Skills comparison
        response.setOldSkills(oldSkills);
        response.setNewSkills(newSkills);

        List<String> addedSkills = new ArrayList<>();
        List<String> removedSkills = new ArrayList<>();
        List<String> retainedSkills = new ArrayList<>();

        for (String skill : newSkills) {
            if (containsIgnoreCase(oldSkills, skill)) {
                retainedSkills.add(skill);
            } else {
                addedSkills.add(skill);
            }
        }
        for (String skill : oldSkills) {
            if (!containsIgnoreCase(newSkills, skill)) {
                removedSkills.add(skill);
            }
        }
        response.setAddedSkills(addedSkills);
        response.setRemovedSkills(removedSkills);
        response.setRetainedSkills(retainedSkills);

        // Sections comparison
        List<String> oldSections = oldAts.getDetectedSections() != null ? oldAts.getDetectedSections() : List.of();
        List<String> newSections = newAts.getDetectedSections() != null ? newAts.getDetectedSections() : List.of();
        response.setOldDetectedSections(oldSections);
        response.setNewDetectedSections(newSections);

        List<String> newlyDetectedSections = new ArrayList<>();
        for (String sec : newSections) {
            if (!containsIgnoreCase(oldSections, sec)) {
                newlyDetectedSections.add(sec);
            }
        }
        response.setNewlyDetectedSections(newlyDetectedSections);

        List<String> oldMissing = oldAts.getMissingSections() != null ? oldAts.getMissingSections() : List.of();
        List<String> newMissing = newAts.getMissingSections() != null ? newAts.getMissingSections() : List.of();
        List<String> resolvedMissing = new ArrayList<>();
        for (String missingSec : oldMissing) {
            if (!containsIgnoreCase(newMissing, missingSec)) {
                resolvedMissing.add(missingSec);
            }
        }
        response.setMissingSectionsResolved(resolvedMissing);
        response.setRemainingMissingSections(newMissing);

        // Job Keywords comparison (if job description provided)
        if (hasJobDescription) {
            List<String> jobKw = newAts.getJobKeywords() != null ? newAts.getJobKeywords() : List.of();
            response.setTargetJobKeywords(jobKw);

            List<String> oldMatch = oldAts.getMatchingKeywords() != null ? oldAts.getMatchingKeywords() : List.of();
            List<String> newMatch = newAts.getMatchingKeywords() != null ? newAts.getMatchingKeywords() : List.of();
            response.setOldMatchingKeywords(oldMatch);
            response.setNewMatchingKeywords(newMatch);

            List<String> newlyMatched = new ArrayList<>();
            for (String kw : newMatch) {
                if (!containsIgnoreCase(oldMatch, kw)) {
                    newlyMatched.add(kw);
                }
            }
            response.setNewlyMatchedKeywords(newlyMatched);
            response.setStillMissingKeywords(newAts.getMissingKeywords() != null ? newAts.getMissingKeywords() : List.of());

            int oldPct = jobKw.isEmpty() ? 0 : Math.round(((float) oldMatch.size() / jobKw.size()) * 100);
            int newPct = jobKw.isEmpty() ? 0 : Math.round(((float) newMatch.size() / jobKw.size()) * 100);
            response.setOldMatchPercentage(oldPct);
            response.setNewMatchPercentage(newPct);
            response.setMatchPercentageDelta(newPct - oldPct);
        }

        // Weak Verbs & Metrics comparison
        List<String> oldWeak = oldSuggestions.getWeakVerbsFound() != null ? oldSuggestions.getWeakVerbsFound() : List.of();
        List<String> newWeak = newSuggestions.getWeakVerbsFound() != null ? newSuggestions.getWeakVerbsFound() : List.of();
        response.setOldWeakVerbsCount(oldWeak.size());
        response.setNewWeakVerbsCount(newWeak.size());

        List<String> weakResolved = new ArrayList<>();
        for (String v : oldWeak) {
            if (!containsIgnoreCase(newWeak, v)) {
                weakResolved.add(v);
            }
        }
        response.setWeakVerbsResolved(weakResolved);
        response.setRemainingWeakVerbs(newWeak);

        response.setOldMetricsCount(oldSuggestions.getMetricsCount());
        response.setNewMetricsCount(newSuggestions.getMetricsCount());
        response.setMetricsDelta(newSuggestions.getMetricsCount() - oldSuggestions.getMetricsCount());

        // Generate Executive Verdict & Highlights
        int scoreDelta = response.getAtsScoreDelta();
        String verdict;
        if (scoreDelta >= 15) {
            verdict = "Significant Improvement (+" + scoreDelta + " pts ATS Score)";
        } else if (scoreDelta > 0) {
            verdict = "Noticeable Improvement (+" + scoreDelta + " pts ATS Score)";
        } else if (scoreDelta == 0) {
            if (!addedSkills.isEmpty() || !resolvedMissing.isEmpty() || !weakResolved.isEmpty()) {
                verdict = "Qualitative Improvement (Content refactored with new skills & improved verbs)";
            } else {
                verdict = "No Measurable Change (Both versions have equivalent scoring profile)";
            }
        } else {
            verdict = "Score Regression (" + scoreDelta + " pts ATS Score)";
        }
        response.setVerdict(verdict);

        // Summary statement
        StringBuilder summaryBuilder = new StringBuilder();
        if (scoreDelta > 0) {
            summaryBuilder.append("Your updated resume increased the ATS score from ")
                    .append(oldAts.getScore()).append(" to ").append(newAts.getScore())
                    .append(" (+").append(scoreDelta).append(" points). ");
        } else if (scoreDelta < 0) {
            summaryBuilder.append("Your updated resume scored ")
                    .append(Math.abs(scoreDelta)).append(" points lower than the original. ");
        } else {
            summaryBuilder.append("Both versions earned an ATS score of ").append(newAts.getScore()).append("/100. ");
        }

        if (!addedSkills.isEmpty()) {
            summaryBuilder.append(addedSkills.size()).append(" newly detected skill(s) were added. ");
        }
        if (!resolvedMissing.isEmpty()) {
            summaryBuilder.append("Critical missing sections (").append(String.join(", ", resolvedMissing)).append(") were successfully introduced. ");
        }
        if (!weakResolved.isEmpty()) {
            summaryBuilder.append(weakResolved.size()).append(" weak passive verb(s) were replaced with high-impact language. ");
        }
        response.setSummary(summaryBuilder.toString().trim());

        // Key Improvements list
        List<String> keyImprovements = new ArrayList<>();
        if (scoreDelta > 0) {
            keyImprovements.add("ATS Compatibility Score increased from " + oldAts.getScore() + " to " + newAts.getScore() + " (+" + scoreDelta + " pts).");
        }
        if (response.getHealthScoreDelta() > 0 && response.getHealthScoreDelta() != scoreDelta) {
            keyImprovements.add("Resume Health Score improved by +" + response.getHealthScoreDelta() + " pts.");
        }
        if (!addedSkills.isEmpty()) {
            keyImprovements.add("Added " + addedSkills.size() + " new skill(s): " + String.join(", ", addedSkills) + ".");
        }
        if (!newlyDetectedSections.isEmpty()) {
            keyImprovements.add("Introduced " + newlyDetectedSections.size() + " newly detected section(s): " + String.join(", ", newlyDetectedSections) + ".");
        }
        if (!resolvedMissing.isEmpty()) {
            keyImprovements.add("Resolved critical missing section warning(s): " + String.join(", ", resolvedMissing) + ".");
        }
        if (hasJobDescription && !response.getNewlyMatchedKeywords().isEmpty()) {
            keyImprovements.add("Matched " + response.getNewlyMatchedKeywords().size() + " additional target job keyword(s): " + String.join(", ", response.getNewlyMatchedKeywords()) + ".");
        }
        if (!weakResolved.isEmpty()) {
            keyImprovements.add("Eliminated " + weakResolved.size() + " passive phrase(s): " + String.join(", ", weakResolved) + ".");
        }
        if (response.getMetricsDelta() > 0) {
            keyImprovements.add("Quantified accomplishments: added " + response.getMetricsDelta() + " measurable metric(s).");
        }
        response.setKeyImprovements(keyImprovements);

        // Actionable Suggestions for v2
        List<String> actionableSuggestions = new ArrayList<>();
        if (!removedSkills.isEmpty()) {
            actionableSuggestions.add("Skills removed: " + String.join(", ", removedSkills) + " were present in your original resume but are absent here. Ensure this was intentional.");
        }
        if (!newMissing.isEmpty()) {
            actionableSuggestions.add("Missing sections: Still missing " + String.join(", ", newMissing) + ". Include standard section headers to maximize ATS score.");
        }
        if (hasJobDescription && !response.getStillMissingKeywords().isEmpty()) {
            List<String> missingSample = response.getStillMissingKeywords().subList(0, Math.min(6, response.getStillMissingKeywords().size()));
            actionableSuggestions.add("Job keyword gaps: Consider naturally incorporating missing target keywords such as " + String.join(", ", missingSample) + ".");
        }
        if (!newWeak.isEmpty()) {
            actionableSuggestions.add("Weak phrasing: Replace remaining passive verbs (" + String.join(", ", newWeak) + ") with action verbs like Architected, Engineered, or Automated.");
        }
        if (newSuggestions.getMetricsCount() == 0) {
            actionableSuggestions.add("Measurable impact: Incorporate numbers, percentages, or multipliers using the Google XYZ Formula ('Accomplished X as measured by Y, by doing Z').");
        }
        response.setActionableSuggestions(actionableSuggestions);

        response.setDisclaimer("Comparison results are generated using deterministic rule-based ATS evaluation algorithms. They provide guidance on relative improvement but do not guarantee employment decisions with specific employers.");

        return response;
    }

    private boolean containsIgnoreCase(List<String> list, String target) {
        if (list == null || target == null) return false;
        String lowerTarget = target.trim().toLowerCase(Locale.ROOT);
        for (String item : list) {
            if (item != null && item.trim().toLowerCase(Locale.ROOT).equals(lowerTarget)) {
                return true;
            }
        }
        return false;
    }
}
