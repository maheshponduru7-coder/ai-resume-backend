package com.mahesh.ai_resume_backend.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.mahesh.ai_resume_backend.model.ProjectSuggestion;
import com.mahesh.ai_resume_backend.model.RoadmapPhase;
import com.mahesh.ai_resume_backend.model.RoleGapComparison;
import com.mahesh.ai_resume_backend.model.SkillGapResponse;
import com.mahesh.ai_resume_backend.model.SkillItem;
import com.mahesh.ai_resume_backend.service.JobRecommendationService.RoleProfile;

@Service
public class SkillGapService {

    private final JobRecommendationService jobRecommendationService;

    // Prerequisite / adjacent skill relationships for partial coverage
    private static final Map<String, List<String>> PARTIAL_RELATIONS = new HashMap<>();
    private static final Map<String, String> PARTIAL_REASONS = new HashMap<>();

    // Difficulty categorizations
    private static final Set<String> BEGINNER_SKILLS = new HashSet<>(Arrays.asList(
            "HTML", "CSS", "Tailwind", "Bootstrap", "Git", "GitHub", "Linux", "SQL", "Problem Solving", "Agile", "Scrum"
    ));

    private static final Set<String> INTERMEDIATE_SKILLS = new HashSet<>(Arrays.asList(
            "Java", "Python", "JavaScript", "C", "C++", "C#", "React", "Angular", "Vue", "Node.js", "Express",
            "MySQL", "PostgreSQL", "MongoDB", "REST API", "Unit Testing", "OOP"
    ));

    private static final Set<String> ADVANCED_SKILLS = new HashSet<>(Arrays.asList(
            "Spring Boot", "Spring", "TypeScript", "Next.js", "Microservices", "Docker", "Kubernetes", "CI/CD",
            "AWS", "Azure", "GCP", "Redis", "GraphQL", "Data Structures", "Algorithms", "DSA", "Machine Learning"
    ));

    static {
        // TypeScript <- JavaScript
        PARTIAL_RELATIONS.put("TypeScript", Arrays.asList("JavaScript"));
        PARTIAL_REASONS.put("TypeScript", "Strong JavaScript foundation directly accelerates TypeScript type syntax mastery.");

        // Spring Boot <- Java
        PARTIAL_RELATIONS.put("Spring Boot", Arrays.asList("Java"));
        PARTIAL_REASONS.put("Spring Boot", "Core Java object-oriented foundation provides the necessary language background for Spring Boot.");

        // Spring <- Java, Spring Boot
        PARTIAL_RELATIONS.put("Spring", Arrays.asList("Java", "Spring Boot"));
        PARTIAL_REASONS.put("Spring", "Java language fluency allows rapid understanding of Spring dependency injection and IoC container.");

        // Next.js <- React, JavaScript
        PARTIAL_RELATIONS.put("Next.js", Arrays.asList("React", "JavaScript"));
        PARTIAL_REASONS.put("Next.js", "React component architecture and hook patterns transfer directly to Next.js full-stack features.");

        // PostgreSQL <- MySQL, SQL
        PARTIAL_RELATIONS.put("PostgreSQL", Arrays.asList("MySQL", "SQL"));
        PARTIAL_REASONS.put("PostgreSQL", "Relational database querying and schema design principles transfer directly to PostgreSQL.");

        // MySQL <- PostgreSQL, SQL
        PARTIAL_RELATIONS.put("MySQL", Arrays.asList("PostgreSQL", "SQL"));
        PARTIAL_REASONS.put("MySQL", "Relational database design and ANSI SQL querying transfer seamlessly to MySQL.");

        // MongoDB <- SQL, MySQL, PostgreSQL
        PARTIAL_RELATIONS.put("MongoDB", Arrays.asList("SQL", "MySQL", "PostgreSQL"));
        PARTIAL_REASONS.put("MongoDB", "General database modeling background provides a solid baseline for NoSQL document stores.");

        // Redis <- SQL, MySQL, PostgreSQL
        PARTIAL_RELATIONS.put("Redis", Arrays.asList("SQL", "MySQL", "PostgreSQL"));
        PARTIAL_REASONS.put("Redis", "Database query experience aids in designing high-speed in-memory caching strategies.");

        // Kubernetes <- Docker
        PARTIAL_RELATIONS.put("Kubernetes", Arrays.asList("Docker"));
        PARTIAL_REASONS.put("Kubernetes", "Docker containerization knowledge is the core prerequisite for Kubernetes cluster orchestration.");

        // CI/CD <- Git, GitHub, Docker
        PARTIAL_RELATIONS.put("CI/CD", Arrays.asList("Git", "GitHub", "Docker"));
        PARTIAL_REASONS.put("CI/CD", "Version control and container workflow experience provides a solid foundation for pipeline automation.");

        // Microservices <- REST API, Spring Boot, Node.js
        PARTIAL_RELATIONS.put("Microservices", Arrays.asList("REST API", "Spring Boot", "Node.js"));
        PARTIAL_REASONS.put("Microservices", "RESTful API and backend service design experience enables transition to distributed microservices.");

        // AWS <- Docker, Linux, GCP, Azure
        PARTIAL_RELATIONS.put("AWS", Arrays.asList("Docker", "Linux", "GCP", "Azure"));
        PARTIAL_REASONS.put("AWS", "Linux and container infrastructure knowledge transfers directly to AWS compute and storage services.");

        // Azure <- AWS, GCP, Docker
        PARTIAL_RELATIONS.put("Azure", Arrays.asList("AWS", "GCP", "Docker"));
        PARTIAL_REASONS.put("Azure", "Cloud computing fundamentals transfer across providers to Microsoft Azure.");

        // GCP <- AWS, Azure, Docker
        PARTIAL_RELATIONS.put("GCP", Arrays.asList("AWS", "Azure", "Docker"));
        PARTIAL_REASONS.put("GCP", "Cloud architecture concepts transfer across major public cloud providers.");

        // Algorithms <- Data Structures, DSA, Problem Solving
        PARTIAL_RELATIONS.put("Algorithms", Arrays.asList("Data Structures", "DSA", "Problem Solving"));
        PARTIAL_REASONS.put("Algorithms", "Analytical problem-solving and data structure foundations directly support algorithmic optimization.");

        // DSA <- Data Structures, Algorithms, Problem Solving
        PARTIAL_RELATIONS.put("DSA", Arrays.asList("Data Structures", "Algorithms", "Problem Solving"));
        PARTIAL_REASONS.put("DSA", "Problem-solving and structured data organization cover core DSA principles.");

        // Data Structures <- DSA, Algorithms, Java, C++, Python
        PARTIAL_RELATIONS.put("Data Structures", Arrays.asList("DSA", "Algorithms", "Java", "C++", "Python"));
        PARTIAL_REASONS.put("Data Structures", "Strong programming language fundamentals assist in mastering structured memory layouts.");

        // Unit Testing <- Java, JavaScript, Python, C#
        PARTIAL_RELATIONS.put("Unit Testing", Arrays.asList("Java", "JavaScript", "Python", "C#"));
        PARTIAL_REASONS.put("Unit Testing", "Core programming language fluency accelerates adopting automated testing frameworks.");

        // Tailwind <- CSS, HTML, Bootstrap
        PARTIAL_RELATIONS.put("Tailwind", Arrays.asList("CSS", "HTML", "Bootstrap"));
        PARTIAL_REASONS.put("Tailwind", "CSS and responsive styling background allows quick adoption of Tailwind utility classes.");

        // Bootstrap <- CSS, HTML, Tailwind
        PARTIAL_RELATIONS.put("Bootstrap", Arrays.asList("CSS", "HTML", "Tailwind"));
        PARTIAL_REASONS.put("Bootstrap", "HTML and CSS layout experience transfers directly to Bootstrap UI components.");

        // GraphQL <- REST API
        PARTIAL_RELATIONS.put("GraphQL", Arrays.asList("REST API"));
        PARTIAL_REASONS.put("GraphQL", "RESTful API design and client-server concepts provide the foundation for GraphQL schema queries.");

        // Express <- Node.js, JavaScript
        PARTIAL_RELATIONS.put("Express", Arrays.asList("Node.js", "JavaScript"));
        PARTIAL_REASONS.put("Express", "Node.js and JavaScript runtime expertise directly powers Express server development.");
    }

    public SkillGapService(JobRecommendationService jobRecommendationService) {
        this.jobRecommendationService = jobRecommendationService;
    }

    public SkillGapResponse analyzeSkillGap(String resumeText, String requestedRole) {
        if (resumeText == null || resumeText.trim().isEmpty()) {
            throw new IllegalArgumentException("Resume text cannot be empty for skill gap analysis.");
        }

        String lowerText = resumeText.toLowerCase(Locale.ROOT);
        List<String> candidateSkills = jobRecommendationService.extractAllCandidateSkills(lowerText);
        List<RoleProfile> allProfiles = jobRecommendationService.getStandardRoleProfiles();

        // Determine target role profile
        RoleProfile targetProfile = resolveRoleProfile(allProfiles, requestedRole);

        // Analyze skills against selected target profile
        List<SkillItem> matchedSkills = new ArrayList<>();
        List<SkillItem> partialSkills = new ArrayList<>();
        List<SkillItem> missingSkills = new ArrayList<>();

        Set<String> processedSkills = new HashSet<>();
        List<String> combinedRoleSkills = new ArrayList<>();

        for (String skill : targetProfile.getPrimarySkills()) {
            if (processedSkills.add(skill.toLowerCase(Locale.ROOT))) {
                combinedRoleSkills.add(skill);
            }
        }
        for (String skill : targetProfile.getSecondarySkills()) {
            if (processedSkills.add(skill.toLowerCase(Locale.ROOT))) {
                combinedRoleSkills.add(skill);
            }
        }

        for (String skill : combinedRoleSkills) {
            boolean isPrimary = containsSkill(targetProfile.getPrimarySkills(), skill);
            String priority = isPrimary ? "HIGH" : "MEDIUM";
            String level = determineLearningLevel(skill);

            if (containsSkill(candidateSkills, skill) || lowerText.contains(skill.toLowerCase(Locale.ROOT))) {
                // Directly matched
                matchedSkills.add(new SkillItem(
                        skill,
                        "MATCHED",
                        priority,
                        level,
                        "Direct match verified in resume skills and project descriptions.",
                        "Mastered"
                ));
            } else {
                // Check for partial coverage via related skills
                String partialPrereq = findPartialPrerequisite(skill, candidateSkills, lowerText);
                if (partialPrereq != null) {
                    String reason = PARTIAL_REASONS.getOrDefault(
                            skill,
                            "Foundational knowledge in " + partialPrereq + " provides transferable concepts for " + skill + "."
                    );
                    partialSkills.add(new SkillItem(
                            skill,
                            "PARTIAL",
                            priority,
                            level,
                            reason + " (Prerequisite: " + partialPrereq + ")",
                            estimateLearningTime(level, true)
                    ));
                } else {
                    // Fully missing
                    missingSkills.add(new SkillItem(
                            skill,
                            "MISSING",
                            priority,
                            level,
                            isPrimary ? "Core requirement essential for " + targetProfile.getRoleName() + " roles."
                                      : "Recommended skill that enhances technical versatility.",
                            estimateLearningTime(level, false)
                    ));
                }
            }
        }

        // Sort missing skills by priority (HIGH first), then by difficulty level
        sortSkills(missingSkills);
        sortSkills(partialSkills);

        int totalSkills = combinedRoleSkills.size();
        int matchedCount = matchedSkills.size();
        int partialCount = partialSkills.size();
        int missingCount = missingSkills.size();

        // Coverage percentage: (matched + 0.5 * partial) / total * 100
        int coveragePercentage = 0;
        if (totalSkills > 0) {
            double coverageValue = ((double) matchedCount + (0.5 * partialCount)) / totalSkills * 100.0;
            coveragePercentage = (int) Math.round(coverageValue);
            coveragePercentage = Math.max(0, Math.min(100, coveragePercentage));
        }

        String readinessLevel;
        if (coveragePercentage >= 80) {
            readinessLevel = "Job Ready";
        } else if (coveragePercentage >= 65) {
            readinessLevel = "Near Ready";
        } else if (coveragePercentage >= 45) {
            readinessLevel = "Developing Competency";
        } else {
            readinessLevel = "Needs Upskilling";
        }

        // Build personalized learning roadmap
        List<RoadmapPhase> roadmap = generateRoadmap(targetProfile, missingSkills, partialSkills);

        // Build practical project suggestions
        List<ProjectSuggestion> projects = generateProjectSuggestions(targetProfile, missingSkills, partialSkills);

        // Generate cross-role comparisons
        List<RoleGapComparison> comparisons = generateRoleComparisons(allProfiles, candidateSkills, lowerText);

        List<String> availableRoleNames = new ArrayList<>();
        for (RoleProfile p : allProfiles) {
            availableRoleNames.add(p.getRoleName());
        }

        SkillGapResponse response = new SkillGapResponse();
        response.setTargetRole(targetProfile.getRoleName());
        response.setRoleDescription(targetProfile.getDescription());
        response.setCoveragePercentage(coveragePercentage);
        response.setReadinessLevel(readinessLevel);
        response.setTotalRequiredSkills(totalSkills);
        response.setMatchedCount(matchedCount);
        response.setPartialCount(partialCount);
        response.setMissingCount(missingCount);
        response.setCandidateSkills(candidateSkills);
        response.setMatchedSkills(matchedSkills);
        response.setPartiallyCoveredSkills(partialSkills);
        response.setMissingSkills(missingSkills);
        response.setRoadmapPhases(roadmap);
        response.setProjectSuggestions(projects);
        response.setRoleComparisons(comparisons);
        response.setAvailableRoles(availableRoleNames);

        response.setAdvice(generateStrategicAdvice(targetProfile, coveragePercentage, readinessLevel, partialSkills, missingSkills));
        response.setDisclaimer("Skill coverage and gap analysis are rule-based estimates derived from your resume text and target industry skill profiles. Actual job hiring standards vary by company and team.");

        return response;
    }

    private RoleProfile resolveRoleProfile(List<RoleProfile> profiles, String requestedRole) {
        if (requestedRole != null && !requestedRole.trim().isEmpty()) {
            String targetLower = requestedRole.trim().toLowerCase(Locale.ROOT);
            for (RoleProfile profile : profiles) {
                if (profile.getRoleName().toLowerCase(Locale.ROOT).equals(targetLower)) {
                    return profile;
                }
            }
            // Partial name match
            for (RoleProfile profile : profiles) {
                if (profile.getRoleName().toLowerCase(Locale.ROOT).contains(targetLower) ||
                        targetLower.contains(profile.getRoleName().toLowerCase(Locale.ROOT))) {
                    return profile;
                }
            }
        }
        return profiles.get(0); // Default to first (Java Developer)
    }

    private String findPartialPrerequisite(String targetSkill, List<String> candidateSkills, String lowerText) {
        List<String> relations = PARTIAL_RELATIONS.get(targetSkill);
        if (relations == null) return null;

        for (String prereq : relations) {
            if (containsSkill(candidateSkills, prereq) || lowerText.contains(prereq.toLowerCase(Locale.ROOT))) {
                return prereq;
            }
        }
        return null;
    }

    private String determineLearningLevel(String skill) {
        if (BEGINNER_SKILLS.contains(skill)) return "BEGINNER";
        if (ADVANCED_SKILLS.contains(skill)) return "ADVANCED";
        return "INTERMEDIATE";
    }

    private String estimateLearningTime(String level, boolean isPartial) {
        if (isPartial) {
            switch (level) {
                case "BEGINNER": return "3–5 Days (Fast-Track)";
                case "INTERMEDIATE": return "1–2 Weeks (Fast-Track)";
                case "ADVANCED": return "2–3 Weeks (Fast-Track)";
                default: return "1–2 Weeks";
            }
        }
        switch (level) {
            case "BEGINNER": return "1–2 Weeks";
            case "INTERMEDIATE": return "2–4 Weeks";
            case "ADVANCED": return "4–6 Weeks";
            default: return "2–3 Weeks";
        }
    }

    private void sortSkills(List<SkillItem> skills) {
        skills.sort((a, b) -> {
            int priorityCompare = getPriorityWeight(a.getPriority()) - getPriorityWeight(b.getPriority());
            if (priorityCompare != 0) return priorityCompare;
            return getDifficultyWeight(a.getLearningLevel()) - getDifficultyWeight(b.getLearningLevel());
        });
    }

    private int getPriorityWeight(String priority) {
        if ("HIGH".equalsIgnoreCase(priority)) return 1;
        if ("MEDIUM".equalsIgnoreCase(priority)) return 2;
        return 3;
    }

    private int getDifficultyWeight(String level) {
        if ("BEGINNER".equalsIgnoreCase(level)) return 1;
        if ("INTERMEDIATE".equalsIgnoreCase(level)) return 2;
        return 3;
    }

    private List<RoadmapPhase> generateRoadmap(RoleProfile profile, List<SkillItem> missing, List<SkillItem> partial) {
        List<RoadmapPhase> phases = new ArrayList<>();

        List<String> phase1Skills = new ArrayList<>();
        List<String> phase2Skills = new ArrayList<>();
        List<String> phase3Skills = new ArrayList<>();

        List<SkillItem> allGaps = new ArrayList<>(partial);
        allGaps.addAll(missing);

        for (SkillItem item : allGaps) {
            if ("BEGINNER".equalsIgnoreCase(item.getLearningLevel())) {
                phase1Skills.add(item.getName());
            } else if ("INTERMEDIATE".equalsIgnoreCase(item.getLearningLevel())) {
                phase2Skills.add(item.getName());
            } else {
                phase3Skills.add(item.getName());
            }
        }

        // Phase 1: Foundations & Tooling
        RoadmapPhase p1 = new RoadmapPhase();
        p1.setPhaseNumber(1);
        p1.setTitle("Phase 1: Foundations & Essential Tooling");
        p1.setEstimatedDuration(phase1Skills.isEmpty() ? "Completed ✓" : "1–2 Weeks");
        if (phase1Skills.isEmpty()) {
            p1.setDescription("Your foundational developer tools, version control, and database querying basics are already established.");
            p1.setTargetSkills(Arrays.asList("Git", "SQL", "Linux"));
            p1.setActionItems(Arrays.asList(
                    "Review clean Git commit workflows, branching models, and pull request hygiene.",
                    "Audit database indexing and relational SQL constraints for performance."
            ));
        } else {
            p1.setDescription("Solidify foundational tooling, version control, and scripting basics necessary for daily workflows.");
            p1.setTargetSkills(phase1Skills);
            p1.setActionItems(Arrays.asList(
                    "Master core commands and syntax for: " + String.join(", ", phase1Skills) + ".",
                    "Incorporate automated Git hooks and write structured SQL queries for relational data."
            ));
        }
        phases.add(p1);

        // Phase 2: Core Stack & Framework Implementation
        RoadmapPhase p2 = new RoadmapPhase();
        p2.setPhaseNumber(2);
        p2.setTitle("Phase 2: Core Frameworks & Architecture");
        p2.setEstimatedDuration(phase2Skills.isEmpty() ? "1 Week" : "2–4 Weeks");
        if (phase2Skills.isEmpty()) {
            p2.setDescription("Your core application development skills are well-matched for " + profile.getRoleName() + ".");
            p2.setTargetSkills(profile.getPrimarySkills().subList(0, Math.min(3, profile.getPrimarySkills().size())));
            p2.setActionItems(Arrays.asList(
                    "Deepen existing architecture with SOLID design patterns and clean code principles.",
                    "Expand automated unit testing coverage with JUnit/Mockito or Jest."
            ));
        } else {
            p2.setDescription("Build hands-on fluency in primary framework requirements and backend/frontend APIs.");
            p2.setTargetSkills(phase2Skills);
            p2.setActionItems(Arrays.asList(
                    "Implement a modular project applying: " + String.join(", ", phase2Skills) + ".",
                    "Focus on RESTful API design, request validation, authentication, and error handling."
            ));
        }
        phases.add(p2);

        // Phase 3: Advanced Architecture & Production Engineering
        RoadmapPhase p3 = new RoadmapPhase();
        p3.setPhaseNumber(3);
        p3.setTitle("Phase 3: Production Readiness, Cloud & Scale");
        p3.setEstimatedDuration(phase3Skills.isEmpty() ? "2 Weeks" : "3–5 Weeks");
        if (phase3Skills.isEmpty()) {
            p3.setDescription("Focus on deployment resilience, containerization, and system performance optimizations.");
            p3.setTargetSkills(Arrays.asList("Docker", "CI/CD", "Performance Optimization"));
            p3.setActionItems(Arrays.asList(
                    "Containerize application using multi-stage Docker builds.",
                    "Set up automated CI/CD deployment pipelines on GitHub Actions."
            ));
        } else {
            p3.setDescription("Master distributed architecture, cloud infrastructure, containerization, and CI/CD automation.");
            p3.setTargetSkills(phase3Skills);
            p3.setActionItems(Arrays.asList(
                    "Implement containerization and deployment pipelines for: " + String.join(", ", phase3Skills) + ".",
                    "Optimize system throughput with caching layers (Redis) and cloud orchestration."
            ));
        }
        phases.add(p3);

        return phases;
    }

    private List<ProjectSuggestion> generateProjectSuggestions(RoleProfile profile, List<SkillItem> missing, List<SkillItem> partial) {
        List<ProjectSuggestion> projects = new ArrayList<>();
        String roleName = profile.getRoleName();

        List<String> gapSkills = new ArrayList<>();
        for (SkillItem item : partial) gapSkills.add(item.getName());
        for (SkillItem item : missing) gapSkills.add(item.getName());

        if (roleName.contains("Java") || roleName.contains("Backend")) {
            ProjectSuggestion p1 = new ProjectSuggestion();
            p1.setTitle("Enterprise Microservices Task & Order Management System");
            p1.setDescription("Build an end-to-end distributed backend service featuring REST APIs, relational database persistence, JWT authentication, and Docker containerization.");
            p1.setDifficulty("Intermediate / Advanced");
            List<String> p1Skills = new ArrayList<>(Arrays.asList("Java", "Spring Boot", "SQL", "REST API"));
            if (gapSkills.contains("Docker")) p1Skills.add("Docker");
            if (gapSkills.contains("Microservices")) p1Skills.add("Microservices");
            if (gapSkills.contains("Redis")) p1Skills.add("Redis");
            p1.setTargetedSkills(p1Skills);
            p1.setKeyFeatures(Arrays.asList(
                    "Stateless authentication and role-based access control using Spring Security & JWT.",
                    "Relational schema design with Spring Data JPA and database migrations.",
                    "In-memory Redis caching to reduce database query latency by over 60%.",
                    "Dockerfile and docker-compose orchestration for reproducible local development."
            ));
            projects.add(p1);

            ProjectSuggestion p2 = new ProjectSuggestion();
            p2.setTitle("High-Throughput Event-Driven Notification Engine");
            p2.setDescription("Architect a resilient background processing service that consumes and queues alerts, sends emails, and enforces rate-limiting.");
            p2.setDifficulty("Advanced");
            List<String> p2Skills = new ArrayList<>(Arrays.asList("Java", "PostgreSQL", "Unit Testing", "CI/CD"));
            p2.setTargetedSkills(p2Skills);
            p2.setKeyFeatures(Arrays.asList(
                    "Asynchronous background task execution with robust retry mechanisms.",
                    "Over 85% test coverage using JUnit 5 and Mockito unit/integration suites.",
                    "Automated GitHub Actions CI workflow running unit tests on every pull request."
            ));
            projects.add(p2);
        } else if (roleName.contains("Full Stack")) {
            ProjectSuggestion p1 = new ProjectSuggestion();
            p1.setTitle("Modern SaaS Collaborative Workflow Platform");
            p1.setDescription("Design a full-stack reactive dashboard connecting a modern React/Next.js frontend with a secure Spring Boot or Node.js API layer.");
            p1.setDifficulty("Intermediate");
            List<String> p1Skills = new ArrayList<>(Arrays.asList("React", "JavaScript", "HTML", "CSS", "REST API"));
            if (gapSkills.contains("TypeScript")) p1Skills.add("TypeScript");
            if (gapSkills.contains("Tailwind")) p1Skills.add("Tailwind");
            if (gapSkills.contains("Spring Boot")) p1Skills.add("Spring Boot");
            p1.setTargetedSkills(p1Skills);
            p1.setKeyFeatures(Arrays.asList(
                    "Responsive client-side interface built with Tailwind CSS and accessible UI components.",
                    "End-to-end type safety using TypeScript interfaces across API contracts.",
                    "Optimistic UI state updates and asynchronous error handling."
            ));
            projects.add(p1);

            ProjectSuggestion p2 = new ProjectSuggestion();
            p2.setTitle("Full-Stack Analytics & Reporting Dashboard");
            p2.setDescription("Develop an interactive data visualization platform with filterable charts, PDF exports, and database persistence.");
            p2.setDifficulty("Advanced");
            p2.setTargetedSkills(Arrays.asList("React", "Node.js", "MongoDB", "Docker", "Git"));
            p2.setKeyFeatures(Arrays.asList(
                    "Aggregated statistical queries and responsive client charting.",
                    "PDF report generation and downloadable data summaries.",
                    "Docker containerized multi-service deployment."
            ));
            projects.add(p2);
        } else if (roleName.contains("Frontend")) {
            ProjectSuggestion p1 = new ProjectSuggestion();
            p1.setTitle("Design-System Powered E-Commerce Web App");
            p1.setDescription("Build a responsive web application featuring custom reusable components, product filtering, shopping cart state, and smooth checkout animations.");
            p1.setDifficulty("Intermediate");
            p1.setTargetedSkills(Arrays.asList("React", "JavaScript", "HTML", "CSS", "Tailwind", "REST API"));
            p1.setKeyFeatures(Arrays.asList(
                    "Modular component architecture using custom React hooks and Context API.",
                    "Tailwind CSS responsive layouts tested across mobile, tablet, and desktop breakpoints.",
                    "REST API integration with loading skeletons and robust error boundaries."
            ));
            projects.add(p1);

            ProjectSuggestion p2 = new ProjectSuggestion();
            p2.setTitle("Accessible Multi-Step Career Application Portal");
            p2.setDescription("Create an accessible form workflow with client-side validation, theme switching, and automated Jest/React Testing Library specs.");
            p2.setDifficulty("Intermediate");
            p2.setTargetedSkills(Arrays.asList("React", "TypeScript", "Unit Testing", "Next.js"));
            p2.setKeyFeatures(Arrays.asList(
                    "Strict WCAG 2.1 AA accessibility compliance and keyboard navigation.",
                    "Comprehensive unit and integration test coverage with React Testing Library."
            ));
            projects.add(p2);
        } else if (roleName.contains("DevOps") || roleName.contains("Cloud")) {
            ProjectSuggestion p1 = new ProjectSuggestion();
            p1.setTitle("Multi-Tier GitOps Cloud Infrastructure Deployment");
            p1.setDescription("Implement containerized microservices orchestrated with Kubernetes and deployed automatically via GitHub Actions pipelines.");
            p1.setDifficulty("Advanced");
            p1.setTargetedSkills(Arrays.asList("Docker", "Kubernetes", "CI/CD", "AWS", "Linux", "Git"));
            p1.setKeyFeatures(Arrays.asList(
                    "Multi-stage Dockerfiles optimizing image size and security vulnerability scanning.",
                    "Kubernetes deployments, service meshes, ingress controllers, and secret managers.",
                    "Automated GitHub Actions CI/CD pipeline triggered on main branch releases."
            ));
            projects.add(p1);

            ProjectSuggestion p2 = new ProjectSuggestion();
            p2.setTitle("Automated Cloud Monitoring & Log Aggregation Pipeline");
            p2.setDescription("Set up automated infrastructure provisioning and log alerting across cloud server instances.");
            p2.setDifficulty("Intermediate / Advanced");
            p2.setTargetedSkills(Arrays.asList("AWS", "Linux", "Python", "CI/CD"));
            p2.setKeyFeatures(Arrays.asList(
                    "Bash and Python automation scripts for routine health checks.",
                    "Centralized dashboard monitoring CPU, memory, and API response metrics."
            ));
            projects.add(p2);
        } else {
            // Software Engineer / General
            ProjectSuggestion p1 = new ProjectSuggestion();
            p1.setTitle("Algorithmic In-Memory Key-Value Store with Concurrency");
            p1.setDescription("Build an in-memory storage engine implementing custom hash tables, LRU eviction caches, and thread-safe data operations.");
            p1.setDifficulty("Advanced");
            p1.setTargetedSkills(Arrays.asList("Data Structures", "Algorithms", "DSA", "OOP", "Problem Solving", "Java"));
            p1.setKeyFeatures(Arrays.asList(
                    "O(1) time complexity LRU cache eviction policy using doubly linked lists and hash maps.",
                    "Concurrent read/write access handling and thread safety validation.",
                    "Comprehensive automated test harness benchmarking throughput under load."
            ));
            projects.add(p1);

            ProjectSuggestion p2 = new ProjectSuggestion();
            p2.setTitle("High-Performance Graph Pathfinding & Network Simulation");
            p2.setDescription("Simulate network routing algorithms (Dijkstra, A*) on large-scale topological graphs with real-time performance metrics.");
            p2.setDifficulty("Intermediate / Advanced");
            p2.setTargetedSkills(Arrays.asList("Algorithms", "DSA", "Git", "Unit Testing"));
            p2.setKeyFeatures(Arrays.asList(
                    "Optimized shortest path computation on weighted dynamic graph networks.",
                    "Unit test suites validating edge cases, negative weights, and disconnected nodes."
            ));
            projects.add(p2);
        }

        return projects;
    }

    private List<RoleGapComparison> generateRoleComparisons(List<RoleProfile> profiles, List<String> candidateSkills, String lowerText) {
        List<RoleGapComparison> comparisons = new ArrayList<>();

        for (RoleProfile p : profiles) {
            Set<String> processed = new HashSet<>();
            List<String> combined = new ArrayList<>();
            for (String s : p.getPrimarySkills()) {
                if (processed.add(s.toLowerCase(Locale.ROOT))) combined.add(s);
            }
            for (String s : p.getSecondarySkills()) {
                if (processed.add(s.toLowerCase(Locale.ROOT))) combined.add(s);
            }

            int matched = 0;
            int partial = 0;
            int missing = 0;

            for (String s : combined) {
                if (containsSkill(candidateSkills, s) || lowerText.contains(s.toLowerCase(Locale.ROOT))) {
                    matched++;
                } else if (findPartialPrerequisite(s, candidateSkills, lowerText) != null) {
                    partial++;
                } else {
                    missing++;
                }
            }

            int total = combined.size();
            int coverage = total == 0 ? 0 : (int) Math.round(((double) matched + (0.5 * partial)) / total * 100.0);
            coverage = Math.max(0, Math.min(100, coverage));

            String status;
            if (coverage >= 80) status = "Job Ready";
            else if (coverage >= 65) status = "Near Ready";
            else if (coverage >= 45) status = "Developing";
            else status = "Needs Upskilling";

            comparisons.add(new RoleGapComparison(
                    p.getRoleName(),
                    coverage,
                    matched,
                    partial,
                    missing,
                    status
            ));
        }

        // Sort by coverage percentage descending
        comparisons.sort(Comparator.comparingInt(RoleGapComparison::getCoveragePercentage).reversed());
        return comparisons;
    }

    private String generateStrategicAdvice(RoleProfile profile, int coverage, String readiness,
                                           List<SkillItem> partial, List<SkillItem> missing) {
        StringBuilder sb = new StringBuilder();
        sb.append("Your resume currently shows a ").append(coverage).append("% skill coverage for ")
          .append(profile.getRoleName()).append(" (Status: ").append(readiness).append("). ");

        if (!partial.isEmpty()) {
            sb.append("You have fast-track opportunities in ")
              .append(partial.get(0).getName())
              .append(" because of your existing foundational experience. ");
        }

        if (!missing.isEmpty()) {
            sb.append("Focus primarily on closing core high-priority gaps in ")
              .append(missing.get(0).getName())
              .append(" by building a practical portfolio project to demonstrate hands-on competence.");
        } else {
            sb.append("You possess all core required competencies! Highlight quantifiable project achievements and system architecture experience in your resume.");
        }

        return sb.toString();
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
