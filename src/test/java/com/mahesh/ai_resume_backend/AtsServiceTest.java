package com.mahesh.ai_resume_backend;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mahesh.ai_resume_backend.model.AtsResult;
import com.mahesh.ai_resume_backend.service.AtsService;

class AtsServiceTest {

    private AtsService atsService;

    @BeforeEach
    void setUp() {
        atsService = new AtsService();
    }

    @Test
    void testGeneralAtsCheck() {
        String resume = "John Doe\njohn.doe@example.com | +1-555-123-4567 | linkedin.com/in/johndoe\n\n"
                + "PROFESSIONAL SUMMARY\nResults-driven Software Engineer with 3+ years experience.\n\n"
                + "EDUCATION\nBachelor of Science in Computer Science, State University\n\n"
                + "TECHNICAL SKILLS\nJava, Spring Boot, MySQL, React, Git, Docker, REST API\n\n"
                + "WORK EXPERIENCE\nSoftware Engineer at TechCorp\n"
                + "- Developed scalable backend services using Java and Spring Boot.\n"
                + "- Designed RESTful APIs and optimized SQL database queries.\n"
                + "- Implemented automated unit tests and deployed containerized microservices.";

        AtsResult result = atsService.analyzeAts(resume, null);

        assertNotNull(result);
        assertEquals("GENERAL", result.getMode());
        assertTrue(result.getScore() >= 65, "Score should be at least 65 for a well-rounded resume");
        assertTrue(result.getDetectedSections().contains("Contact Information"));
        assertTrue(result.getDetectedSections().contains("Summary / Objective"));
        assertTrue(result.getDetectedSections().contains("Education"));
        assertTrue(result.getDetectedSections().contains("Skills"));
        assertTrue(result.getDetectedSections().contains("Experience / Projects"));
        assertTrue(result.getMatchingKeywords().contains("Java"));
        assertTrue(result.getMatchingKeywords().contains("Spring Boot"));
        assertNotNull(result.getDisclaimer());
    }

    @Test
    void testTargetedAtsCheck() {
        String resume = "Jane Smith\njane.smith@example.com | 9876543210\n\n"
                + "SUMMARY\nFull-Stack Developer passionate about web apps.\n\n"
                + "EDUCATION\nB.Tech in Computer Science\n\n"
                + "SKILLS\nJava, React, SQL, HTML, CSS\n\n"
                + "PROJECTS\n"
                + "- Built an e-commerce platform using React and Java.\n"
                + "- Developed database schemas in SQL and optimized query performance.";

        String jobDescription = "Looking for a Software Engineer proficient in Java, React, Docker, and Kubernetes.";

        AtsResult result = atsService.analyzeAts(resume, jobDescription);

        assertNotNull(result);
        assertEquals("TARGETED", result.getMode());
        assertTrue(result.getMatchingKeywords().contains("Java"));
        assertTrue(result.getMatchingKeywords().contains("React"));
        assertTrue(result.getMissingKeywords().contains("Docker"));
        assertTrue(result.getMissingKeywords().contains("Kubernetes"));
        assertTrue(result.getCategoryScores().containsKey("keywordScore"));
    }

    @Test
    void testEmptyResumeThrows() {
        assertThrows(IllegalArgumentException.class, () -> atsService.analyzeAts("", ""));
        assertThrows(IllegalArgumentException.class, () -> atsService.analyzeAts("   ", null));
        assertThrows(IllegalArgumentException.class, () -> atsService.analyzeAts(null, null));
    }

    @Test
    void testMissingSectionsDetection() {
        String bareResume = "Developer with some coding experience in Java.";

        AtsResult result = atsService.analyzeAts(bareResume, "");

        assertNotNull(result);
        assertTrue(result.getMissingSections().contains("Contact Information"));
        assertTrue(result.getMissingSections().contains("Summary / Objective"));
        assertTrue(result.getMissingSections().contains("Education"));
        assertFalse(result.getSuggestions().isEmpty());
    }
}
