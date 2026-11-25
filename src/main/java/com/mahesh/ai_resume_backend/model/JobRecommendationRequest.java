package com.mahesh.ai_resume_backend.model;

public class JobRecommendationRequest {

    private String resumeText;
    private Long resumeId;

    public JobRecommendationRequest() {
    }

    public JobRecommendationRequest(String resumeText) {
        this.resumeText = resumeText;
    }

    public JobRecommendationRequest(String resumeText, Long resumeId) {
        this.resumeText = resumeText;
        this.resumeId = resumeId;
    }

    public String getResumeText() {
        return resumeText;
    }

    public void setResumeText(String resumeText) {
        this.resumeText = resumeText;
    }

    public Long getResumeId() {
        return resumeId;
    }

    public void setResumeId(Long resumeId) {
        this.resumeId = resumeId;
    }
}
