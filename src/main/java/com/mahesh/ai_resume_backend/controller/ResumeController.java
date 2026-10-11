
package com.mahesh.ai_resume_backend.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mahesh.ai_resume_backend.repository.ResumeRepository;
import com.mahesh.ai_resume_backend.model.JobDescriptionRequest;
import com.mahesh.ai_resume_backend.model.Resume;
import com.mahesh.ai_resume_backend.model.ResumeAnalysisResponse;
import com.mahesh.ai_resume_backend.service.JobMatchingService;
import com.mahesh.ai_resume_backend.service.SkillService;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class ResumeController {

    private final SkillService skillService;
    private final JobMatchingService jobMatchingService;
    private final ResumeRepository resumeRepository;

    public ResumeController(
            SkillService skillService,
            JobMatchingService jobMatchingService,
            ResumeRepository resumeRepository) {
        this.skillService = skillService;
        this.jobMatchingService = jobMatchingService;
        this.resumeRepository = resumeRepository;
    }

    @PostMapping("/api/resume/analyze")
    public ResumeAnalysisResponse analyzeResume(
            @RequestBody String resumeText) {

        List<String> skills = skillService.detectSkills(resumeText);

        return new ResumeAnalysisResponse(
                skills,
                skills.size(),
                "Resume analyzed successfully");
    }

    @PostMapping("/api/resume/match")
    public Map<String, Object> matchResume(
            @RequestBody JobDescriptionRequest request) {

        return jobMatchingService.analyze(
                request.getResumeText(),
                request.getJobDescription());
    }

    @PostMapping("/api/resume/save")
    public Resume saveResume(@RequestBody Resume resume) {

        String name = resume.getResumeName();

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Resume name cannot be empty");
        }

        name = name.trim();

        List<Resume> matches =
                resumeRepository.findByResumeNameIgnoreCaseOrderByIdAsc(name);

        if (matches.isEmpty()) {
            resume.setResumeName(name);
            return resumeRepository.save(resume);
        }

        // Keep the oldest record for this filename.
        Resume existing = matches.get(0);
        existing.setResumeName(name);
        existing.setSkills(resume.getSkills());

        // Remove any older duplicate entries beyond the first.
        if (matches.size() > 1) {
            resumeRepository.deleteAll(
                    matches.subList(1, matches.size()));
        }

        return resumeRepository.save(existing);
    }

    @GetMapping("/api/resume/history")
    public List<Resume> getResumeHistory() {

        List<Resume> allResumes =
                resumeRepository.findAllByOrderByIdAsc();

        Map<String, Resume> uniqueResumes = new HashMap<>();
        List<Resume> duplicates = new ArrayList<>();

        for (Resume resume : allResumes) {
            String name = resume.getResumeName();

            if (name == null || name.trim().isEmpty()) {
                // Keep records that have no usable filename.
                uniqueResumes.put(
                        "__missing_name_" + resume.getId(), resume);
                continue;
            }

            String key = name.trim().toLowerCase(Locale.ROOT);

            if (uniqueResumes.containsKey(key)) {
                duplicates.add(resume);
            } else {
                uniqueResumes.put(key, resume);
            }
        }

        if (!duplicates.isEmpty()) {
            resumeRepository.deleteAll(duplicates);
        }

        return new ArrayList<>(uniqueResumes.values());
    }
}
