package com.mahesh.ai_resume_backend;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.mahesh.ai_resume_backend.service.SkillService;

class SkillServiceTest {

    private final SkillService skillService = new SkillService();

    @Test
    void testDetectSkills() {
        String resumeText = "Experienced software engineer proficient in Java, Spring Boot, MySQL, and React with Git.";
        List<String> skills = skillService.detectSkills(resumeText);

        assertTrue(skills.contains("Java"));
        assertTrue(skills.contains("Spring Boot"));
        assertTrue(skills.contains("MySQL"));
        assertTrue(skills.contains("React"));
        assertTrue(skills.contains("Git"));
    }

    @Test
    void testDetectSkillsEmpty() {
        List<String> skills = skillService.detectSkills("");
        assertTrue(skills.isEmpty());
    }
}
