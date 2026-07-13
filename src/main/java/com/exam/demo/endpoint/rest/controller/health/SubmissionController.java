package com.exam.demo.endpoint.rest.controller.health;

import com.exam.demo.submission.entity.Submission;
import com.exam.demo.submission.service.SubmissionService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
@RequestMapping("/submissions")
public class SubmissionController {
  private final SubmissionService service;

  @PostMapping
  public Submission submit(@RequestParam String email, @RequestParam MultipartFile file) {
    return service.submit(email, file);
  }

  @GetMapping
  public List<Submission> getAll() {
    return service.getAll();
  }
}
