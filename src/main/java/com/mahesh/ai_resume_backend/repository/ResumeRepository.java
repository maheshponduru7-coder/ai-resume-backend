package com.mahesh.ai_resume_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mahesh.ai_resume_backend.model.Resume;

public interface ResumeRepository extends JpaRepository<Resume, Long> {

}
