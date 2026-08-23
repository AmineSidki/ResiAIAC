package org.aminesidki.resiaiac.service;

import java.io.IOException;
import java.util.UUID;
import org.aminesidki.resiaiac.dto.DocumentDto;
import org.aminesidki.resiaiac.enumeration.FileType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentService {
  String getMyFileUrlById(Jwt jwt, UUID id);

  Page<DocumentDto> getAllMy(Jwt jwt, Pageable pageable);

  DocumentDto uploadMyDocument(Jwt jwt, FileType fileType, MultipartFile file) throws IOException;

  DocumentDto getById(UUID id);

  String getFileUrlById(UUID id);

  DocumentDto update(UUID id, DocumentDto dto);

  void delete(UUID id);
}
