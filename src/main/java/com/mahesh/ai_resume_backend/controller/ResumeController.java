package com.mahesh.ai_resume_backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mahesh.ai_resume_backend.model.JobDescriptionRequest;
import com.mahesh.ai_resume_backend.model.Resume;
import com.mahesh.ai_resume_backend.model.ResumeAnalysisResponse;
import com.mahesh.ai_resume_backend.model.User;
import com.mahesh.ai_resume_backend.repository.ResumeRepository;
import com.mahesh.ai_resume_backend.repository.UserRepository;
import com.mahesh.ai_resume_backend.security.UserPrincipal;
import com.mahesh.ai_resume_backend.service.JobMatchingService;
import com.mahesh.ai_resume_backend.service.SkillService;

@RestController
@CrossOrigin(origins = "*")
public class ResumeController {

    private final SkillService skillService;
    private final JobMatchingService jobMatchingService;
    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;

    public ResumeController(
            SkillService skillService,
            JobMatchingService jobMatchingService,
            ResumeRepository resumeRepository,
            UserRepository userRepository) {
        this.skillService = skillService;
        this.jobMatchingService = jobMatchingService;
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
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
    public ResponseEntity<?> saveResume(
            @RequestBody Resume resume,
            @AuthenticationPrincipal UserPrincipal principal) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "You must be logged in to save resumes to your history."));
        }

        User user = userRepository.findById(principal.getId()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Authenticated user not found."));
        }

        String name = resume.getResumeName();
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Resume name cannot be empty"));
        }

        name = name.trim();

        // Scope lookup strictly to the authenticated user's resumes
        List<Resume> userMatches =
                resumeRepository.findByUserIdAndResumeNameIgnoreCaseOrderByIdAsc(user.getId(), name);

        if (userMatches.isEmpty()) {
            resume.setResumeName(name);
            resume.setUser(user);
            Resume saved = resumeRepository.save(resume);
            return ResponseEntity.ok(saved);
        }

        // Update the existing record for this user
        Resume existing = userMatches.get(0);
        existing.setResumeName(name);
        existing.setSkills(resume.getSkills());

        // Remove any redundant duplicates for this user only
        if (userMatches.size() > 1) {
            resumeRepository.deleteAll(userMatches.subList(1, userMatches.size()));
        }

        Resume updated = resumeRepository.save(existing);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/api/resume/history")
    public ResponseEntity<?> getResumeHistory(@AuthenticationPrincipal UserPrincipal principal) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "You must be logged in to view your resume history."));
        }

        // Return ONLY the authenticated user's resumes, newest first
        List<Resume> userResumes = resumeRepository.findByUserIdOrderByIdDesc(principal.getId());
        return ResponseEntity.ok(userResumes);
    }

    @DeleteMapping("/api/resume/{id}")
    public ResponseEntity<?> deleteResume(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "You must be logged in to delete a resume."));
        }

        Resume resume = resumeRepository.findById(id).orElse(null);
        if (resume == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Resume not found"));
        }

        // Ensure user can only delete their own resume
        if (resume.getUser() == null || !resume.getUser().getId().equals(principal.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Access denied: You cannot delete another user's resume."));
        }

        resumeRepository.delete(resume);
        return ResponseEntity.ok(Map.of("message", "Resume deleted successfully", "id", id));
    }
}
