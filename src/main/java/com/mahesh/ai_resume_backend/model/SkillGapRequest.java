package com.mahesh.ai_resume_backend.model;

public class SkillGapRequest {

    private String resumeText;
    private String targetRole;
    private Long resumeId;

    public SkillGapRequest() {
    }

    public SkillGapRequest(String resumeText, String targetRole) {
        this.resumeText = resumeText;
        this.targetRole = targetRole;
    }

    public SkillGapRequest(String resumeText, String targetRole, Long resumeId) {
        this.resumeText = resumeText;
        this.targetRole = targetRole;
        this.resumeId = resumeId;
    }

    public String getResumeText() {
        return resumeText;
    }

    public void setResumeText(String resumeText) {
        this.resumeText = resumeText;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public Long getResumeId() {
        return resumeId;
    }

    public void setResumeId(Long resumeId) {
        this.resumeId = resumeId;
    }
}
