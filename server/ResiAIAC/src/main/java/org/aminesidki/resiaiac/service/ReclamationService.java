package org.aminesidki.resiaiac.service;

import java.util.UUID;
import org.aminesidki.resiaiac.dto.ReclamationDto;

public interface ReclamationService {
  ReclamationDto save(ReclamationDto dto);

  ReclamationDto getById(UUID id);

  ReclamationDto update(UUID id, ReclamationDto dto);

  void delete(UUID id);
}
