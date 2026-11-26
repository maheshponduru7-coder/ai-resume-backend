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

import com.mahesh.ai_resume_backend.model.AtsResult;
import com.mahesh.ai_resume_backend.model.JobDescriptionRequest;
import com.mahesh.ai_resume_backend.model.JobRecommendationRequest;
import com.mahesh.ai_resume_backend.model.JobRecommendationResponse;
import com.mahesh.ai_resume_backend.model.Resume;
import com.mahesh.ai_resume_backend.model.ResumeAnalysisResponse;
import com.mahesh.ai_resume_backend.model.ResumeCompareRequest;
import com.mahesh.ai_resume_backend.model.ResumeCompareResponse;
import com.mahesh.ai_resume_backend.model.ResumeImprovementResponse;
import com.mahesh.ai_resume_backend.model.SkillGapRequest;
import com.mahesh.ai_resume_backend.model.SkillGapResponse;
import com.mahesh.ai_resume_backend.model.User;
import com.mahesh.ai_resume_backend.repository.ResumeRepository;
import com.mahesh.ai_resume_backend.repository.UserRepository;
import com.mahesh.ai_resume_backend.security.UserPrincipal;
import com.mahesh.ai_resume_backend.service.AtsService;
import com.mahesh.ai_resume_backend.service.JobMatchingService;
import com.mahesh.ai_resume_backend.service.JobRecommendationService;
import com.mahesh.ai_resume_backend.service.ResumeCompareService;
import com.mahesh.ai_resume_backend.service.ResumeImprovementService;
import com.mahesh.ai_resume_backend.service.SkillGapService;
import com.mahesh.ai_resume_backend.service.SkillService;

@RestController
@CrossOrigin(origins = "*")
public class ResumeController {

    private final SkillService skillService;
    private final JobMatchingService jobMatchingService;
    private final AtsService atsService;
    private final ResumeImprovementService resumeImprovementService;
    private final ResumeCompareService resumeCompareService;
    private final JobRecommendationService jobRecommendationService;
    private final SkillGapService skillGapService;
    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;

    public ResumeController(
            SkillService skillService,
            JobMatchingService jobMatchingService,
            AtsService atsService,
            ResumeImprovementService resumeImprovementService,
            ResumeCompareService resumeCompareService,
            JobRecommendationService jobRecommendationService,
            SkillGapService skillGapService,
            ResumeRepository resumeRepository,
            UserRepository userRepository) {
        this.skillService = skillService;
        this.jobMatchingService = jobMatchingService;
        this.atsService = atsService;
        this.resumeImprovementService = resumeImprovementService;
        this.resumeCompareService = resumeCompareService;
        this.jobRecommendationService = jobRecommendationService;
        this.skillGapService = skillGapService;
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

    @PostMapping("/api/resume/ats-check")
    public ResponseEntity<?> checkAtsScore(
            @RequestBody JobDescriptionRequest request) {

        if (request == null || request.getResumeText() == null || request.getResumeText().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Resume text cannot be empty. Please upload or provide a resume with readable text."));
        }

        AtsResult result = atsService.analyzeAts(request.getResumeText(), request.getJobDescription());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/api/resume/suggestions")
    public ResponseEntity<?> getImprovementSuggestions(
            @RequestBody JobDescriptionRequest request) {

        if (request == null || request.getResumeText() == null || request.getResumeText().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Resume text cannot be empty. Please upload or provide a resume with readable text."));
        }

        ResumeImprovementResponse response = resumeImprovementService.generateSuggestions(
                request.getResumeText(), request.getJobDescription());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/resume/compare")
    public ResponseEntity<?> compareResumes(
            @RequestBody ResumeCompareRequest request) {

        if (request == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Comparison request body cannot be null."));
        }
        if (request.getOldResumeText() == null || request.getOldResumeText().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Original resume text cannot be empty. Please upload or provide the previous resume."));
        }
        if (request.getNewResumeText() == null || request.getNewResumeText().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Updated resume text cannot be empty. Please upload or provide the updated resume."));
        }

        ResumeCompareResponse response = resumeCompareService.compareResumes(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/resume/recommend-jobs")
    public ResponseEntity<?> recommendJobs(
            @RequestBody JobRecommendationRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        if (request == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Request body cannot be null."));
        }

        String textToAnalyze = request.getResumeText();

        // If resumeId is provided and user is authenticated, retrieve user's resume
        if (request.getResumeId() != null) {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                        "message", "Authentication required to access saved resumes by ID."));
            }
            Resume userResume = resumeRepository.findByIdAndUserId(request.getResumeId(), principal.getId()).orElse(null);
            if (userResume == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "message", "Resume not found or access denied."));
            }
            if (textToAnalyze == null || textToAnalyze.trim().isEmpty()) {
                textToAnalyze = userResume.getResumeName() + "\nSkills: " + userResume.getSkills();
            }
        }

        if (textToAnalyze == null || textToAnalyze.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Resume text cannot be empty. Please upload or provide a resume."));
        }

        JobRecommendationResponse response = jobRecommendationService.recommendJobs(textToAnalyze);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/resume/skill-gap")
    public ResponseEntity<?> analyzeSkillGap(
            @RequestBody SkillGapRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        if (request == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Request body cannot be null."));
        }

        String textToAnalyze = request.getResumeText();

        // If resumeId is provided and user is authenticated, retrieve user's resume with data isolation
        if (request.getResumeId() != null) {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                        "message", "Authentication required to access saved resumes by ID."));
            }
            Resume userResume = resumeRepository.findByIdAndUserId(request.getResumeId(), principal.getId()).orElse(null);
            if (userResume == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "message", "Resume not found or access denied."));
            }
            if (textToAnalyze == null || textToAnalyze.trim().isEmpty()) {
                textToAnalyze = userResume.getResumeName() + "\nSkills: " + userResume.getSkills();
            }
        }

        if (textToAnalyze == null || textToAnalyze.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Resume text cannot be empty. Please upload or provide a resume."));
        }

        SkillGapResponse response = skillGapService.analyzeSkillGap(textToAnalyze, request.getTargetRole());
        return ResponseEntity.ok(response);
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
        if (resume.getSkills() != null) {
            existing.setSkills(resume.getSkills());
        }
        if (resume.getResumeData() != null) {
            existing.setResumeData(resume.getResumeData());
        }
        if (resume.getAtsScore() != null) {
            existing.setAtsScore(resume.getAtsScore());
        }
        if (resume.getMatchPercentage() != null) {
            existing.setMatchPercentage(resume.getMatchPercentage());
        }

        // Remove any redundant duplicates for this user only
        if (userMatches.size() > 1) {
            resumeRepository.deleteAll(userMatches.subList(1, userMatches.size()));
        }

        Resume updated = resumeRepository.save(existing);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/api/resume/builder/save")
    public ResponseEntity<?> saveBuilderResume(
            @RequestBody Resume resume,
            @AuthenticationPrincipal UserPrincipal principal) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Authentication required to save resume drafts."));
        }

        User user = userRepository.findById(principal.getId()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Authenticated user not found."));
        }

        String name = resume.getResumeName();
        if (name == null || name.trim().isEmpty()) {
            name = "Untitled Resume";
        }
        name = name.trim();

        Resume target;
        if (resume.getId() != null) {
            target = resumeRepository.findByIdAndUserId(resume.getId(), user.getId()).orElse(null);
            if (target == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Resume not found or access denied."));
            }
        } else {
            target = new Resume();
            target.setUser(user);
        }

        target.setResumeName(name);
        target.setSkills(resume.getSkills());
        target.setResumeData(resume.getResumeData());
        if (resume.getAtsScore() != null) {
            target.setAtsScore(resume.getAtsScore());
        }
        if (resume.getMatchPercentage() != null) {
            target.setMatchPercentage(resume.getMatchPercentage());
        }

        Resume saved = resumeRepository.save(target);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/api/resume/builder/{id}")
    public ResponseEntity<?> getBuilderResume(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Authentication required to load resume draft."));
        }

        Resume resume = resumeRepository.findByIdAndUserId(id, principal.getId()).orElse(null);
        if (resume == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Resume draft not found or access denied."));
        }

        return ResponseEntity.ok(resume);
    }

    @GetMapping("/api/resume/history")
    public ResponseEntity<?> getResumeHistory(@AuthenticationPrincipal UserPrincipal principal) {

        if (principal == null) {
            return ResponseEntity.ok(java.util.Collections.emptyList());
        }

        // Return ONLY the authenticated user's resumes, newest first
        List<Resume> userResumes = resumeRepository.findByUserIdOrderByIdDesc(principal.getId());
        return ResponseEntity.ok(userResumes);
    }

    @GetMapping("/api/resume/analytics")
    public ResponseEntity<?> getDashboardAnalytics(@AuthenticationPrincipal UserPrincipal principal) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "User must be authenticated to view dashboard analytics."));
        }

        List<Resume> resumes = resumeRepository.findByUserIdOrderByIdDesc(principal.getId());

        int totalResumes = resumes.size();
        Integer latestAtsScore = null;
        double totalAts = 0;
        int atsCount = 0;
        Integer bestMatchPercentage = null;

        List<Map<String, Object>> scoreTrends = new java.util.ArrayList<>();
        List<Map<String, Object>> recentActivity = new java.util.ArrayList<>();
        java.util.Set<String> uniqueSkills = new java.util.HashSet<>();

        for (Resume r : resumes) {
            if (r.getAtsScore() != null) {
                if (latestAtsScore == null) {
                    latestAtsScore = r.getAtsScore();
                }
                totalAts += r.getAtsScore();
                atsCount++;
            }
            if (r.getMatchPercentage() != null) {
                if (bestMatchPercentage == null || r.getMatchPercentage() > bestMatchPercentage) {
                    bestMatchPercentage = r.getMatchPercentage();
                }
            }

            if (r.getSkills() != null && !r.getSkills().isBlank()) {
                String[] parts = r.getSkills().split(",");
                for (String p : parts) {
                    String trimmed = p.trim();
                    if (!trimmed.isEmpty()) {
                        uniqueSkills.add(trimmed);
                    }
                }
            }

            if (recentActivity.size() < 10) {
                Map<String, Object> act = new java.util.LinkedHashMap<>();
                act.put("id", r.getId());
                act.put("resumeName", r.getResumeName());
                act.put("skills", r.getSkills());
                act.put("atsScore", r.getAtsScore());
                act.put("matchPercentage", r.getMatchPercentage());
                act.put("createdAt", r.getCreatedAt());
                recentActivity.add(act);
            }
        }

        int trendLimit = Math.min(10, resumes.size());
        List<Resume> sub = resumes.subList(0, trendLimit);
        for (int i = sub.size() - 1; i >= 0; i--) {
            Resume r = sub.get(i);
            Map<String, Object> trendItem = new java.util.LinkedHashMap<>();
            trendItem.put("id", r.getId());
            trendItem.put("resumeName", r.getResumeName());
            trendItem.put("atsScore", r.getAtsScore());
            trendItem.put("matchPercentage", r.getMatchPercentage());
            trendItem.put("createdAt", r.getCreatedAt());
            scoreTrends.add(trendItem);
        }

        Integer averageAtsScore = atsCount > 0 ? (int) Math.round(totalAts / atsCount) : null;

        Map<String, Object> analytics = new java.util.LinkedHashMap<>();
        analytics.put("totalResumesAnalyzed", totalResumes);
        analytics.put("latestAtsScore", latestAtsScore);
        analytics.put("averageAtsScore", averageAtsScore);
        analytics.put("bestMatchPercentage", bestMatchPercentage != null ? bestMatchPercentage : 0);
        analytics.put("totalUniqueSkills", uniqueSkills.size());
        analytics.put("scoreTrends", scoreTrends);
        analytics.put("recentActivity", recentActivity);

        return ResponseEntity.ok(analytics);
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
