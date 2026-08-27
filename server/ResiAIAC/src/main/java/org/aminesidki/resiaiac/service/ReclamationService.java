package org.aminesidki.resiaiac.service;

import java.util.UUID;
import org.aminesidki.resiaiac.dto.ReclamationDto;
import org.aminesidki.resiaiac.dto.request.MyReclamationRequest;
import org.aminesidki.resiaiac.entity.Reclamation;
import org.aminesidki.resiaiac.enumeration.EtatReclamation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;

public interface ReclamationService {
  Page<ReclamationDto> getAllMyByStatus(Jwt jwt, EtatReclamation etat, Pageable pageable);

  Page<ReclamationDto> getAllMy(Jwt jwt, Pageable pageable);

  ReclamationDto saveMy(Jwt jwt, MyReclamationRequest dto);

  ReclamationDto getMyById(Jwt jwt, UUID id);

  Page<ReclamationDto> getAllByStatus(EtatReclamation etat, Pageable pageable);

  Page<ReclamationDto> getAll(Pageable pageable);

  Reclamation getEntityById(UUID id);

  ReclamationDto save(ReclamationDto dto);

  ReclamationDto getById(UUID id);

  ReclamationDto update(UUID id, ReclamationDto dto);

  void delete(UUID id);
}
