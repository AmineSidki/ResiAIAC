package org.aminesidki.resiaiac.service;

import java.util.UUID;
import org.aminesidki.resiaiac.dto.DocumentDto;

public interface DocumentService {
  DocumentDto save(DocumentDto dto);

  DocumentDto getById(UUID id);

  DocumentDto update(UUID id, DocumentDto dto);

  void delete(UUID id);
}
