package org.aminesidki.resiaiac.service;

import org.aminesidki.resiaiac.dto.DocumentDto;
import org.aminesidki.resiaiac.dto.ServiceDto;

import java.util.UUID;

public interface DocumentService {
    DocumentDto save(ServiceDto dto);
    DocumentDto getById(UUID id);
    DocumentDto update(UUID id, DocumentDto dto);
    void delete(UUID id);
}
