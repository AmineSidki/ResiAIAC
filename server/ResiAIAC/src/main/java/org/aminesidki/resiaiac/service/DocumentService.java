package org.aminesidki.resiaiac.service;

import java.util.UUID;
import org.aminesidki.resiaiac.dto.DocumentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;

public interface DocumentService {
  Page<DocumentDto> getAllMy(Jwt jwt, Pageable pageable);

  DocumentDto save(DocumentDto dto);

  DocumentDto getById(UUID id);

  DocumentDto update(UUID id, DocumentDto dto);

  void delete(UUID id);
}
