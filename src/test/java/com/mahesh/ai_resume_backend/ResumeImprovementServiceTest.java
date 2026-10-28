package com.mahesh.ai_resume_backend;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mahesh.ai_resume_backend.model.ResumeImprovementResponse;
import com.mahesh.ai_resume_backend.service.AtsService;
import com.mahesh.ai_resume_backend.service.ResumeImprovementService;

class ResumeImprovementServiceTest {

    private ResumeImprovementService service;

    @BeforeEach
    void setUp() {
        AtsService atsService = new AtsService();
        service = new ResumeImprovementService(atsService);
    }

    @Test
    void testGenerateSuggestionsGeneral() {
        String resume = "John Doe\njohn.doe@example.com | 555-123-4567\n\n"
                + "PROFESSIONAL SUMMARY\nSoftware Engineer with Java experience.\n\n"
                + "EDUCATION\nB.S. in Computer Science\n\n"
                + "SKILLS\nJava, Spring Boot, MySQL, Git\n\n"
                + "PROJECTS\n"
                + "- Engineered a banking portal using Java and Spring Boot.\n"
                + "- Optimized database queries improving response by 25%.";

        ResumeImprovementResponse response = service.generateSuggestions(resume, null);

        assertNotNull(response);
        assertEquals("GENERAL", response.getMode());
        assertTrue(response.getOverallHealthScore() >= 60);
        assertNotNull(response.getCategories());
        assertEquals(5, response.getCategories().size());
        assertFalse(response.getPriorityActionItems().isEmpty());
        assertFalse(response.getStrengths().isEmpty());
        assertNotNull(response.getDisclaimer());
    }

    @Test
    void testGenerateSuggestionsTargeted() {
        String resume = "Jane Smith\njane@example.com | 123-456-7890\n\n"
                + "SUMMARY\nWeb developer.\n\n"
                + "EDUCATION\nBachelor of Science\n\n"
                + "SKILLS\nJava, React\n\n"
                + "EXPERIENCE\n"
                + "- Built client websites with React.";

        String jobDescription = "Seeking Senior Software Engineer with Java, React, Docker, Kubernetes, and AWS experience.";

        ResumeImprovementResponse response = service.generateSuggestions(resume, jobDescription);

        assertNotNull(response);
        assertEquals("TARGETED", response.getMode());
        assertTrue(response.getMissingKeywords().contains("Docker"));
        assertTrue(response.getMissingKeywords().contains("Kubernetes"));
        assertTrue(response.getMatchingKeywords().contains("Java"));
        assertTrue(response.getMatchingKeywords().contains("React"));

        // Check that skill-gaps category is marked HIGH priority
        boolean foundSkillGaps = response.getCategories().stream()
                .anyMatch(c -> c.getId().equals("skill-gaps") && c.getPriority().equals("HIGH"));
        assertTrue(foundSkillGaps, "Skill gaps should be flagged as HIGH priority when target job keywords are missing");
    }

    @Test
    void testWeakVerbsDetection() {
        String resumeWithPassiveVerbs = "Alex\nalex@example.com\n\n"
                + "EXPERIENCE\n"
                + "- Was responsible for creating database schemas.\n"
                + "- Worked on the frontend interface and helped with bug fixing.";

        ResumeImprovementResponse response = service.generateSuggestions(resumeWithPassiveVerbs, "");

        assertNotNull(response);
        assertTrue(response.getWeakVerbsFound().contains("responsible for"));
        assertTrue(response.getWeakVerbsFound().contains("worked on"));
        assertTrue(response.getWeakVerbsFound().contains("helped with"));
        assertFalse(response.getSuggestedPowerVerbs().isEmpty());
    }

    @Test
    void testMetricsDetection() {
        String noMetrics = "Alex\nalex@example.com\n\nEDUCATION\nDegree\n\nEXPERIENCE\nBuilt web app.";
        ResumeImprovementResponse responseNoMetrics = service.generateSuggestions(noMetrics, "");
        assertFalse(responseNoMetrics.isHasQuantifiableMetrics());

        String withMetrics = "Alex\nalex@example.com\n\nEDUCATION\nDegree\n\nEXPERIENCE\n- Boosted throughput by 35% across 5000 users.";
        ResumeImprovementResponse responseWithMetrics = service.generateSuggestions(withMetrics, "");
        assertTrue(responseWithMetrics.isHasQuantifiableMetrics());
        assertTrue(responseWithMetrics.getMetricsCount() >= 2);
    }

    @Test
    void testEmptyResumeThrows() {
        assertThrows(IllegalArgumentException.class, () -> service.generateSuggestions("", ""));
        assertThrows(IllegalArgumentException.class, () -> service.generateSuggestions("   ", null));
        assertThrows(IllegalArgumentException.class, () -> service.generateSuggestions(null, null));
    }
}
