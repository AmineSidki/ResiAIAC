package org.aminesidki.resiaiac.service;

import java.util.UUID;
import org.aminesidki.resiaiac.dto.ReclamationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReclamationService {
  Page<ReclamationDto> getAll(Pageable pageable);

  ReclamationDto save(ReclamationDto dto);

  ReclamationDto getById(UUID id);

  ReclamationDto update(UUID id, ReclamationDto dto);

  void delete(UUID id);
}
