package com.mahesh.ai_resume_backend;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mahesh.ai_resume_backend.model.ProjectSuggestion;
import com.mahesh.ai_resume_backend.model.RoadmapPhase;
import com.mahesh.ai_resume_backend.model.RoleGapComparison;
import com.mahesh.ai_resume_backend.model.SkillGapResponse;
import com.mahesh.ai_resume_backend.model.SkillItem;
import com.mahesh.ai_resume_backend.service.JobRecommendationService;
import com.mahesh.ai_resume_backend.service.SkillGapService;
import com.mahesh.ai_resume_backend.service.SkillService;

class SkillGapServiceTest {

    private SkillGapService skillGapService;

    @BeforeEach
    void setUp() {
        SkillService skillService = new SkillService();
        JobRecommendationService jobRecommendationService = new JobRecommendationService(skillService);
        skillGapService = new SkillGapService(jobRecommendationService);
    }

    @Test
    void testAnalyzeSkillGap_JavaDeveloper_HighCoverage() {
        String resume = "Alex Mercer\n"
                + "alex@example.com\n\n"
                + "PROFESSIONAL SUMMARY\n"
                + "Java Backend Engineer with experience in Spring Boot and REST APIs.\n\n"
                + "SKILLS\n"
                + "Java, Spring Boot, SQL, MySQL, REST API, Git, Docker, Unit Testing\n\n"
                + "EXPERIENCE\n"
                + "- Developed RESTful APIs using Java and Spring Boot.\n"
                + "- Managed MySQL schemas and executed queries.\n";

        SkillGapResponse response = skillGapService.analyzeSkillGap(resume, "Java Developer");

        assertNotNull(response);
        assertEquals("Java Developer", response.getTargetRole());
        assertTrue(response.getCoveragePercentage() >= 70, "Coverage should be >= 70%");
        assertTrue(response.getMatchedCount() > 0);
        assertFalse(response.getMatchedSkills().isEmpty());

        // Check matched skills contain Java, Spring Boot, etc.
        boolean hasJava = response.getMatchedSkills().stream().anyMatch(s -> "Java".equalsIgnoreCase(s.getName()));
        boolean hasSpringBoot = response.getMatchedSkills().stream().anyMatch(s -> "Spring Boot".equalsIgnoreCase(s.getName()));
        assertTrue(hasJava, "Java should be matched");
        assertTrue(hasSpringBoot, "Spring Boot should be matched");

        // Verify roadmap phases
        List<RoadmapPhase> phases = response.getRoadmapPhases();
        assertNotNull(phases);
        assertEquals(3, phases.size());

        // Verify project suggestions
        List<ProjectSuggestion> projects = response.getProjectSuggestions();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());

        // Verify multi-role comparisons
        List<RoleGapComparison> comparisons = response.getRoleComparisons();
        assertNotNull(comparisons);
        assertEquals(6, comparisons.size());
    }

    @Test
    void testAnalyzeSkillGap_PartialCoverageDetection() {
        // Candidate has JavaScript, CSS, and MySQL, but not TypeScript, Tailwind, or PostgreSQL
        String resume = "Jordan Lee\n"
                + "jordan@example.com\n\n"
                + "SKILLS\n"
                + "JavaScript, CSS, HTML, MySQL, Git\n\n"
                + "EXPERIENCE\n"
                + "Frontend developer building web interfaces with JavaScript and CSS.\n";

        SkillGapResponse response = skillGapService.analyzeSkillGap(resume, "Full Stack Developer");

        assertNotNull(response);
        assertEquals("Full Stack Developer", response.getTargetRole());

        // TypeScript should be partially covered via JavaScript
        List<SkillItem> partials = response.getPartiallyCoveredSkills();
        assertNotNull(partials);
        boolean tsPartial = partials.stream().anyMatch(s -> "TypeScript".equalsIgnoreCase(s.getName()));
        assertTrue(tsPartial, "TypeScript should be partially covered when JavaScript is present");

        SkillItem tsItem = partials.stream().filter(s -> "TypeScript".equalsIgnoreCase(s.getName())).findFirst().orElse(null);
        assertNotNull(tsItem);
        assertEquals("PARTIAL", tsItem.getStatus());
        assertTrue(tsItem.getReason().contains("JavaScript"));
        assertEquals("ADVANCED", tsItem.getLearningLevel());

        // Tailwind should be partially covered via CSS
        boolean tailwindPartial = partials.stream().anyMatch(s -> "Tailwind".equalsIgnoreCase(s.getName()));
        assertTrue(tailwindPartial, "Tailwind should be partially covered when CSS is present");
    }

    @Test
    void testAnalyzeSkillGap_MissingSkillsCategorizationAndPriority() {
        String resume = "Candidate with minimal skills\n"
                + "SKILLS\n"
                + "HTML, Git\n";

        SkillGapResponse response = skillGapService.analyzeSkillGap(resume, "Java Developer");

        assertNotNull(response);
        List<SkillItem> missing = response.getMissingSkills();
        assertFalse(missing.isEmpty());

        // Java is primary skill -> High priority
        SkillItem javaSkill = missing.stream().filter(s -> "Java".equalsIgnoreCase(s.getName())).findFirst().orElse(null);
        assertNotNull(javaSkill);
        assertEquals("HIGH", javaSkill.getPriority());
        assertEquals("INTERMEDIATE", javaSkill.getLearningLevel());

        // Docker or Microservices is secondary -> Medium priority
        SkillItem dockerSkill = missing.stream().filter(s -> "Docker".equalsIgnoreCase(s.getName())).findFirst().orElse(null);
        if (dockerSkill != null) {
            assertEquals("MEDIUM", dockerSkill.getPriority());
            assertEquals("ADVANCED", dockerSkill.getLearningLevel());
        }
    }

    @Test
    void testAnalyzeSkillGap_RoleSwitching() {
        String resume = "Docker, Kubernetes, Linux, AWS, Git, CI/CD, Python\n";

        SkillGapResponse devopsResponse = skillGapService.analyzeSkillGap(resume, "DevOps / Cloud Engineer");
        assertNotNull(devopsResponse);
        assertEquals("DevOps / Cloud Engineer", devopsResponse.getTargetRole());
        assertTrue(devopsResponse.getCoveragePercentage() >= 65);
        assertTrue(devopsResponse.getCoveragePercentage() > 50);

        // Switch to Frontend Developer
        SkillGapResponse frontendResponse = skillGapService.analyzeSkillGap(resume, "Frontend Developer");
        assertNotNull(frontendResponse);
        assertEquals("Frontend Developer", frontendResponse.getTargetRole());
        assertTrue(frontendResponse.getCoveragePercentage() < devopsResponse.getCoveragePercentage());
    }

    @Test
    void testAnalyzeSkillGap_EmptyInputValidation() {
        assertThrows(IllegalArgumentException.class, () -> skillGapService.analyzeSkillGap(null, "Java Developer"));
        assertThrows(IllegalArgumentException.class, () -> skillGapService.analyzeSkillGap("   ", "Java Developer"));
    }
}
