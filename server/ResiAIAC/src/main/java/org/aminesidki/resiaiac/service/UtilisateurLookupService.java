package org.aminesidki.resiaiac.service;

import java.util.UUID;
import org.aminesidki.resiaiac.dto.UtilisateurDto;

public interface UtilisateurLookupService {
  UUID getUtilisateurIdByKeycloakId(UUID keycloakId);

  void evictUtilisateurIdByKeycloakId(UUID keycloakId);

  UtilisateurDto getUtilisateurDtoById(UUID id);

  void evictUtilisateurDtoById(UUID id);
}
