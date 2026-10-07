package com.mahesh.ai_resume_backend.service;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class JobMatchingService {

    public Map<String, Object> analyze(
            String resumeText,
            String jobDescription) {

        String resume = resumeText.toLowerCase();
        String job = jobDescription.toLowerCase();

        String[] skillList = {
            "Java",
            "Python",
            "C++",
            "JavaScript",
            "HTML",
            "CSS",
            "React",
            "SQL",
            "MySQL",
            "Spring Boot",
            "Git",
            "GitHub",
            "Data Structures",
            "DSA"
        };

        List<String> jobSkills = new ArrayList<>();
        List<String> matchingSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

        for (String skill : skillList) {

            if (job.contains(skill.toLowerCase())) {

                jobSkills.add(skill);

                if (resume.contains(skill.toLowerCase())) {
                    matchingSkills.add(skill);
                } else {
                    missingSkills.add(skill);
                }
            }
        }

        int matchPercentage = 0;

        if (!jobSkills.isEmpty()) {
            matchPercentage =
                    (matchingSkills.size() * 100) / jobSkills.size();
        }

        Map<String, Object> result = new HashMap<>();

        result.put("jobSkills", jobSkills);
        result.put("matchingSkills", matchingSkills);
        result.put("missingSkills", missingSkills);
        result.put("matchPercentage", matchPercentage);

        return result;
    }
}