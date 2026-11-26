package com.mahesh.ai_resume_backend;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mahesh.ai_resume_backend.model.AtsResult;
import com.mahesh.ai_resume_backend.model.Resume;
import com.mahesh.ai_resume_backend.service.AtsService;
import com.mahesh.ai_resume_backend.service.SkillService;

class ResumeBuilderTest {

    private AtsService atsService;
    private SkillService skillService;

    @BeforeEach
    void setUp() {
        skillService = new SkillService();
        atsService = new AtsService();
    }

    @Test
    void testResumeEntity_ResumeDataPersistence() {
        Resume resume = new Resume("Software Engineer Resume", "Java, Spring Boot, MySQL");
        String sampleJson = "{\"personalInfo\":{\"fullName\":\"Alex Mercer\",\"title\":\"Backend Engineer\"}}";
        resume.setResumeData(sampleJson);
        resume.setAtsScore(88);
        resume.setMatchPercentage(92);

        assertEquals("Software Engineer Resume", resume.getResumeName());
        assertEquals("Java, Spring Boot, MySQL", resume.getSkills());
        assertEquals(sampleJson, resume.getResumeData());
        assertEquals(88, resume.getAtsScore());
        assertEquals(92, resume.getMatchPercentage());
    }

    @Test
    void testBuilderGeneratedText_IsAtsCompatible() {
        // Simulated structured resume text generated from builder
        String atsResumeText = "ALEX MERCER\n"
                + "Full Stack Software Engineer\n"
                + "alex.mercer@example.com | +1 (555) 234-5678 | San Francisco, CA\n"
                + "linkedin.com/in/alexmercer | github.com/alexmercer\n\n"
                + "PROFESSIONAL SUMMARY\n"
                + "Innovative and results-driven Software Engineer with 4+ years of experience designing and deploying scalable web applications, microservices, and high-throughput REST APIs using Java, Spring Boot, React, and MySQL.\n\n"
                + "TECHNICAL SKILLS\n"
                + "Programming Languages: Java, JavaScript, TypeScript, Python, SQL\n"
                + "Frameworks & Libraries: Spring Boot, React, Node.js, Tailwind CSS\n"
                + "Tools & Platforms: Docker, Git, GitHub, Linux, AWS, CI/CD\n"
                + "Databases: MySQL, PostgreSQL, MongoDB, Redis\n\n"
                + "PROFESSIONAL EXPERIENCE\n"
                + "Senior Software Engineer | TechCorp Inc. | 2022 - Present\n"
                + "- Architected microservices backend with Spring Boot and PostgreSQL, decreasing API latency by 42%.\n"
                + "- Led migration of legacy monolith to containerized Docker environment with automated CI/CD.\n"
                + "- Spearheaded React UI enhancements using Tailwind CSS, improving user conversion rates by 28%.\n\n"
                + "EDUCATION\n"
                + "Bachelor of Science in Computer Science | Stanford University | 2018 - 2022 | GPA: 3.8\n\n"
                + "PROJECTS\n"
                + "Distributed E-Commerce Platform | Java, Spring Boot, React, Docker\n"
                + "- Engineered resilient payment and checkout service processing over 10,000 requests per minute.\n"
                + "- Implemented in-memory Redis caching and JWT authentication for secure session management.\n\n"
                + "CERTIFICATIONS\n"
                + "- AWS Certified Solutions Architect - Associate | Amazon Web Services | 2023\n"
                + "- Oracle Certified Professional: Java SE 17 Developer | Oracle | 2022\n\n"
                + "ACHIEVEMENTS\n"
                + "- 1st Place Winner - National Hackathon 2023 for AI-driven workflow optimization.\n"
                + "- Published technical article on microservices caching strategies viewed by 15,000+ engineers.\n";

        // Validate that SkillService parses the skills
        List<String> detectedSkills = skillService.detectSkills(atsResumeText.toLowerCase());
        assertNotNull(detectedSkills);
        assertTrue(detectedSkills.contains("Java"));
        assertTrue(detectedSkills.contains("Spring Boot"));
        assertTrue(detectedSkills.contains("React"));
        assertTrue(detectedSkills.contains("SQL"));
        assertTrue(detectedSkills.contains("MySQL"));
        assertTrue(detectedSkills.contains("Git"));

        // Validate that AtsService evaluates the resume with a high score
        AtsResult atsResult = atsService.analyzeAts(atsResumeText, null);
        assertNotNull(atsResult);
        assertTrue(atsResult.getScore() >= 80, "Builder resume text should score >= 80 on general ATS check, got: " + atsResult.getScore());
        assertTrue(atsResult.getMissingSections().isEmpty(), "Standard builder sections should all be detected");
        assertTrue(atsResult.getDetectedSections().contains("Contact Information"));
        assertTrue(atsResult.getDetectedSections().contains("Skills"));
        assertTrue(atsResult.getDetectedSections().contains("Experience / Projects"));
        assertTrue(atsResult.getDetectedSections().contains("Education"));
    }
}
