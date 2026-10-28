package com.mahesh.ai_resume_backend.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.mahesh.ai_resume_backend.model.AtsResult;
import com.mahesh.ai_resume_backend.model.ResumeImprovementResponse;
import com.mahesh.ai_resume_backend.model.ResumeImprovementResponse.ImprovementCategory;

@Service
public class ResumeImprovementService {

    private final AtsService atsService;

    private static final String[] WEAK_PHRASES = {
        "worked on", "responsible for", "helped with", "assisted in", "handled",
        "involved in", "participated in", "duties included", "tasked with", "did",
        "tried to", "helped make", "assisted with"
    };

    private static final String[] POWER_VERBS = {
        "Architected", "Engineered", "Implemented", "Spearheaded", "Optimized",
        "Automated", "Streamlined", "Deployed", "Orchestrated", "Accelerated",
        "Refactored", "Designed", "Formulated", "Scaled", "Resolved"
    };

    private static final Pattern METRICS_PATTERN = Pattern.compile(
        "\\b(\\d+[%kKmMbB]?|\\$\\d+|\\d+\\+|\\d+x)\\b|\\b\\d{2,}\\b"
    );

    public ResumeImprovementService(AtsService atsService) {
        this.atsService = atsService;
    }

    public ResumeImprovementResponse generateSuggestions(String resumeText, String jobDescription) {
        if (resumeText == null || resumeText.trim().isEmpty()) {
            throw new IllegalArgumentException("Resume text cannot be empty");
        }

        // 1. Leverage AtsService analysis
        AtsResult ats = atsService.analyzeAts(resumeText, jobDescription);

        String trimmed = resumeText.trim();
        String resumeLower = trimmed.toLowerCase(Locale.ROOT);
        boolean hasJobDescription = jobDescription != null && !jobDescription.trim().isEmpty();

        ResumeImprovementResponse response = new ResumeImprovementResponse();
        response.setOverallHealthScore(ats.getScore());
        response.setMode(ats.getMode());
        response.setModeLabel(hasJobDescription
                ? "Targeted Job Description Improvement Plan"
                : "Comprehensive Resume Quality Improvement Plan");
        response.setMatchingKeywords(ats.getMatchingKeywords());
        response.setMissingKeywords(ats.getMissingKeywords());

        List<ImprovementCategory> categories = new ArrayList<>();
        List<String> priorityActions = new ArrayList<>();
        List<String> strengths = new ArrayList<>();

        // ----------------------------------------------------
        // 2. Identify Weak Verbs vs Strong Action Verbs
        // ----------------------------------------------------
        List<String> weakVerbsFound = new ArrayList<>();
        for (String phrase : WEAK_PHRASES) {
            if (resumeLower.contains(phrase)) {
                weakVerbsFound.add(phrase);
            }
        }
        response.setWeakVerbsFound(weakVerbsFound);
        response.setSuggestedPowerVerbs(Arrays.asList(POWER_VERBS));

        List<String> verbSuggestions = new ArrayList<>();
        List<String> verbExamples = new ArrayList<>();

        if (!weakVerbsFound.isEmpty()) {
            verbSuggestions.add(String.format("Replace passive phrases (%s) with dynamic action verbs that showcase leadership and direct ownership.",
                    String.join(", ", weakVerbsFound)));
            verbSuggestions.add("Begin every project and experience bullet point with an assertive power verb.");
            verbExamples.add("Weak: 'Responsible for backend APIs and database queries.'");
            verbExamples.add("Strong: 'Architected and deployed 15+ RESTful APIs using Spring Boot, reducing query latency by 35%.'");
            priorityActions.add("Replace passive phrases like '" + weakVerbsFound.get(0) + "' with strong power verbs (e.g. Engineered, Spearheaded, Optimized).");
        } else {
            verbSuggestions.add("Great job avoiding common passive expressions! Ensure your verbs vary across bullets (avoid repeating 'Developed' multiple times).");
            verbExamples.add("Tip: Vary your verbs: use 'Engineered' for systems, 'Optimized' for speed/cost, 'Designed' for architecture, and 'Automated' for scripts.");
        }

        categories.add(new ImprovementCategory(
                "action-verbs",
                "Action Verbs & Phrasing",
                weakVerbsFound.isEmpty() ? "LOW" : "HIGH",
                weakVerbsFound.isEmpty() ? "Strong Verbs" : weakVerbsFound.size() + " Passive Phrases Detected",
                verbSuggestions,
                verbExamples
        ));

        // ----------------------------------------------------
        // 3. Vague Project / Experience Descriptions & Metrics
        // ----------------------------------------------------
        Matcher metricMatcher = METRICS_PATTERN.matcher(trimmed);
        int metricsCount = 0;
        while (metricMatcher.find()) {
            metricsCount++;
        }
        boolean hasMetrics = metricsCount >= 2;
        response.setHasQuantifiableMetrics(hasMetrics);
        response.setMetricsCount(metricsCount);

        List<String> impactSuggestions = new ArrayList<>();
        List<String> impactExamples = new ArrayList<>();

        if (!hasMetrics) {
            impactSuggestions.add("Apply the Google XYZ Formula: 'Accomplished [X] as measured by [Y], by doing [Z]'.");
            impactSuggestions.add("Quantify your technical contributions: include percentage improvements, latency drops, user counts, or test coverage numbers.");
            impactSuggestions.add("Provide context on project scale: mention database record sizes, concurrent users, or API request throughput.");
            impactExamples.add("Vague: 'Built a web application with React and MySQL.'");
            impactExamples.add("Impactful: 'Developed an AI-powered resume analyzer serving 1,000+ users, reducing review time by 40% with React and Spring Boot.'");
            priorityActions.add("Quantify achievements in your projects using numbers, percentages, or scale metrics (Google XYZ formula).");
        } else {
            impactSuggestions.add("Good inclusion of quantifiable metrics! Continue reinforcing business impact alongside technical stack details.");
            impactExamples.add("Formula: '[Action Verb] + [Feature/System Built] + [Technologies Used] + [Measurable Result %/$]'");
        }

        categories.add(new ImprovementCategory(
                "measurable-impact",
                "Measurable Impact & Project Descriptions",
                hasMetrics ? "LOW" : "HIGH",
                hasMetrics ? "Metrics Present" : "Needs Quantifiable Results",
                impactSuggestions,
                impactExamples
        ));

        // ----------------------------------------------------
        // 4. Missing Resume Sections
        // ----------------------------------------------------
        List<String> missingSections = ats.getMissingSections();
        List<String> sectionSuggestions = new ArrayList<>();
        List<String> sectionExamples = new ArrayList<>();

        if (!missingSections.isEmpty()) {
            for (String sec : missingSections) {
                switch (sec) {
                    case "Summary / Objective":
                        sectionSuggestions.add("Add a 2–3 sentence 'Professional Summary' at the top of your resume highlighting your primary stack and career goals.");
                        sectionExamples.add("Example Summary: 'Full-Stack Software Engineer with expertise in Java, Spring Boot, and React. Passionate about building high-performance REST APIs and scalable web architectures.'");
                        break;
                    case "Contact Information":
                        sectionSuggestions.add("Ensure your email address, phone number, and LinkedIn/GitHub links are clearly visible at the top of your resume.");
                        sectionExamples.add("Format: 'FirstName LastName | email@domain.com | +1-555-0199 | linkedin.com/in/profile | github.com/username'");
                        break;
                    case "Education":
                        sectionSuggestions.add("Add an 'Education' section specifying your degree, university name, and graduation year.");
                        sectionExamples.add("Format: 'Bachelor of Technology in Computer Science | XYZ University | 2022 – 2026'");
                        break;
                    case "Skills":
                        sectionSuggestions.add("Create an organized 'Technical Skills' section categorized by Languages, Frameworks, Databases, and Tools.");
                        sectionExamples.add("Format: 'Languages: Java, Python, SQL | Frameworks: Spring Boot, React | Tools: Git, Docker, Maven'");
                        break;
                    case "Experience / Projects":
                        sectionSuggestions.add("Include an 'Experience & Projects' section with 2–3 in-depth projects featuring tech stack, architecture, and outcomes.");
                        sectionExamples.add("Format: 'Project Title | Tech Stack | 3 bullet points detailing challenges, solution, and results'");
                        break;
                    default:
                        sectionSuggestions.add(String.format("Add a dedicated '%s' section using standard ATS headers.", sec));
                        break;
                }
            }
            priorityActions.add("Add missing core resume sections: " + String.join(", ", missingSections) + ".");
        } else {
            sectionSuggestions.add("All 5 core resume sections (Contact, Summary, Education, Skills, Experience/Projects) are detected!");
            sectionSuggestions.add("Ensure each section uses standard title casing and clean linear margins for effortless ATS parsing.");
        }

        categories.add(new ImprovementCategory(
                "resume-sections",
                "Resume Section Completeness",
                missingSections.isEmpty() ? "LOW" : "HIGH",
                missingSections.isEmpty() ? "All 5 Sections Present" : missingSections.size() + " Missing Section(s)",
                sectionSuggestions,
                sectionExamples
        ));

        // ----------------------------------------------------
        // 5. Missing Skills & Tech Stack Completeness
        // ----------------------------------------------------
        List<String> skillSuggestions = new ArrayList<>();
        List<String> skillExamples = new ArrayList<>();

        if (hasJobDescription && !ats.getMissingKeywords().isEmpty()) {
            skillSuggestions.add("Align your skills list with the target job requirements by incorporating missing technologies you have experience with.");
            skillSuggestions.add(String.format("Job-specific missing keywords to consider: %s.",
                    String.join(", ", ats.getMissingKeywords())));
            skillExamples.add("Tip: When listing target skills, reference them inside project descriptions to demonstrate practical application.");
            priorityActions.add("Incorporate top missing job keywords: " +
                    String.join(", ", ats.getMissingKeywords().subList(0, Math.min(4, ats.getMissingKeywords().size()))) + ".");
        } else {
            // General skill breadth evaluation
            List<String> detected = ats.getMatchingKeywords();
            boolean hasBackend = detected.contains("Java") || detected.contains("Python") || detected.contains("Spring Boot") || detected.contains("Node.js");
            boolean hasFrontend = detected.contains("React") || detected.contains("JavaScript") || detected.contains("HTML");
            boolean hasDb = detected.contains("SQL") || detected.contains("MySQL") || detected.contains("PostgreSQL") || detected.contains("MongoDB");
            boolean hasDevOps = detected.contains("Docker") || detected.contains("Kubernetes") || detected.contains("AWS") || detected.contains("CI/CD");
            boolean hasGit = detected.contains("Git") || detected.contains("GitHub");

            if (hasBackend && !hasDb) {
                skillSuggestions.add("You have backend skills listed; consider explicitly highlighting database knowledge (e.g., MySQL, PostgreSQL, Redis, JPA/Hibernate).");
            }
            if (hasFrontend && !detected.contains("TypeScript")) {
                skillSuggestions.add("Adding TypeScript alongside JavaScript strengthens modern frontend profiles.");
            }
            if (!hasGit) {
                skillSuggestions.add("Add version control tools (Git, GitHub, GitLab) — recruiters look for collaborative workflow experience.");
            }
            if (!hasDevOps) {
                skillSuggestions.add("Add cloud & containerization exposure (Docker, AWS, CI/CD pipelines) to show modern deployment proficiency.");
            }
            if (skillSuggestions.isEmpty()) {
                skillSuggestions.add("Well-rounded technical skill breadth detected across core development domains.");
            }
            skillExamples.add("Group your skills: 'Languages: Java, Python | Backend: Spring Boot, REST APIs | Databases: MySQL | Tools: Git, Docker'");
        }

        categories.add(new ImprovementCategory(
                "skill-gaps",
                "Skills & Tech Stack Alignment",
                (hasJobDescription && !ats.getMissingKeywords().isEmpty()) ? "HIGH" : "MEDIUM",
                hasJobDescription ? ats.getMissingKeywords().size() + " Skill Gaps vs Job" : "Tech Breadth Optimization",
                skillSuggestions,
                skillExamples
        ));

        // ----------------------------------------------------
        // 6. ATS Keyword Gaps & Screening Optimization
        // ----------------------------------------------------
        List<String> atsSuggestions = new ArrayList<>();
        List<String> atsExamples = new ArrayList<>();

        if (hasJobDescription) {
            atsSuggestions.add("ATS scanners calculate match percentages based on exact keyword frequencies. Integrate required terms organically into bullet points.");
            atsSuggestions.add("Avoid keyword stuffing: words placed in white text or list dumps can trigger ATS rejection penalties.");
            atsExamples.add("Best Practice: Mention a key requirement in your Skills list AND describe how you implemented it in at least one Project.");
        } else {
            atsSuggestions.add("To benchmark keyword alignment for a specific role, paste the employer's Job Description in the box above.");
            atsSuggestions.add("Use standard section headings ('Professional Experience', 'Technical Skills', 'Education') to maximize parsing accuracy.");
            atsExamples.add("Standard Headings: Use simple, universally recognized headers rather than creative titles like 'My Journey' or 'Stuff I Know'.");
        }

        if (ats.getWordCount() < 300) {
            atsSuggestions.add(String.format("Current word count (%d words) is on the low side. Aim for 400–700 words for an early-to-mid career 1-page resume.", ats.getWordCount()));
        }

        categories.add(new ImprovementCategory(
                "ats-keywords",
                "ATS Keyword Strategy & Formatting",
                "MEDIUM",
                hasJobDescription ? "Role-Tailored Screening" : "Standard ATS Heuristics",
                atsSuggestions,
                atsExamples
        ));

        // ----------------------------------------------------
        // 7. Extract Strengths
        // ----------------------------------------------------
        if (ats.getDetectedSections().size() >= 4) {
            strengths.add(String.format("Strong section architecture: %d of 5 core sections clearly identified.", ats.getDetectedSections().size()));
        }
        if (!ats.getMatchingKeywords().isEmpty()) {
            strengths.add("Solid technical skill foundation: " + String.join(", ", ats.getMatchingKeywords().subList(0, Math.min(5, ats.getMatchingKeywords().size()))));
        }
        if (hasMetrics) {
            strengths.add("Includes measurable numbers and metrics to substantiate project results.");
        }
        if (weakVerbsFound.isEmpty()) {
            strengths.add("Clean action-oriented phrasing without passive responsibility clauses.");
        }
        if (strengths.isEmpty()) {
            strengths.add("Clear readable document structure ready for targeted content enhancement.");
        }
        response.setStrengths(strengths);

        // ----------------------------------------------------
        // 8. Overall Summary & Final Polish
        // ----------------------------------------------------
        String summary;
        if (ats.getScore() >= 80) {
            summary = "Your resume is in strong shape with high ATS compatibility. Fine-tune your project metrics and tailor target keywords to achieve maximum interview conversion.";
        } else if (ats.getScore() >= 65) {
            summary = "Good foundation with solid technical skills. Focus on adding missing sections, quantifying project outcomes, and incorporating target role keywords.";
        } else {
            summary = "Your resume has high potential for improvement. Addressing the missing sections, replacing passive verbs, and adding measurable project achievements will significantly boost your recruiter response rate.";
        }
        response.setSummary(summary);
        response.setCategories(categories);

        // Default priority action if list was short
        if (priorityActions.size() < 3) {
            priorityActions.add("Review the Google XYZ formula and add at least two measurable project metrics (% or numbers).");
            priorityActions.add("Organize your technical skills into distinct categories (Languages, Frameworks, Databases, Tools).");
        }
        response.setPriorityActionItems(priorityActions.subList(0, Math.min(5, priorityActions.size())));
        response.setDisclaimer("Disclaimer: Improvement suggestions are generated using deterministic rule-based screening heuristics modeled after industry-standard ATS parsers. They provide actionable guidance to elevate your resume but do not guarantee employment selection.");

        return response;
    }
}
