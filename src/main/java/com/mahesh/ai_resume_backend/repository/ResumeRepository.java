
package com.mahesh.ai_resume_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mahesh.ai_resume_backend.model.Resume;

public interface ResumeRepository extends JpaRepository<Resume, Long> {

    List<Resume> findByResumeNameIgnoreCaseOrderByIdAsc(String resumeName);

    List<Resume> findAllByOrderByIdAsc();
}
