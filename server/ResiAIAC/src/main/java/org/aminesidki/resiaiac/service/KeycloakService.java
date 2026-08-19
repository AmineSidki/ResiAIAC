package org.aminesidki.resiaiac.service;

import java.util.UUID;
import org.aminesidki.resiaiac.dto.UtilisateurDto;

public interface KeycloakService {
  UUID createUser(UtilisateurDto dto);

  void deleteUser(UUID id);
}
