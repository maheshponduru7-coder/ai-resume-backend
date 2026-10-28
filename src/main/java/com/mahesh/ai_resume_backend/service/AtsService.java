package com.mahesh.ai_resume_backend.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.mahesh.ai_resume_backend.model.AtsResult;
import com.mahesh.ai_resume_backend.model.AtsResult.CategoryScore;

@Service
public class AtsService {

    private static final String[] MASTER_SKILLS = {
        "Java", "Python", "C++", "C#", "C", "JavaScript", "TypeScript", "React", "Angular", "Vue",
        "Next.js", "HTML", "CSS", "Tailwind", "Bootstrap", "Spring Boot", "Spring", "Node.js",
        "Express", "SQL", "MySQL", "PostgreSQL", "MongoDB", "Redis", "AWS", "Azure", "GCP",
        "Docker", "Kubernetes", "CI/CD", "Git", "GitHub", "Linux", "REST API", "GraphQL",
        "Microservices", "Data Structures", "Algorithms", "DSA", "Unit Testing", "Agile",
        "Scrum", "OOP", "Machine Learning", "Problem Solving"
    };

    private static final String[] ACTION_VERBS = {
        "developed", "designed", "implemented", "built", "created", "managed", "led", "architected",
        "optimized", "deployed", "engineered", "collaborated", "analyzed", "resolved", "tested",
        "maintained", "reduced", "increased", "integrated", "automated"
    };

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "(?i)[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "(?:\\+?\\d{1,3}[-.\\s]?)?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}|\\b\\d{10}\\b"
    );

    public AtsResult analyzeAts(String resumeText, String jobDescription) {
        if (resumeText == null || resumeText.trim().isEmpty()) {
            throw new IllegalArgumentException("Resume text cannot be empty");
        }

        String trimmed = resumeText.trim();
        String resumeLower = trimmed.toLowerCase(Locale.ROOT);
        int wordCount = trimmed.split("\\s+").length;

        boolean hasJobDescription = jobDescription != null && !jobDescription.trim().isEmpty();
        String jobLower = hasJobDescription ? jobDescription.trim().toLowerCase(Locale.ROOT) : "";

        // 1. Detect Sections
        List<String> detectedSections = new ArrayList<>();
        List<String> missingSections = new ArrayList<>();
        Map<String, Object> contactDetails = new HashMap<>();

        // Contact info detection
        Matcher emailMatcher = EMAIL_PATTERN.matcher(trimmed);
        boolean hasEmail = emailMatcher.find();
        String emailFound = hasEmail ? emailMatcher.group() : null;

        Matcher phoneMatcher = PHONE_PATTERN.matcher(trimmed);
        boolean hasPhone = phoneMatcher.find();
        String phoneFound = hasPhone ? phoneMatcher.group() : null;

        boolean hasLinkedIn = resumeLower.contains("linkedin.com") || resumeLower.contains("linkedin");
        boolean hasGitHub = resumeLower.contains("github.com") || resumeLower.contains("github");

        contactDetails.put("hasEmail", hasEmail);
        contactDetails.put("hasPhone", hasPhone);
        contactDetails.put("hasLinkedIn", hasLinkedIn);
        contactDetails.put("hasGitHub", hasGitHub);
        if (emailFound != null) contactDetails.put("email", emailFound);
        if (phoneFound != null) contactDetails.put("phone", phoneFound);

        if (hasEmail || hasPhone) {
            detectedSections.add("Contact Information");
        } else {
            missingSections.add("Contact Information");
        }

        // Summary / Objective
        boolean hasSummary = containsAny(resumeLower, "summary", "objective", "profile", "about me", "professional summary");
        if (hasSummary) {
            detectedSections.add("Summary / Objective");
        } else {
            missingSections.add("Summary / Objective");
        }

        // Education
        boolean hasEducation = containsAny(resumeLower, "education", "academic", "academics", "bachelor", "master",
                "degree", "university", "college", "b.tech", "b.e", "bca", "mca", "b.sc", "m.sc", "gpa");
        if (hasEducation) {
            detectedSections.add("Education");
        } else {
            missingSections.add("Education");
        }

        // Skills
        boolean hasSkillsSection = containsAny(resumeLower, "skills", "technical skills", "technologies",
                "core competencies", "programming languages", "tools", "tech stack");
        if (hasSkillsSection) {
            detectedSections.add("Skills");
        } else {
            missingSections.add("Skills");
        }

        // Experience / Projects
        boolean hasExperience = containsAny(resumeLower, "experience", "work experience", "employment",
                "projects", "project", "internship", "work history");
        if (hasExperience) {
            detectedSections.add("Experience / Projects");
        } else {
            missingSections.add("Experience / Projects");
        }

        // 2. Formatting Concerns
        List<String> formattingConcerns = new ArrayList<>();
        if (wordCount < 150) {
            formattingConcerns.add(String.format("Resume is very brief (%d words). Aim for 350–800 words to provide ATS adequate depth.", wordCount));
        } else if (wordCount > 1200) {
            formattingConcerns.add(String.format("Resume exceeds standard length (%d words). Consider condensing to 1–2 pages for optimal ATS parsing.", wordCount));
        }

        if (!hasEmail) {
            formattingConcerns.add("Missing email address. ATS parsers require an email to create candidate profile records.");
        }
        if (!hasPhone) {
            formattingConcerns.add("Missing phone number. Standard contact details are expected by ATS screeners.");
        }
        if (trimmed.contains("\uFFFD")) {
            formattingConcerns.add("Detected non-standard encoding or unreadable characters. Ensure text is exported from a clean digital document.");
        }

        // Count action verbs
        int actionVerbCount = 0;
        for (String verb : ACTION_VERBS) {
            if (Pattern.compile("\\b" + Pattern.quote(verb) + "\\b").matcher(resumeLower).find()) {
                actionVerbCount++;
            }
        }
        if (actionVerbCount < 3) {
            formattingConcerns.add("Low use of active verbs. Start bullet points with strong action verbs (e.g. Developed, Led, Architected, Optimized).");
        }

        // 3. Category Scoring
        Map<String, CategoryScore> categoryScores = new LinkedHashMap<>();
        List<String> matchingKeywords = new ArrayList<>();
        List<String> missingKeywords = new ArrayList<>();
        List<String> jobKeywords = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();

        int sectionScore;
        int keywordScore;
        int contentScore;
        int formattingScore;

        if (hasJobDescription) {
            // TARGETED ATS CHECK
            // Identify target keywords in job description
            for (String skill : MASTER_SKILLS) {
                if (containsSkill(jobLower, skill)) {
                    jobKeywords.add(skill);
                }
            }

            // Also detect any significant technical terms in job description
            String[] commonTerms = { "docker", "kubernetes", "rest", "api", "graphql", "microservices", "agile", "scrum", "ci/cd", "cloud" };
            for (String term : commonTerms) {
                if (jobLower.contains(term) && !jobKeywords.contains(term.toUpperCase())) {
                    boolean alreadyCovered = false;
                    for (String jk : jobKeywords) {
                        if (jk.equalsIgnoreCase(term)) {
                            alreadyCovered = true;
                            break;
                        }
                    }
                    if (!alreadyCovered) {
                        jobKeywords.add(capitalize(term));
                    }
                }
            }

            // Keyword match against resume
            if (!jobKeywords.isEmpty()) {
                for (String kw : jobKeywords) {
                    if (containsSkill(resumeLower, kw)) {
                        matchingKeywords.add(kw);
                    } else {
                        missingKeywords.add(kw);
                    }
                }
                keywordScore = (matchingKeywords.size() * 40) / jobKeywords.size();
            } else {
                // Fallback if JD didn't contain catalog keywords: detect resume skill count
                List<String> resumeSkills = detectMasterSkills(resumeLower);
                matchingKeywords.addAll(resumeSkills);
                keywordScore = Math.min(40, resumeSkills.size() * 6);
            }

            // Section Score (Max 30)
            int secCalc = 0;
            if (hasEmail) secCalc += 8;
            if (hasPhone) secCalc += 4;
            if (hasSummary) secCalc += 4;
            if (hasEducation) secCalc += 4;
            if (hasSkillsSection) secCalc += 5;
            if (hasExperience) secCalc += 5;
            sectionScore = Math.min(30, secCalc);

            // Content Score (Max 20)
            int contentCalc = 0;
            if (wordCount >= 300 && wordCount <= 900) contentCalc += 12;
            else if (wordCount >= 150) contentCalc += 8;
            else contentCalc += 3;

            if (actionVerbCount >= 4) contentCalc += 8;
            else if (actionVerbCount >= 2) contentCalc += 5;
            else if (actionVerbCount >= 1) contentCalc += 2;
            contentScore = Math.min(20, contentCalc);

            // Formatting Score (Max 10)
            int formCalc = 10 - (formattingConcerns.size() * 2);
            formattingScore = Math.max(0, formCalc);

            categoryScores.put("sectionScore", new CategoryScore(sectionScore, 30,
                    String.format("%d of 5 core resume sections recognized by ATS", detectedSections.size())));
            categoryScores.put("keywordScore", new CategoryScore(keywordScore, 40,
                    String.format("%d of %d target job keywords matched", matchingKeywords.size(), jobKeywords.size())));
            categoryScores.put("contentScore", new CategoryScore(contentScore, 20,
                    String.format("%d words, %d action verbs detected", wordCount, actionVerbCount)));
            categoryScores.put("formattingScore", new CategoryScore(formattingScore, 10,
                    formattingConcerns.isEmpty() ? "No critical formatting issues" : formattingConcerns.size() + " formatting item(s) to review"));

            // Targeted suggestions
            if (!missingKeywords.isEmpty()) {
                int limit = Math.min(6, missingKeywords.size());
                suggestions.add(String.format("Add missing target job keywords: %s.",
                        String.join(", ", missingKeywords.subList(0, limit))));
            }

        } else {
            // GENERAL ATS CHECK (No Job Description)
            List<String> detectedSkills = detectMasterSkills(resumeLower);
            matchingKeywords.addAll(detectedSkills);

            // Skills Score (Max 25)
            if (detectedSkills.size() >= 8) keywordScore = 25;
            else if (detectedSkills.size() >= 5) keywordScore = 20;
            else if (detectedSkills.size() >= 3) keywordScore = 15;
            else if (detectedSkills.size() >= 1) keywordScore = 8;
            else keywordScore = 0;

            // Section Score (Max 35)
            int secCalc = 0;
            if (hasEmail) secCalc += 10;
            if (hasPhone) secCalc += 5;
            if (hasSummary) secCalc += 5;
            if (hasEducation) secCalc += 5;
            if (hasSkillsSection) secCalc += 5;
            if (hasExperience) secCalc += 5;
            sectionScore = Math.min(35, secCalc);

            // Content Score (Max 25)
            int contentCalc = 0;
            if (wordCount >= 300 && wordCount <= 900) contentCalc += 15;
            else if (wordCount >= 150) contentCalc += 10;
            else contentCalc += 4;

            if (actionVerbCount >= 4) contentCalc += 10;
            else if (actionVerbCount >= 2) contentCalc += 6;
            else if (actionVerbCount >= 1) contentCalc += 3;
            contentScore = Math.min(25, contentCalc);

            // Formatting Score (Max 15)
            int formCalc = 15 - (formattingConcerns.size() * 3);
            formattingScore = Math.max(0, formCalc);

            categoryScores.put("sectionScore", new CategoryScore(sectionScore, 35,
                    String.format("%d of 5 core resume sections recognized by ATS", detectedSections.size())));
            categoryScores.put("keywordScore", new CategoryScore(keywordScore, 25,
                    String.format("%d industry skills detected", detectedSkills.size())));
            categoryScores.put("contentScore", new CategoryScore(contentScore, 25,
                    String.format("%d words, %d action verbs detected", wordCount, actionVerbCount)));
            categoryScores.put("formattingScore", new CategoryScore(formattingScore, 15,
                    formattingConcerns.isEmpty() ? "No critical formatting issues" : formattingConcerns.size() + " formatting item(s) to review"));

            suggestions.add("Paste a target Job Description above to evaluate targeted keyword matching and missing ATS keywords.");
        }

        // Suggestions for missing sections
        for (String missing : missingSections) {
            suggestions.add(String.format("Add a dedicated '%s' section with standard headings for automated ATS indexing.", missing));
        }

        if (actionVerbCount < 3) {
            suggestions.add("Begin experience bullet points with strong action verbs (e.g., Developed, Engineered, Optimized, Deployed).");
        }
        if (wordCount < 300) {
            suggestions.add("Expand project descriptions with technical details, technologies used, and quantifiable results.");
        }

        int totalScore = Math.min(100, Math.max(0, sectionScore + keywordScore + contentScore + formattingScore));

        String rating;
        if (totalScore >= 80) {
            rating = "Strong ATS Match";
        } else if (totalScore >= 65) {
            rating = "Good ATS Match";
        } else if (totalScore >= 50) {
            rating = "Needs Improvement";
        } else {
            rating = "Low ATS Match";
        }

        AtsResult result = new AtsResult();
        result.setScore(totalScore);
        result.setRating(rating);
        result.setMode(hasJobDescription ? "TARGETED" : "GENERAL");
        result.setModeLabel(hasJobDescription
                ? "Targeted ATS Compatibility Score (Matched against provided Job Description)"
                : "General Resume Quality ATS Score (Estimated without specific job description)");
        result.setWordCount(wordCount);
        result.setCategoryScores(categoryScores);
        result.setDetectedSections(detectedSections);
        result.setMissingSections(missingSections);
        result.setContactDetails(contactDetails);
        result.setMatchingKeywords(matchingKeywords);
        result.setMissingKeywords(missingKeywords);
        result.setJobKeywords(jobKeywords);
        result.setFormattingConcerns(formattingConcerns);
        result.setSuggestions(suggestions);
        result.setDisclaimer("Note: ATS scores are calculated estimates based on industry-standard formatting and keyword screening heuristics. They do not guarantee compatibility with every employer's proprietary ATS platform.");

        return result;
    }

    private boolean containsAny(String text, String... candidates) {
        for (String candidate : candidates) {
            if (Pattern.compile("\\b" + Pattern.quote(candidate) + "\\b").matcher(text).find()) {
                return true;
            }
        }
        return false;
    }

    private boolean containsSkill(String text, String skill) {
        String s = skill.toLowerCase(Locale.ROOT);
        if (s.equals("c")) {
            return Pattern.compile("\\b[c]\\b").matcher(text).find();
        }
        if (s.equals("c++") || s.equals("c#") || s.equals(".net")) {
            return text.contains(s);
        }
        return Pattern.compile("\\b" + Pattern.quote(s) + "\\b").matcher(text).find();
    }

    private List<String> detectMasterSkills(String text) {
        List<String> list = new ArrayList<>();
        for (String skill : MASTER_SKILLS) {
            if (containsSkill(text, skill)) {
                list.add(skill);
            }
        }
        return list;
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase(Locale.ROOT) + str.substring(1);
    }
}
