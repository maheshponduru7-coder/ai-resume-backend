package com.mahesh.ai_resume_backend.model;

public class ResumeCompareRequest {

    private String oldResumeText;
    private String newResumeText;
    private String oldResumeName;
    private String newResumeName;
    private String jobDescription;

    public ResumeCompareRequest() {
    }

    public ResumeCompareRequest(String oldResumeText, String newResumeText, String jobDescription) {
        this.oldResumeText = oldResumeText;
        this.newResumeText = newResumeText;
        this.jobDescription = jobDescription;
    }

    public String getOldResumeText() {
        return oldResumeText;
    }

    public void setOldResumeText(String oldResumeText) {
        this.oldResumeText = oldResumeText;
    }

    public String getNewResumeText() {
        return newResumeText;
    }

    public void setNewResumeText(String newResumeText) {
        this.newResumeText = newResumeText;
    }

    public String getOldResumeName() {
        return oldResumeName;
    }

    public void setOldResumeName(String oldResumeName) {
        this.oldResumeName = oldResumeName;
    }

    public String getNewResumeName() {
        return newResumeName;
    }

    public void setNewResumeName(String newResumeName) {
        this.newResumeName = newResumeName;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }
}
