package com.mahesh.ai_resume_backend.service;


import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class SkillService {

    public List<String> detectSkills(String resumeText) {

        List<String> skills = new ArrayList<>();

        String text = resumeText.toLowerCase();

        if (text.contains("java")) {
            skills.add("Java");
        }

        if (text.contains("python")) {
            skills.add("Python");
        }

        if (text.contains("c++")) {
            skills.add("C++");
        }

        if (text.contains("javascript")) {
            skills.add("JavaScript");
        }

        if (text.contains("html")) {
            skills.add("HTML");
        }

        if (text.contains("css")) {
            skills.add("CSS");
        }

        if (text.contains("react")) {
            skills.add("React");
        }

        if (text.contains("sql")) {
            skills.add("SQL");
        }

        if (text.contains("mysql")) {
            skills.add("MySQL");
        }

        if (text.contains("spring boot")) {
            skills.add("Spring Boot");
        }

        if (text.contains("git")) {
            skills.add("Git");
        }

        if (text.contains("github")) {
            skills.add("GitHub");
        }

        if (text.contains("data structures")) {
            skills.add("Data Structures");
        }

        if (text.contains("dsa")) {
            skills.add("DSA");
        }

        return skills;
    }
}
