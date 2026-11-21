package com.mahesh.ai_resume_backend;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mahesh.ai_resume_backend.model.ResumeCompareRequest;
import com.mahesh.ai_resume_backend.model.ResumeCompareResponse;
import com.mahesh.ai_resume_backend.service.AtsService;
import com.mahesh.ai_resume_backend.service.ResumeCompareService;
import com.mahesh.ai_resume_backend.service.ResumeImprovementService;
import com.mahesh.ai_resume_backend.service.SkillService;

class ResumeCompareServiceTest {

    private ResumeCompareService compareService;

    @BeforeEach
    void setUp() {
        AtsService atsService = new AtsService();
        ResumeImprovementService improvementService = new ResumeImprovementService(atsService);
        SkillService skillService = new SkillService();
        compareService = new ResumeCompareService(atsService, improvementService, skillService);
    }

    @Test
    void testCompareResumes_ScoreImprovement() {
        // v1: Basic resume with missing summary, weak verbs, no metrics
        String oldResume = "Alex Taylor\n\n"
                + "EXPERIENCE\n"
                + "Worked on software applications.\n"
                + "Responsible for database maintenance.\n\n"
                + "SKILLS\nJava, SQL";

        // v2: Comprehensive resume with contact info, summary, education, skills, power verbs, metrics
        String newResume = "Alex Taylor\n"
                + "alex.taylor@example.com | +1 555-321-4321 | linkedin.com/in/alextaylor\n\n"
                + "PROFESSIONAL SUMMARY\n"
                + "Experienced Full-Stack Software Engineer specializing in scalable cloud applications.\n\n"
                + "EDUCATION\n"
                + "Bachelor of Science in Computer Science, State University\n\n"
                + "TECHNICAL SKILLS\n"
                + "Java, Spring Boot, MySQL, React, Docker, Kubernetes, Git, REST API\n\n"
                + "EXPERIENCE & PROJECTS\n"
                + "- Architected high-throughput microservices using Java and Spring Boot.\n"
                + "- Optimized MySQL database queries reducing API latency by 45%.\n"
                + "- Automated CI/CD deployments using Docker and Kubernetes, serving 100k daily users.";

        ResumeCompareRequest request = new ResumeCompareRequest(oldResume, newResume, null);
        request.setOldResumeName("Alex_v1.pdf");
        request.setNewResumeName("Alex_v2.pdf");

        ResumeCompareResponse response = compareService.compareResumes(request);

        assertNotNull(response);
        assertEquals("Alex_v1.pdf", response.getOldResumeName());
        assertEquals("Alex_v2.pdf", response.getNewResumeName());
        assertEquals("GENERAL", response.getMode());
        assertFalse(response.isHasJobDescription());

        // Score improvements
        assertTrue(response.getNewAtsScore() > response.getOldAtsScore(), "New ATS score should exceed old ATS score");
        assertTrue(response.getAtsScoreDelta() > 0, "ATS delta should be positive");
        assertTrue(response.getNewHealthScore() >= response.getOldHealthScore());

        // Skills diff
        assertTrue(response.getAddedSkills().contains("Spring Boot") || response.getAddedSkills().contains("React"));
        assertTrue(response.getRetainedSkills().contains("Java"));

        // Sections diff
        assertTrue(response.getMissingSectionsResolved().contains("Contact Information")
                || response.getMissingSectionsResolved().contains("Education"));

        // Verbs and metrics
        assertTrue(response.getWeakVerbsResolved().contains("worked on")
                || response.getWeakVerbsResolved().contains("responsible for"));
        assertTrue(response.getMetricsDelta() > 0);

        // Verdict & highlights
        assertNotNull(response.getVerdict());
        assertFalse(response.getKeyImprovements().isEmpty());
        assertNotNull(response.getDisclaimer());
    }

    @Test
    void testCompareResumes_TargetedJobDescription() {
        String oldResume = "Bob Developer\nbob@example.com | 555-000-1111\n\n"
                + "SUMMARY\nWeb developer.\n\n"
                + "SKILLS\nHTML, CSS, JavaScript\n\n"
                + "EXPERIENCE\nBuilt web pages.";

        String newResume = "Bob Developer\nbob@example.com | 555-000-1111\n\n"
                + "SUMMARY\nFull Stack Developer.\n\n"
                + "SKILLS\nHTML, CSS, JavaScript, React, Node.js, TypeScript, AWS, Docker\n\n"
                + "EXPERIENCE\nEngineered web applications with React and AWS.";

        String jobDescription = "Seeking a Full Stack Engineer proficient in React, TypeScript, Node.js, AWS, and Docker.";

        ResumeCompareRequest request = new ResumeCompareRequest(oldResume, newResume, jobDescription);
        ResumeCompareResponse response = compareService.compareResumes(request);

        assertNotNull(response);
        assertEquals("TARGETED", response.getMode());
        assertTrue(response.isHasJobDescription());
        assertFalse(response.getTargetJobKeywords().isEmpty());

        // Targeted keyword alignment
        assertTrue(response.getNewMatchPercentage() > response.getOldMatchPercentage());
        assertTrue(response.getMatchPercentageDelta() > 0);
        assertFalse(response.getNewlyMatchedKeywords().isEmpty());
    }

    @Test
    void testCompareResumes_ValidationExceptions() {
        assertThrows(IllegalArgumentException.class, () -> compareService.compareResumes(null));

        ResumeCompareRequest emptyOld = new ResumeCompareRequest("", "Valid new resume", null);
        assertThrows(IllegalArgumentException.class, () -> compareService.compareResumes(emptyOld));

        ResumeCompareRequest emptyNew = new ResumeCompareRequest("Valid old resume", "   ", null);
        assertThrows(IllegalArgumentException.class, () -> compareService.compareResumes(emptyNew));
    }
}
