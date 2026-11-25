package com.mahesh.ai_resume_backend;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mahesh.ai_resume_backend.model.JobRecommendationResponse;
import com.mahesh.ai_resume_backend.model.JobRoleRecommendation;
import com.mahesh.ai_resume_backend.service.JobRecommendationService;
import com.mahesh.ai_resume_backend.service.SkillService;

class JobRecommendationServiceTest {

    private JobRecommendationService recommendationService;

    @BeforeEach
    void setUp() {
        SkillService skillService = new SkillService();
        recommendationService = new JobRecommendationService(skillService);
    }

    @Test
    void testRecommendJobs_JavaDeveloperMatch() {
        String resume = "John Doe\n"
                + "john.doe@example.com | 555-123-4567\n\n"
                + "PROFESSIONAL SUMMARY\n"
                + "Backend software engineer with 3 years of experience building Java microservices.\n\n"
                + "SKILLS\n"
                + "Java, Spring Boot, MySQL, SQL, REST API, Git, Microservices, Docker, Unit Testing\n\n"
                + "EXPERIENCE\n"
                + "- Architected and deployed Java Spring Boot REST APIs.\n"
                + "- Optimized MySQL database queries reducing latency by 35%.\n";

        JobRecommendationResponse response = recommendationService.recommendJobs(resume);

        assertNotNull(response);
        assertFalse(response.getRecommendations().isEmpty());
        assertTrue(response.getCandidateSkills().contains("Java"));
        assertTrue(response.getCandidateSkills().contains("Spring Boot"));

        // Java Developer should be near or at the top
        JobRoleRecommendation topRole = response.getRecommendations().get(0);
        assertTrue(topRole.getRoleName().equals("Java Developer") || topRole.getRoleName().equals("Backend Developer"));
        assertTrue(topRole.getMatchPercentage() >= 75, "Match percentage should be >= 75%");
        assertNotNull(topRole.getFitLevel());
        assertFalse(topRole.getMatchingSkills().isEmpty());
        assertFalse(topRole.getActionableSuggestions().isEmpty());
        assertNotNull(response.getOverallAdvice());
        assertNotNull(response.getDisclaimer());
    }

    @Test
    void testRecommendJobs_FullStackMatch() {
        String resume = "Sarah Connor\n"
                + "sarah@example.com\n\n"
                + "SKILLS\n"
                + "React, JavaScript, HTML, CSS, Tailwind, Node.js, SQL, Git, REST API\n\n"
                + "PROJECTS\n"
                + "Built full stack web application with React and Node.js.\n";

        JobRecommendationResponse response = recommendationService.recommendJobs(resume);

        assertNotNull(response);
        List<JobRoleRecommendation> recs = response.getRecommendations();

        JobRoleRecommendation fullStackRec = recs.stream()
                .filter(r -> r.getRoleName().equals("Full Stack Developer"))
                .findFirst()
                .orElse(null);

        assertNotNull(fullStackRec);
        assertTrue(fullStackRec.getMatchPercentage() >= 65);
        assertTrue(fullStackRec.getMatchingSkills().contains("React"));
        assertTrue(fullStackRec.getMatchingSkills().contains("JavaScript"));
    }

    @Test
    void testRecommendJobs_SoftwareEngineerMatch() {
        String resume = "David Smith\n"
                + "david@example.com\n\n"
                + "EDUCATION\n"
                + "B.Tech in Computer Science\n\n"
                + "SKILLS\n"
                + "Data Structures, Algorithms, DSA, OOP, Problem Solving, C++, Java, Git\n\n"
                + "EXPERIENCE\n"
                + "Solved 400+ problems on LeetCode covering dynamic programming and graphs.\n";

        JobRecommendationResponse response = recommendationService.recommendJobs(resume);

        assertNotNull(response);
        JobRoleRecommendation sweRec = response.getRecommendations().stream()
                .filter(r -> r.getRoleName().equals("Software Engineer"))
                .findFirst()
                .orElse(null);

        assertNotNull(sweRec);
        assertTrue(sweRec.getMatchPercentage() >= 70);
        assertTrue(sweRec.getMatchingSkills().contains("Data Structures") || sweRec.getMatchingSkills().contains("DSA"));
        assertFalse(sweRec.getActionableSuggestions().isEmpty());
    }

    @Test
    void testRecommendJobs_MissingSkillsAndGaps() {
        // Very basic resume
        String resume = "Junior Coder\nSkills: HTML, CSS\n";

        JobRecommendationResponse response = recommendationService.recommendJobs(resume);
        assertNotNull(response);
        assertFalse(response.getRecommendations().isEmpty());

        for (JobRoleRecommendation rec : response.getRecommendations()) {
            assertFalse(rec.getMissingSkills().isEmpty(), "Roles should identify missing skills for basic resume");
            assertNotNull(rec.getActionableSuggestions());
            assertFalse(rec.getActionableSuggestions().isEmpty());
        }
    }

    @Test
    void testRecommendJobs_InvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> recommendationService.recommendJobs(null));
        assertThrows(IllegalArgumentException.class, () -> recommendationService.recommendJobs("   "));
    }
}
