package org.aminesidki.resiaiac.service;

import java.util.UUID;
import org.aminesidki.resiaiac.dto.UtilisateurDto;

public interface UtilisateurService {
  UtilisateurDto save(UtilisateurDto dto);

  UtilisateurDto getById(UUID id);

  UtilisateurDto update(UUID id, UtilisateurDto dto);

  void delete(UUID id);
}
