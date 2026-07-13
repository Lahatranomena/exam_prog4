package com.exam.demo.submission.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.exam.demo.endpoint.event.EventProducer;
import com.exam.demo.endpoint.event.model.ImageUploadRequested;
import com.exam.demo.file.bucket.BucketComponent;
import com.exam.demo.submission.entity.Submission;
import com.exam.demo.submission.repository.SubmissionRepository;
import java.io.File;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class SubmissionServiceTest {

  @Mock private SubmissionRepository repository;
  @Mock private BucketComponent bucketComponent;
  @Mock private EventProducer<ImageUploadRequested> eventProducer;

  private SubmissionService service;

  @BeforeEach
  void setUp() {
    service = new SubmissionService(repository, bucketComponent, eventProducer);
  }

  @Test
  void submit_shouldUploadFilePersistSubmissionAndProduceEvent() throws Exception {
    var file =
        new MockMultipartFile("file", "image.png", "image/png", "fake-image-content".getBytes());
    var email = "test@example.com";

    when(repository.saveAndFlush(any(Submission.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    var result = service.submit(email, file);

    assertThat(result.getFileName()).isEqualTo("image.png");
    assertThat(result.getEmail()).isEqualTo(email);
    assertThat(result.getId()).isNotNull();

    verify(bucketComponent).upload(any(File.class), any(String.class));
    verify(repository).saveAndFlush(any(Submission.class));
    verify(eventProducer).accept(anyList());
  }

  @Test
  void getAll_shouldReturnAllSubmissions() {
    var submission = Submission.builder().fileName("image.png").email("test@example.com").build();
    when(repository.findAll()).thenReturn(List.of(submission));

    var result = service.getAll();

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getFileName()).isEqualTo("image.png");
  }
}
