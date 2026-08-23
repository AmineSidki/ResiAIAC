package org.aminesidki.resiaiac.service;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface SeaweedFsService {
  void uploadFile(String bucketName, String fileName, MultipartFile file) throws IOException;

  String getFileUrl(String bucketName, String fileName, int expiry);

  void deleteFile(String bucketName, String fileName);

  void createBucket(String bucketName);

  void deleteBucket(String bucketName);

  boolean bucketExists(String bucketName);
}
