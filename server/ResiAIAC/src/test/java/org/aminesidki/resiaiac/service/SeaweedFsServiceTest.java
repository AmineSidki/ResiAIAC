package org.aminesidki.resiaiac.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import org.aminesidki.resiaiac.exception.BucketNotFoundException;
import org.aminesidki.resiaiac.service.impl.SeaweedFsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

/**
 * Unit tests for {@link SeaweedFsService}, exercised through its {@link SeaweedFsServiceImpl}
 * implementation.
 *
 * <p>The AWS SDK v2 {@code S3Client}/{@code S3Presigner} clients are mocked entirely — no real
 * S3-compatible backend involved, no Testcontainers. {@code bucketExists} is driven by whether
 * {@code headBucket} throws {@link NoSuchBucketException}, which is how the implementation
 * distinguishes "missing" from any other outcome.
 */
@ExtendWith(MockitoExtension.class)
class SeaweedFsServiceTest {

  @Mock private S3Client client;

  @Mock private S3Presigner presigner;

  private SeaweedFsService seaweedFsService;

  private static final String BUCKET = "cin";
  private static final String FILE_NAME = "cin.pdf";

  @BeforeEach
  void setUp() {
    seaweedFsService = new SeaweedFsServiceImpl(client, presigner);
  }

  // ---------- bucketExists ----------

  @Test
  void bucketExists_shouldReturnTrue_whenHeadBucketSucceeds() {
    when(client.headBucket(any(HeadBucketRequest.class))).thenReturn(null);

    boolean result = seaweedFsService.bucketExists(BUCKET);

    assertThat(result).isTrue();
    verify(client).headBucket(HeadBucketRequest.builder().bucket(BUCKET).build());
  }

  @Test
  void bucketExists_shouldReturnFalse_whenNoSuchBucketExceptionThrown() {
    when(client.headBucket(any(HeadBucketRequest.class)))
        .thenThrow(NoSuchBucketException.builder().message("no such bucket").build());

    boolean result = seaweedFsService.bucketExists(BUCKET);

    assertThat(result).isFalse();
  }

  // ---------- createBucket ----------

  @Test
  void createBucket_shouldDelegateToClient() {
    seaweedFsService.createBucket(BUCKET);

    verify(client).createBucket(CreateBucketRequest.builder().bucket(BUCKET).build());
  }

  // ---------- deleteBucket ----------

  @Test
  void deleteBucket_shouldDelegateToClient() {
    seaweedFsService.deleteBucket(BUCKET);

    verify(client).deleteBucket(DeleteBucketRequest.builder().bucket(BUCKET).build());
  }

  // ---------- uploadFile ----------

  @Test
  void uploadFile_shouldPutObjectDirectly_whenBucketAlreadyExists() throws Exception {
    MockMultipartFile file =
        new MockMultipartFile("file", FILE_NAME, "application/pdf", "content".getBytes());
    when(client.headBucket(any(HeadBucketRequest.class))).thenReturn(null);

    seaweedFsService.uploadFile(BUCKET, FILE_NAME, file);

    verify(client, never()).createBucket(any(CreateBucketRequest.class));
    verify(client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
  }

  @Test
  void uploadFile_shouldCreateBucketFirst_whenBucketDoesNotExist() throws Exception {
    MockMultipartFile file =
        new MockMultipartFile("file", FILE_NAME, "application/pdf", "content".getBytes());
    when(client.headBucket(any(HeadBucketRequest.class)))
        .thenThrow(NoSuchBucketException.builder().message("no such bucket").build());

    seaweedFsService.uploadFile(BUCKET, FILE_NAME, file);

    verify(client).createBucket(CreateBucketRequest.builder().bucket(BUCKET).build());
    verify(client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
  }

  // ---------- getFileUrl ----------

  @Test
  void getFileUrl_shouldReturnPresignedUrl_whenBucketExists() throws Exception {
    when(client.headBucket(any(HeadBucketRequest.class))).thenReturn(null);
    PresignedGetObjectRequest presignedRequest = mock(PresignedGetObjectRequest.class);
    when(presignedRequest.url())
        .thenReturn(new URI("https://seaweed.local/cin/cin.pdf?signed=1").toURL());
    when(presigner.presignGetObject(any(GetObjectPresignRequest.class)))
        .thenReturn(presignedRequest);

    String result = seaweedFsService.getFileUrl(BUCKET, FILE_NAME, 900);

    assertThat(result).isEqualTo("https://seaweed.local/cin/cin.pdf?signed=1");
  }

  @Test
  void getFileUrl_shouldThrowBucketNotFoundException_whenBucketMissing() {
    when(client.headBucket(any(HeadBucketRequest.class)))
        .thenThrow(NoSuchBucketException.builder().message("no such bucket").build());

    assertThatThrownBy(() -> seaweedFsService.getFileUrl(BUCKET, FILE_NAME, 900))
        .isInstanceOf(BucketNotFoundException.class);

    verify(presigner, never()).presignGetObject(any(GetObjectPresignRequest.class));
  }

  // ---------- deleteFile ----------

  @Test
  void deleteFile_shouldDeleteObject_whenBucketExists() {
    when(client.headBucket(any(HeadBucketRequest.class))).thenReturn(null);

    seaweedFsService.deleteFile(BUCKET, FILE_NAME);

    verify(client)
        .deleteObject(DeleteObjectRequest.builder().bucket(BUCKET).key(FILE_NAME).build());
  }

  @Test
  void deleteFile_shouldThrowBucketNotFoundException_whenBucketMissing() {
    when(client.headBucket(any(HeadBucketRequest.class)))
        .thenThrow(NoSuchBucketException.builder().message("no such bucket").build());

    assertThatThrownBy(() -> seaweedFsService.deleteFile(BUCKET, FILE_NAME))
        .isInstanceOf(BucketNotFoundException.class);

    verify(client, never()).deleteObject(any(DeleteObjectRequest.class));
  }
}
