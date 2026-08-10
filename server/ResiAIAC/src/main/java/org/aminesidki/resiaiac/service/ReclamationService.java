package org.aminesidki.resiaiac.service;

import org.aminesidki.resiaiac.dto.ReclamationDto;

import java.util.UUID;

public interface ReclamationService {
    ReclamationDto save(ReclamationDto dto);
    ReclamationDto getById(UUID id);
    ReclamationDto update(UUID id, ReclamationDto dto);
    void delete(UUID id);
}
