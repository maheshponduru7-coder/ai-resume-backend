package com.mahesh.ai_resume_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mahesh.ai_resume_backend.model.Resume;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {

    @Query("SELECT r FROM Resume r WHERE r.user.id = :userId ORDER BY r.id DESC")
    List<Resume> findByUserIdOrderByIdDesc(@Param("userId") Long userId);

    @Query("SELECT r FROM Resume r WHERE r.user.id = :userId AND LOWER(r.resumeName) = LOWER(:resumeName) ORDER BY r.id ASC")
    List<Resume> findByUserIdAndResumeNameIgnoreCaseOrderByIdAsc(@Param("userId") Long userId, @Param("resumeName") String resumeName);

    @Query("SELECT r FROM Resume r WHERE r.id = :id AND r.user.id = :userId")
    Optional<Resume> findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    List<Resume> findByResumeNameIgnoreCaseOrderByIdAsc(String resumeName);

    List<Resume> findAllByOrderByIdAsc();
}

