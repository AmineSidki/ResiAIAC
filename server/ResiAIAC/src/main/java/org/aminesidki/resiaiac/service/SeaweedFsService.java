package org.aminesidki.resiaiac.service;

import org.springframework.web.multipart.MultipartFile;

public interface SeaweedFsService {
    void uploadFile(String bucketName, String fileName, MultipartFile file);
    String getFileUrl(String bucketName, String fileName, int expiry);
    void deleteFile(String bucketName, String fileName);
}
