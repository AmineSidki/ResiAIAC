package org.aminesidki.resiaiac.service;

import java.util.UUID;
import org.aminesidki.resiaiac.dto.UtilisateurDto;
import org.aminesidki.resiaiac.dto.request.UpdateMeRequest;
import org.aminesidki.resiaiac.entity.Utilisateur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;

public interface UtilisateurService {
  Utilisateur getMyEntityById(UUID id);

  Utilisateur getMyEntityByJwt(Jwt jwt);

  UtilisateurDto getMyDtoByJwt(Jwt jwt);

  UtilisateurDto updateMe(Jwt jwt, UpdateMeRequest request);

  Page<UtilisateurDto> getAll(Pageable pageable);

  UtilisateurDto save(UtilisateurDto dto);

  UtilisateurDto saveAdmin(UtilisateurDto dto);

  UtilisateurDto getById(UUID id);

  UtilisateurDto update(UUID id, UtilisateurDto dto);

  void delete(UUID id);
}
