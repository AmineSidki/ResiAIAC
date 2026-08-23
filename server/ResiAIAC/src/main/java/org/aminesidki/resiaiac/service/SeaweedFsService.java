package org.aminesidki.resiaiac.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface SeaweedFsService {
  void uploadFile(String bucketName, String fileName, MultipartFile file) throws IOException;

  String getFileUrl(String bucketName, String fileName, int expiry);

  void deleteFile(String bucketName, String fileName);

  void createBucket(String bucketName);

  boolean bucketExists(String bucketName);
}
