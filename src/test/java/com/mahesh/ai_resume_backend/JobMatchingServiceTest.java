package com.mahesh.ai_resume_backend;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.mahesh.ai_resume_backend.service.JobMatchingService;

class JobMatchingServiceTest {

    private final JobMatchingService matchingService = new JobMatchingService();

    @Test
    void testAnalyzeMatchingAndMissing() {
        String resume = "I have experience in Java and SQL and React.";
        String job = "Looking for a developer with Java, Python, and SQL knowledge.";

        Map<String, Object> result = matchingService.analyze(resume, job);

        @SuppressWarnings("unchecked")
        List<String> jobSkills = (List<String>) result.get("jobSkills");
        @SuppressWarnings("unchecked")
        List<String> matching = (List<String>) result.get("matchingSkills");
        @SuppressWarnings("unchecked")
        List<String> missing = (List<String>) result.get("missingSkills");
        Integer matchPercentage = (Integer) result.get("matchPercentage");

        assertNotNull(jobSkills);
        assertTrue(jobSkills.contains("Java"));
        assertTrue(jobSkills.contains("Python"));
        assertTrue(jobSkills.contains("SQL"));

        assertTrue(matching.contains("Java"));
        assertTrue(matching.contains("SQL"));
        assertTrue(missing.contains("Python"));

        // 2 matched out of 3 = 66%
        assertEquals(66, matchPercentage);
    }
}
