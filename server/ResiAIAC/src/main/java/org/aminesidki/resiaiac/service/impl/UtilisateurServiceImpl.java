package org.aminesidki.resiaiac.service.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aminesidki.resiaiac.dto.UtilisateurDto;
import org.aminesidki.resiaiac.dto.request.UpdateMeRequest;
import org.aminesidki.resiaiac.entity.Utilisateur;
import org.aminesidki.resiaiac.exception.ResourceNotFoundException;
import org.aminesidki.resiaiac.mapper.UtilisateurMapper;
import org.aminesidki.resiaiac.repository.UtilisateurRepository;
import org.aminesidki.resiaiac.service.KeycloakService;
import org.aminesidki.resiaiac.service.UtilisateurService;
import org.aminesidki.resiaiac.util.ResourceFetcher;
import org.aminesidki.resiaiac.util.StringUtil;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
@CacheConfig(cacheNames = "utilisateurs")
public class UtilisateurServiceImpl implements UtilisateurService {

  private final KeycloakService keycloakService;
  private final UtilisateurRepository utilisateurRepository;
  private final UtilisateurMapper utilisateurMapper;

  private UUID extractIdFromJwt(Jwt jwt) {
    String idString = jwt.getClaimAsString("sub");
    if (idString == null) throw new ResourceNotFoundException("Keycloak id for user is null !");
    return UUID.fromString(idString);
  }

  @Transactional(readOnly = true)
  @Override
  public Utilisateur getMyEntityById(UUID id) {
    return ResourceFetcher.fetchResource(id, utilisateurRepository, "Utilisateur");
  }

  @Transactional(readOnly = true)
  @Override
  public Utilisateur getMyEntityByJwt(Jwt jwt) {
    UUID keycloakId = extractIdFromJwt(jwt);
    return utilisateurRepository
        .findByKeycloakUser(keycloakId)
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    "Resource not found: Utilisateur with keycloak id " + keycloakId));
  }

  @Transactional(readOnly = true)
  @Override
  public UtilisateurDto getMyDtoByJwt(Jwt jwt) {
    return utilisateurMapper.toDto(getMyEntityByJwt(jwt));
  }

  @Override
  public UtilisateurDto updateMe(Jwt jwt, UpdateMeRequest request) {
    UtilisateurDto filteredDto = utilisateurMapper.updateMeRequestToDto(request);
    Utilisateur entity = getMyEntityByJwt(jwt);
    utilisateurMapper.updateEntityFromDto(filteredDto, entity);
    entity = utilisateurRepository.save(entity);
    return utilisateurMapper.toDto(entity);
  }

  @Override
  public Page<UtilisateurDto> getAll(Pageable pageable) {
    return utilisateurRepository.findAll(pageable).map(utilisateurMapper::toDto);
  }

  @Override
  public UtilisateurDto save(UtilisateurDto dto) {
    UUID keycloakId = null;
    try {
      keycloakId = keycloakService.createUser(dto);
      Utilisateur entity = utilisateurMapper.toEntity(dto);
      entity.setKeycloakUser(keycloakId);
      entity = utilisateurRepository.save(entity);
      return utilisateurMapper.toDto(entity);
    } catch (Exception e) {
      log.error(
          "An error occurred whilst saving user with username {} !",
          StringUtil.nameToUsername(dto.nom(), dto.prenom()));
      if (keycloakId != null) {
        keycloakService.deleteUser(keycloakId);
      }
      throw e;
    }
  }

  @Transactional(readOnly = true)
  @Override
  @Cacheable(key = "#id")
  public UtilisateurDto getById(UUID id) {
    Utilisateur entity = ResourceFetcher.fetchResource(id, utilisateurRepository, "Utilisateur");
    return utilisateurMapper.toDto(entity);
  }

  @Override
  @CacheEvict(key = "#id")
  public UtilisateurDto update(UUID id, UtilisateurDto dto) {
    Utilisateur entity = ResourceFetcher.fetchResource(id, utilisateurRepository, "Utilisateur");
    utilisateurMapper.updateEntityFromDto(dto, entity);
    entity = utilisateurRepository.save(entity);
    return utilisateurMapper.toDto(entity);
  }

  @Override
  @CacheEvict(key = "#id")
  public void delete(UUID id) {
    Utilisateur utilisateur =
        ResourceFetcher.fetchResource(id, utilisateurRepository, "Utilisateur");
    try {
      utilisateurRepository.delete(utilisateur);
      keycloakService.deleteUser(utilisateur.getKeycloakUser());
    } catch (Exception e) {
      log.error("An error occurred whilst deleting user with id {} !", id);
      throw e;
    }
  }
}
