package org.aminesidki.resiaiac.service.impl;

import java.io.IOException;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.exception.BucketNotFoundException;
import org.aminesidki.resiaiac.service.SeaweedFsService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Service
@RequiredArgsConstructor
public class SeaweedFsServiceImpl implements SeaweedFsService {
  private final S3Client client;
  private final S3Presigner presigner;

  @Override
  public void uploadFile(String bucketName, String fileName, MultipartFile file)
      throws IOException {
    if (!bucketExists(bucketName)) createBucket(bucketName);
    PutObjectRequest request =
        PutObjectRequest.builder()
            .bucket(bucketName)
            .key(fileName)
            .contentType(file.getContentType())
            .build();
    client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
  }

  @Override
  public String getFileUrl(String bucketName, String fileName, int expiry) {
    if (!bucketExists(bucketName))
      throw new BucketNotFoundException("No such bucket : " + bucketName);
    GetObjectRequest request = GetObjectRequest.builder().bucket(bucketName).key(fileName).build();

    GetObjectPresignRequest requestPresignRequest =
        GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofSeconds(expiry))
            .getObjectRequest(request)
            .build();

    PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(requestPresignRequest);
    return presignedRequest.url().toString();
  }

  @Override
  public void deleteFile(String bucketName, String fileName) {
    if (!bucketExists(bucketName))
      throw new BucketNotFoundException("No such bucket : " + bucketName);
    client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(fileName).build());
  }

  @Override
  public void createBucket(String bucketName) {
    client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
  }

  @Override
  public void deleteBucket(String bucketName) {
    client.deleteBucket(DeleteBucketRequest.builder().bucket(bucketName).build());
  }

  @Override
  public boolean bucketExists(String bucketName) {
    try {
      client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
      return true;
    } catch (NoSuchBucketException e) {
      return false;
    }
  }
}
