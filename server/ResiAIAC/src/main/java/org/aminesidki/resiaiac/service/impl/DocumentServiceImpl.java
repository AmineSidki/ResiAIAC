package org.aminesidki.resiaiac.service.impl;

import java.util.UUID;
import org.aminesidki.resiaiac.dto.DocumentDto;
import org.aminesidki.resiaiac.dto.ServiceDto;
import org.aminesidki.resiaiac.service.DocumentService;
import org.springframework.stereotype.Service;

@Service
public class DocumentServiceImpl implements DocumentService {
  @Override
  public DocumentDto save(ServiceDto dto) {
    return null;
  }

  @Override
  public DocumentDto getById(UUID id) {
    return null;
  }

  @Override
  public DocumentDto update(UUID id, DocumentDto dto) {
    return null;
  }

  @Override
  public void delete(UUID id) {}
}
