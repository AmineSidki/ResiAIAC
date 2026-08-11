package org.aminesidki.resiaiac.service;

import java.util.UUID;
import org.aminesidki.resiaiac.dto.DocumentDto;
import org.aminesidki.resiaiac.dto.ServiceDto;

public interface DocumentService {
  DocumentDto save(ServiceDto dto);

  DocumentDto getById(UUID id);

  DocumentDto update(UUID id, DocumentDto dto);

  void delete(UUID id);
}
