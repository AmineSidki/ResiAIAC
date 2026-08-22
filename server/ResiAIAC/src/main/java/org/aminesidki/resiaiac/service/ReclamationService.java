package org.aminesidki.resiaiac.service;

import java.util.UUID;
import org.aminesidki.resiaiac.dto.ReclamationDto;
import org.aminesidki.resiaiac.dto.request.MyReclamationRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;

public interface ReclamationService {
  Page<ReclamationDto> getAllMy(Jwt jwt, Pageable pageable);

  ReclamationDto saveMy(Jwt jwt, MyReclamationRequest dto);

  ReclamationDto getMyById(Jwt jwt, UUID id);

  Page<ReclamationDto> getAll(Pageable pageable);

  ReclamationDto save(ReclamationDto dto);

  ReclamationDto getById(UUID id);

  ReclamationDto update(UUID id, ReclamationDto dto);

  void delete(UUID id);
}
