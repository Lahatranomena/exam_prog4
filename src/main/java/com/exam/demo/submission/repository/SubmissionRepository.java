package com.exam.demo.submission.repository;

import com.exam.demo.submission.entity.Submission;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionRepository extends JpaRepository<Submission, UUID> {}
