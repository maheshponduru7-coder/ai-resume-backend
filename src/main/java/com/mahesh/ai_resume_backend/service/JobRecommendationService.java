package com.mahesh.ai_resume_backend.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.mahesh.ai_resume_backend.model.JobRecommendationResponse;
import com.mahesh.ai_resume_backend.model.JobRoleRecommendation;

@Service
public class JobRecommendationService {

    private final SkillService skillService;

    private static final String[] MASTER_SKILLS = {
        "Java", "Python", "C++", "C#", "C", "JavaScript", "TypeScript", "React", "Angular", "Vue",
        "Next.js", "HTML", "CSS", "Tailwind", "Bootstrap", "Spring Boot", "Spring", "Node.js",
        "Express", "SQL", "MySQL", "PostgreSQL", "MongoDB", "Redis", "AWS", "Azure", "GCP",
        "Docker", "Kubernetes", "CI/CD", "Git", "GitHub", "Linux", "REST API", "GraphQL",
        "Microservices", "Data Structures", "Algorithms", "DSA", "Unit Testing", "Agile",
        "Scrum", "OOP", "Machine Learning", "Problem Solving"
    };

    public JobRecommendationService(SkillService skillService) {
        this.skillService = skillService;
    }

    private static class RoleProfile {
        String roleName;
        String description;
        List<String> primarySkills;
        List<String> secondarySkills;
        List<String> tips;

        RoleProfile(String roleName, String description, List<String> primarySkills,
                    List<String> secondarySkills, List<String> tips) {
            this.roleName = roleName;
            this.description = description;
            this.primarySkills = primarySkills;
            this.secondarySkills = secondarySkills;
            this.tips = tips;
        }
    }

    private List<RoleProfile> getStandardRoleProfiles() {
        List<RoleProfile> profiles = new ArrayList<>();

        // 1. Java Developer
        profiles.add(new RoleProfile(
                "Java Developer",
                "Designs, builds, and maintains enterprise backends, microservices, and high-performance server systems in the Java ecosystem.",
                Arrays.asList("Java", "Spring Boot", "SQL", "MySQL", "REST API", "Git"),
                Arrays.asList("Microservices", "Docker", "Unit Testing", "OOP", "Data Structures", "DSA"),
                Arrays.asList(
                        "Strengthen hands-on expertise with Spring Boot, Spring Data JPA, and RESTful web services.",
                        "Add experience with database indexing, transactions, and MySQL/PostgreSQL schema design.",
                        "Demonstrate unit and integration testing skills using JUnit and Mockito.",
                        "Highlight experience with microservices architecture and Docker containerization."
                )
        ));

        // 2. Backend Developer
        profiles.add(new RoleProfile(
                "Backend Developer",
                "Architects and optimizes server-side business logic, database queries, and resilient REST/GraphQL APIs.",
                Arrays.asList("Java", "Python", "Node.js", "Spring Boot", "SQL", "MySQL", "PostgreSQL", "REST API", "Git"),
                Arrays.asList("Microservices", "Redis", "Docker", "CI/CD", "Linux", "MongoDB", "Data Structures"),
                Arrays.asList(
                        "Deepen expertise in backend framework fundamentals (Spring Boot, Express, or Django).",
                        "Incorporate caching layers with Redis to optimize database response times.",
                        "Add containerization with Docker and setup automated CI/CD deployment pipelines.",
                        "Emphasize API security, authentication (JWT/OAuth), and rate limiting."
                )
        ));

        // 3. Full Stack Developer
        profiles.add(new RoleProfile(
                "Full Stack Developer",
                "Builds end-to-end web products spanning reactive client-side interfaces, server-side APIs, and database persistence.",
                Arrays.asList("React", "JavaScript", "HTML", "CSS", "Java", "Node.js", "SQL", "Git"),
                Arrays.asList("TypeScript", "Tailwind", "Spring Boot", "REST API", "MongoDB", "Docker"),
                Arrays.asList(
                        "Showcase complete full-stack web applications connecting React to a Spring Boot or Node backend.",
                        "Emphasize responsive UI design with Tailwind CSS and modern React hooks / state management.",
                        "Learn TypeScript to write robust, maintainable full-stack codebases.",
                        "Document end-to-end API communication, error handling, and deployment."
                )
        ));

        // 4. Software Engineer
        profiles.add(new RoleProfile(
                "Software Engineer",
                "Applies computer science fundamentals, efficient data structures, and clean architecture to solve complex technical problems.",
                Arrays.asList("Data Structures", "Algorithms", "DSA", "OOP", "Problem Solving", "Git", "Java", "C++"),
                Arrays.asList("SQL", "Unit Testing", "Agile", "CI/CD", "REST API", "Linux"),
                Arrays.asList(
                        "Practice core algorithmic problem-solving and space/time complexity optimization.",
                        "Apply Object-Oriented Programming (OOP) and SOLID architectural design principles.",
                        "Highlight quantifiable project metrics using Google's XYZ formula (Accomplished X as measured by Y, by doing Z).",
                        "Show familiarity with Agile workflows, Git version control, and unit test suites."
                )
        ));

        // 5. Frontend Developer
        profiles.add(new RoleProfile(
                "Frontend Developer",
                "Crafts intuitive, accessible, and high-performance user interfaces and responsive web applications.",
                Arrays.asList("JavaScript", "React", "HTML", "CSS", "Git"),
                Arrays.asList("TypeScript", "Tailwind", "Next.js", "REST API", "Bootstrap", "Unit Testing"),
                Arrays.asList(
                        "Build interactive, accessible client-side interfaces using React and modern component state.",
                        "Master responsive design using Tailwind CSS or CSS Grid/Flexbox across mobile and desktop.",
                        "Integrate RESTful APIs smoothly with asynchronous data fetching and error states.",
                        "Familiarize with client-side build tools (Vite) and modern JavaScript (ES6+)."
                )
        ));

        // 6. DevOps / Cloud Engineer
        profiles.add(new RoleProfile(
                "DevOps / Cloud Engineer",
                "Automates deployment pipelines, provisions scalable cloud infrastructure, and oversees container orchestration.",
                Arrays.asList("Docker", "Kubernetes", "CI/CD", "AWS", "Linux", "Git"),
                Arrays.asList("Python", "Microservices", "GCP", "Azure", "SQL", "REST API"),
                Arrays.asList(
                        "Build automated CI/CD deployment pipelines using GitHub Actions or GitLab CI.",
                        "Containerize services with Docker and orchestrate with Kubernetes manifests.",
                        "Gain practical knowledge of cloud compute and storage services on AWS, Azure, or GCP.",
                        "Automate routine deployment and infrastructure monitoring tasks."
                )
        ));

        return profiles;
    }

    public JobRecommendationResponse recommendJobs(String resumeText) {
        if (resumeText == null || resumeText.trim().isEmpty()) {
            throw new IllegalArgumentException("Resume text cannot be empty for job recommendations.");
        }

        String lowerText = resumeText.toLowerCase(Locale.ROOT);

        // 1. Detect candidate skills
        List<String> detectedSkills = extractAllCandidateSkills(lowerText);

        // 2. Detect experience level
        String experienceLevel = detectExperienceLevel(lowerText);

        // 3. Evaluate each role profile
        List<RoleProfile> profiles = getStandardRoleProfiles();
        List<JobRoleRecommendation> recommendations = new ArrayList<>();

        for (RoleProfile profile : profiles) {
            JobRoleRecommendation rec = evaluateRole(profile, detectedSkills, lowerText);
            recommendations.add(rec);
        }

        // Sort by match percentage descending
        recommendations.sort(Comparator.comparingInt(JobRoleRecommendation::getMatchPercentage).reversed());

        JobRecommendationResponse response = new JobRecommendationResponse();
        response.setCandidateSkills(detectedSkills);
        response.setDetectedExperienceLevel(experienceLevel);
        response.setRecommendations(recommendations);

        if (!recommendations.isEmpty()) {
            JobRoleRecommendation top = recommendations.get(0);
            response.setTopRecommendedRole(top.getRoleName());
            response.setTopMatchPercentage(top.getMatchPercentage());
        }

        // Generate tailored overall advice
        response.setOverallAdvice(generateOverallAdvice(recommendations, detectedSkills, experienceLevel));
        response.setDisclaimer("Role recommendations and match percentages are calculated algorithmically based on detected skills, tools, and technical keywords. They provide readiness guidance but do not guarantee job offers with any specific employer.");

        return response;
    }

    private JobRoleRecommendation evaluateRole(RoleProfile profile, List<String> candidateSkills, String lowerText) {
        List<String> matching = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        int primaryMatched = 0;
        for (String skill : profile.primarySkills) {
            if (containsSkill(candidateSkills, skill) || lowerText.contains(skill.toLowerCase(Locale.ROOT))) {
                if (!containsSkill(matching, skill)) {
                    matching.add(skill);
                }
                primaryMatched++;
            } else {
                if (!containsSkill(missing, skill)) {
                    missing.add(skill);
                }
            }
        }

        int secondaryMatched = 0;
        for (String skill : profile.secondarySkills) {
            if (containsSkill(candidateSkills, skill) || lowerText.contains(skill.toLowerCase(Locale.ROOT))) {
                if (!containsSkill(matching, skill)) {
                    matching.add(skill);
                }
                secondaryMatched++;
            } else {
                if (!containsSkill(missing, skill)) {
                    missing.add(skill);
                }
            }
        }

        double primaryWeight = 0.80;
        double secondaryWeight = 0.20;

        double primaryScore = profile.primarySkills.isEmpty() ? 0 :
                ((double) primaryMatched / profile.primarySkills.size()) * primaryWeight * 100.0;
        double secondaryScore = profile.secondarySkills.isEmpty() ? 0 :
                ((double) secondaryMatched / profile.secondarySkills.size()) * secondaryWeight * 100.0;

        int matchPercentage = (int) Math.round(primaryScore + secondaryScore);

        // Keyword relevance bonus if title or related terms appear in resume
        String roleLower = profile.roleName.toLowerCase(Locale.ROOT);
        if (lowerText.contains(roleLower) || lowerText.contains(roleLower.replace(" developer", "")) ||
                lowerText.contains(roleLower.replace(" engineer", ""))) {
            matchPercentage = Math.min(100, matchPercentage + 5);
        }

        // Clamp between 0 and 100
        matchPercentage = Math.max(0, Math.min(100, matchPercentage));

        String fitLevel;
        if (matchPercentage >= 80) {
            fitLevel = "High Match";
        } else if (matchPercentage >= 65) {
            fitLevel = "Strong Match";
        } else if (matchPercentage >= 45) {
            fitLevel = "Moderate Match";
        } else {
            fitLevel = "Potential Match";
        }

        JobRoleRecommendation rec = new JobRoleRecommendation();
        rec.setRoleName(profile.roleName);
        rec.setDescription(profile.description);
        rec.setMatchPercentage(matchPercentage);
        rec.setFitLevel(fitLevel);
        rec.setMatchingSkills(matching);
        rec.setMissingSkills(missing);
        rec.setRequiredSkills(profile.primarySkills);
        rec.setReadinessScore(matchPercentage);

        // Key strengths
        List<String> strengths = new ArrayList<>();
        if (!matching.isEmpty()) {
            strengths.add("Strong foundation in " + String.join(", ", matching.subList(0, Math.min(4, matching.size()))));
        }
        if (primaryMatched >= profile.primarySkills.size() / 2) {
            strengths.add("Demonstrated core competencies required for " + profile.roleName + " responsibilities.");
        }
        rec.setKeyStrengths(strengths);

        // Actionable suggestions
        List<String> suggestions = new ArrayList<>(profile.tips);
        if (!missing.isEmpty()) {
            List<String> keyMissing = missing.subList(0, Math.min(3, missing.size()));
            suggestions.add(0, "Close critical skill gap by learning and demonstrating " + String.join(", ", keyMissing) + ".");
        }
        rec.setActionableSuggestions(suggestions);

        return rec;
    }

    private List<String> extractAllCandidateSkills(String lowerText) {
        List<String> list = new ArrayList<>();

        // 1. SkillService base skills
        List<String> baseSkills = skillService.detectSkills(lowerText);
        for (String s : baseSkills) {
            if (!containsSkill(list, s)) {
                list.add(s);
            }
        }

        // 2. Master skills check
        for (String skill : MASTER_SKILLS) {
            String sLower = skill.toLowerCase(Locale.ROOT);
            if (sLower.equals("c")) {
                if (Pattern.compile("\\b[c]\\b").matcher(lowerText).find()) {
                    if (!containsSkill(list, "C")) list.add("C");
                }
            } else if (sLower.equals("c++") || sLower.equals("c#")) {
                if (lowerText.contains(sLower)) {
                    if (!containsSkill(list, skill)) list.add(skill);
                }
            } else {
                if (Pattern.compile("\\b" + Pattern.quote(sLower) + "\\b").matcher(lowerText).find()) {
                    if (!containsSkill(list, skill)) list.add(skill);
                }
            }
        }

        Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
        return list;
    }

    private String detectExperienceLevel(String text) {
        if (text.contains("senior") || text.contains("lead") || text.contains("principal") || text.contains("architect")) {
            return "Senior / Lead Level";
        }

        Pattern expYearsPattern = Pattern.compile("(\\d+)\\+?\\s*(?:years?|yrs?)\\b");
        Matcher m = expYearsPattern.matcher(text);
        if (m.find()) {
            try {
                int years = Integer.parseInt(m.group(1));
                if (years >= 5) return "Senior Level (" + years + "+ Years)";
                if (years >= 2) return "Mid-Level (" + years + "+ Years)";
            } catch (NumberFormatException ignored) {
            }
        }

        if (text.contains("intern") || text.contains("fresher") || text.contains("b.tech") ||
                text.contains("bachelor") || text.contains("student") || text.contains("graduate")) {
            return "Entry-Level / Fresher";
        }

        return "Mid-Level Professional";
    }

    private String generateOverallAdvice(List<JobRoleRecommendation> recommendations, List<String> skills, String expLevel) {
        if (recommendations.isEmpty()) {
            return "Upload a resume with detailed project descriptions and technical skills to generate tailored role recommendations.";
        }

        JobRoleRecommendation top = recommendations.get(0);
        return "Based on your technical profile and " + expLevel + " background, your strongest current alignment is as a "
                + top.getRoleName() + " (" + top.getMatchPercentage() + "% match). "
                + "Focus on closing the identified skill gaps in "
                + (top.getMissingSkills().isEmpty() ? "advanced domain topics" : String.join(", ", top.getMissingSkills().subList(0, Math.min(3, top.getMissingSkills().size()))))
                + " to maximize interview conversion rates.";
    }

    private boolean containsSkill(List<String> list, String target) {
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
