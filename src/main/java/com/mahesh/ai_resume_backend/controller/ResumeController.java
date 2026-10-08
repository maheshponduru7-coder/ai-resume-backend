package com.mahesh.ai_resume_backend.controller;

import java.util.List;
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

        List<String> skills =
                skillService.detectSkills(resumeText);

        return new ResumeAnalysisResponse(
                skills,
                skills.size(),
                "Resume analyzed successfully"
        );
    }

    @PostMapping("/api/resume/match")
    public Map<String, Object> matchResume(
            @RequestBody JobDescriptionRequest request) {

        return jobMatchingService.analyze(
                request.getResumeText(),
                request.getJobDescription()
        );
    }
    @PostMapping("/api/resume/save")
public Resume saveResume(@RequestBody Resume resume) {
    return resumeRepository.save(resume);
}

@GetMapping("/api/resume/history")
public List<Resume> getResumeHistory() {
    return resumeRepository.findAll();
}
}