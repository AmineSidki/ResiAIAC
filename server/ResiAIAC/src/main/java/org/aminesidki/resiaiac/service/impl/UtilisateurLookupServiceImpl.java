package org.aminesidki.resiaiac.service.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.UtilisateurDto;
import org.aminesidki.resiaiac.entity.Utilisateur;
import org.aminesidki.resiaiac.exception.ResourceNotFoundException;
import org.aminesidki.resiaiac.mapper.UtilisateurMapper;
import org.aminesidki.resiaiac.repository.UtilisateurRepository;
import org.aminesidki.resiaiac.service.UtilisateurLookupService;
import org.aminesidki.resiaiac.util.ResourceFetcher;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UtilisateurLookupServiceImpl implements UtilisateurLookupService {

  private final UtilisateurRepository utilisateurRepository;
  private final UtilisateurMapper utilisateurMapper;

  @Override
  @Transactional(readOnly = true)
  @Cacheable(cacheNames = "utilisateur-keycloak-mapping", key = "#keycloakId")
  public UUID getUtilisateurIdByKeycloakId(UUID keycloakId) {
    return utilisateurRepository
        .findByKeycloakUser(keycloakId)
        .map(Utilisateur::getId)
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    "Resource not found: Utilisateur with keycloak id " + keycloakId));
  }

  @Override
  @CacheEvict(cacheNames = "utilisateur-keycloak-mapping", key = "#keycloakId")
  public void evictUtilisateurIdByKeycloakId(UUID keycloakId) {}

  @Override
  @Transactional(readOnly = true)
  @Cacheable(cacheNames = "utilisateur-dto", key = "#id")
  public UtilisateurDto getUtilisateurDtoById(UUID id) {
    Utilisateur entity = ResourceFetcher.fetchResource(id, utilisateurRepository, "Utilisateur");
    return utilisateurMapper.toDto(entity);
  }

  @Override
  @CacheEvict(cacheNames = "utilisateur-dto", key = "#id")
  public void evictUtilisateurDtoById(UUID id) {}
}
