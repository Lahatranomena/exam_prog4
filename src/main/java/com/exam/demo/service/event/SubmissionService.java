package com.exam.demo.service.event;

import com.exam.demo.file.bucket.BucketComponent;
import com.exam.demo.submission.entity.Submission;
import com.exam.demo.submission.repository.SubmissionRepository;
import java.io.File;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class SubmissionService {
  private final SubmissionRepository repository;
  private final BucketComponent bucketComponent;

  @SneakyThrows
  public Submission submit(String email, MultipartFile file) {
    var id = UUID.randomUUID();
    var bucketKey = id + "-" + file.getOriginalFilename();

    File tempFile = File.createTempFile("upload-", file.getOriginalFilename());
    file.transferTo(tempFile);
    bucketComponent.upload(tempFile, bucketKey);

    var submission =
        Submission.builder()
            .id(id)
            .fileName(file.getOriginalFilename())
            .email(email)
            .createdAt(Instant.now())
            .build();

    return repository.saveAndFlush(submission);
  }

  public List<Submission> getAll() {
    return repository.findAll();
  }
}
