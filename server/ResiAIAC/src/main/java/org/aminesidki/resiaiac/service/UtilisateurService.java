package org.aminesidki.resiaiac.service;

import java.util.UUID;
import org.aminesidki.resiaiac.dto.UtilisateurDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UtilisateurService {
  Page<UtilisateurDto> getAll(Pageable pageable);

  UtilisateurDto save(UtilisateurDto dto);

  UtilisateurDto getById(UUID id);

  UtilisateurDto update(UUID id, UtilisateurDto dto);

  void delete(UUID id);
}
