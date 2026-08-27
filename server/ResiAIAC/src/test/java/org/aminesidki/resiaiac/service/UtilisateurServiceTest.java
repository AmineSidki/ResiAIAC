package org.aminesidki.resiaiac.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.aminesidki.resiaiac.dto.UtilisateurDto;
import org.aminesidki.resiaiac.dto.request.UpdateMeRequest;
import org.aminesidki.resiaiac.entity.Utilisateur;
import org.aminesidki.resiaiac.exception.ResourceNotFoundException;
import org.aminesidki.resiaiac.mapper.UtilisateurMapper;
import org.aminesidki.resiaiac.repository.UtilisateurRepository;
import org.aminesidki.resiaiac.service.impl.UtilisateurServiceImpl;
import org.aminesidki.resiaiac.util.ResourceFetcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Unit tests for {@link UtilisateurService}, exercised through its {@link UtilisateurServiceImpl}
 * implementation.
 *
 * <p>{@code save} and {@code delete} are the interesting cases here: {@code save} must create the
 * Keycloak user before persisting, and roll it back if the DB write fails; {@code delete} must
 * delete the DB row *before* the Keycloak user, so a failed Keycloak delete leaves the transaction
 * rollback restoring full consistency rather than an orphaned DB row.
 *
 * <p>ResourceFetcher.fetchResource is a static method, mocked per-test with Mockito's mockStatic
 * (requires Mockito 5+ / mockito-inline).
 */
@ExtendWith(MockitoExtension.class)
class UtilisateurServiceTest {

  @Mock private KeycloakService keycloakService;

  @Mock private UtilisateurRepository utilisateurRepository;

  @Mock private UtilisateurMapper utilisateurMapper;

  private UtilisateurService utilisateurService;

  private UUID id;
  private UUID keycloakId;
  private Utilisateur entity;
  private UtilisateurDto dto;

  @BeforeEach
  void setUp() {
    utilisateurService =
        new UtilisateurServiceImpl(keycloakService, utilisateurRepository, utilisateurMapper);

    id = UUID.randomUUID();
    keycloakId = UUID.randomUUID();
    entity =
        Utilisateur.builder().id(id).keycloakUser(keycloakId).nom("Sidki").prenom("Amine").build();
    dto =
        new UtilisateurDto(
            id,
            "amine.sidki@example.com",
            "Sidki",
            "Amine",
            null,
            null,
            null,
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            null,
            null);
  }

  // ---------- save ----------

  @Test
  void save_shouldCreateKeycloakUserThenPersistAndReturnDto() {
    UtilisateurDto inputDto =
        new UtilisateurDto(
            null,
            "amine.sidki@example.com",
            "Sidki",
            "Amine",
            null,
            null,
            null,
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            null,
            null);
    Utilisateur mappedEntity = Utilisateur.builder().nom("Sidki").prenom("Amine").build();
    Utilisateur savedEntity =
        Utilisateur.builder().id(id).keycloakUser(keycloakId).nom("Sidki").prenom("Amine").build();
    UtilisateurDto resultDto =
        new UtilisateurDto(
            id,
            "amine.sidki@example.com",
            "Sidki",
            "Amine",
            null,
            null,
            null,
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            null,
            null);

    when(keycloakService.createUser(inputDto)).thenReturn(keycloakId);
    when(utilisateurMapper.toEntity(inputDto)).thenReturn(mappedEntity);
    when(utilisateurRepository.save(mappedEntity)).thenReturn(savedEntity);
    when(utilisateurMapper.toDto(savedEntity)).thenReturn(resultDto);

    UtilisateurDto result = utilisateurService.save(inputDto);

    assertThat(result).isEqualTo(resultDto);
    assertThat(mappedEntity.getKeycloakUser()).isEqualTo(keycloakId);
    verify(keycloakService).createUser(inputDto);
    verify(utilisateurMapper).toEntity(inputDto);
    verify(utilisateurRepository).save(mappedEntity);
    verify(utilisateurMapper).toDto(savedEntity);
    verifyNoMoreInteractions(keycloakService, utilisateurRepository, utilisateurMapper);
  }

  @Test
  void save_shouldNotTouchDbOrRollbackWhenKeycloakCreationFails() {
    UtilisateurDto inputDto =
        new UtilisateurDto(
            null,
            "amine.sidki@example.com",
            "Sidki",
            "Amine",
            null,
            null,
            null,
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            null,
            null);
    RuntimeException keycloakFailure = new RuntimeException("Keycloak unavailable");

    when(keycloakService.createUser(inputDto)).thenThrow(keycloakFailure);

    assertThatThrownBy(() -> utilisateurService.save(inputDto)).isSameAs(keycloakFailure);

    verify(keycloakService).createUser(inputDto);
    verify(keycloakService, never()).deleteUser(any());
    verifyNoMoreInteractions(keycloakService);
    verifyNoInteractions(utilisateurRepository, utilisateurMapper);
  }

  @Test
  void save_shouldRollbackKeycloakUserWhenDbSaveFails() {
    UtilisateurDto inputDto =
        new UtilisateurDto(
            null,
            "amine.sidki@example.com",
            "Sidki",
            "Amine",
            null,
            null,
            null,
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            null,
            null);
    Utilisateur mappedEntity = Utilisateur.builder().nom("Sidki").prenom("Amine").build();
    RuntimeException dbFailure = new RuntimeException("DB unavailable");

    when(keycloakService.createUser(inputDto)).thenReturn(keycloakId);
    when(utilisateurMapper.toEntity(inputDto)).thenReturn(mappedEntity);
    when(utilisateurRepository.save(mappedEntity)).thenThrow(dbFailure);

    assertThatThrownBy(() -> utilisateurService.save(inputDto)).isSameAs(dbFailure);

    verify(keycloakService).createUser(inputDto);
    verify(keycloakService).deleteUser(keycloakId);
    verifyNoMoreInteractions(keycloakService);
  }

  // ---------- getById ----------

  @Test
  void getById_shouldFetchAndReturnDto() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, utilisateurRepository, "Utilisateur"))
          .thenReturn(entity);
      when(utilisateurMapper.toDto(entity)).thenReturn(dto);

      UtilisateurDto result = utilisateurService.getById(id);

      assertThat(result).isEqualTo(dto);
      fetcher.verify(() -> ResourceFetcher.fetchResource(id, utilisateurRepository, "Utilisateur"));
      verify(utilisateurMapper).toDto(entity);
      verifyNoMoreInteractions(utilisateurMapper);
      verifyNoInteractions(keycloakService);
    }
  }

  @Test
  void getById_shouldPropagateExceptionWhenNotFound() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      RuntimeException notFound = new RuntimeException("Utilisateur not found");
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, utilisateurRepository, "Utilisateur"))
          .thenThrow(notFound);

      assertThatThrownBy(() -> utilisateurService.getById(id)).isSameAs(notFound);

      verifyNoMoreInteractions(utilisateurMapper);
      verifyNoInteractions(keycloakService);
    }
  }

  // ---------- update ----------

  @Test
  void update_shouldFetchMutateSaveAndReturnDto() {
    Utilisateur savedEntity =
        Utilisateur.builder()
            .id(id)
            .keycloakUser(keycloakId)
            .nom("Sidki")
            .prenom("Amine-renamed")
            .build();
    UtilisateurDto resultDto =
        new UtilisateurDto(
            id,
            "amine.sidki@example.com",
            "Sidki",
            "Amine-renamed",
            null,
            null,
            null,
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            null,
            null);

    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, utilisateurRepository, "Utilisateur"))
          .thenReturn(entity);
      when(utilisateurRepository.save(entity)).thenReturn(savedEntity);
      when(utilisateurMapper.toDto(savedEntity)).thenReturn(resultDto);

      UtilisateurDto result = utilisateurService.update(id, dto);

      assertThat(result).isEqualTo(resultDto);
      fetcher.verify(() -> ResourceFetcher.fetchResource(id, utilisateurRepository, "Utilisateur"));
      verify(utilisateurMapper).updateEntityFromDto(dto, entity);
      verify(utilisateurRepository).save(entity);
      verify(utilisateurMapper).toDto(savedEntity);
      verifyNoMoreInteractions(utilisateurRepository, utilisateurMapper);
      verifyNoInteractions(keycloakService);
    }
  }

  @Test
  void update_shouldNotSaveWhenResourceNotFound() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      RuntimeException notFound = new RuntimeException("Utilisateur not found");
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, utilisateurRepository, "Utilisateur"))
          .thenThrow(notFound);

      assertThatThrownBy(() -> utilisateurService.update(id, dto)).isSameAs(notFound);

      verify(utilisateurRepository, never()).save(any());
      verifyNoMoreInteractions(utilisateurMapper);
      verifyNoInteractions(keycloakService);
    }
  }

  // ---------- delete ----------

  @Test
  void delete_shouldDeleteDbRowBeforeKeycloakUser() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, utilisateurRepository, "Utilisateur"))
          .thenReturn(entity);

      utilisateurService.delete(id);

      fetcher.verify(() -> ResourceFetcher.fetchResource(id, utilisateurRepository, "Utilisateur"));
      InOrder order = inOrder(utilisateurRepository, keycloakService);
      order.verify(utilisateurRepository).delete(entity);
      order.verify(keycloakService).deleteUser(keycloakId);
      verifyNoMoreInteractions(utilisateurRepository, keycloakService);
    }
  }

  @Test
  void delete_shouldNotDeleteWhenResourceNotFound() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      RuntimeException notFound = new RuntimeException("Utilisateur not found");
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, utilisateurRepository, "Utilisateur"))
          .thenThrow(notFound);

      assertThatThrownBy(() -> utilisateurService.delete(id)).isSameAs(notFound);

      verify(utilisateurRepository, never()).delete(any());
      verifyNoInteractions(keycloakService);
    }
  }

  @Test
  void delete_shouldNotCallKeycloakWhenDbDeleteFails() {
    RuntimeException dbFailure = new RuntimeException("DB unavailable");

    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, utilisateurRepository, "Utilisateur"))
          .thenReturn(entity);
      doThrow(dbFailure).when(utilisateurRepository).delete(entity);

      assertThatThrownBy(() -> utilisateurService.delete(id)).isSameAs(dbFailure);

      verify(utilisateurRepository).delete(entity);
      verifyNoInteractions(keycloakService);
    }
  }

  // ---------- getMyEntityByJwt / getMyDtoByJwt ----------

  @Test
  void getMyEntity_ByJwt_shouldResolveUserByKeycloakSubClaim() {
    Jwt jwt = jwtWithSub(keycloakId);
    when(utilisateurRepository.findByKeycloakUser(keycloakId)).thenReturn(Optional.of(entity));

    Utilisateur result = utilisateurService.getMyEntityByJwt(jwt);

    assertThat(result).isEqualTo(entity);
    verify(utilisateurRepository).findByKeycloakUser(keycloakId);
  }

  @Test
  void getMyEntity_ByJwt_shouldThrowWhenNoUserMatchesTheKeycloakId() {
    Jwt jwt = jwtWithSub(keycloakId);
    when(utilisateurRepository.findByKeycloakUser(keycloakId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> utilisateurService.getMyEntityByJwt(jwt))
        .isInstanceOf(ResourceNotFoundException.class);

    verifyNoMoreInteractions(utilisateurMapper);
  }

  @Test
  void getMyEntity_ByJwt_shouldThrowWhenJwtHasNoSubClaim() {
    Jwt jwt = mock(Jwt.class);
    when(jwt.getClaimAsString("sub")).thenReturn(null);

    assertThatThrownBy(() -> utilisateurService.getMyEntityByJwt(jwt))
        .isInstanceOf(ResourceNotFoundException.class);

    verifyNoInteractions(utilisateurRepository);
  }

  @Test
  void getMyDto_ByJwt_shouldMapResolvedEntity() {
    Jwt jwt = jwtWithSub(keycloakId);
    when(utilisateurRepository.findByKeycloakUser(keycloakId)).thenReturn(Optional.of(entity));
    when(utilisateurMapper.toDto(entity)).thenReturn(dto);

    UtilisateurDto result = utilisateurService.getMyDtoByJwt(jwt);

    assertThat(result).isEqualTo(dto);
  }

  // ---------- updateMe ----------

  @Test
  void updateMe_shouldFilterMutateSaveAndReturnDto() {
    Jwt jwt = jwtWithSub(keycloakId);
    UpdateMeRequest request = new UpdateMeRequest("12 Rue des Fleurs", "+212600000000");
    UtilisateurDto filteredDto =
        new UtilisateurDto(
            null,
            null,
            null,
            null,
            null,
            "12 Rue des Fleurs",
            "+212600000000",
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            null,
            null);
    Utilisateur savedEntity =
        Utilisateur.builder()
            .id(id)
            .keycloakUser(keycloakId)
            .nom("Sidki")
            .prenom("Amine")
            .adresse("12 Rue des Fleurs")
            .telephone("+212600000000")
            .build();

    when(utilisateurRepository.findByKeycloakUser(keycloakId)).thenReturn(Optional.of(entity));
    when(utilisateurMapper.updateMeRequestToDto(request)).thenReturn(filteredDto);
    when(utilisateurRepository.save(entity)).thenReturn(savedEntity);
    when(utilisateurMapper.toDto(savedEntity)).thenReturn(dto);

    UtilisateurDto result = utilisateurService.updateMe(jwt, request);

    assertThat(result).isEqualTo(dto);
    verify(utilisateurMapper).updateMeRequestToDto(request);
    verify(utilisateurMapper).updateEntityFromDto(filteredDto, entity);
    verify(utilisateurRepository).save(entity);
  }

  @Test
  void updateMe_shouldPropagateExceptionWhenUserNotFound() {
    Jwt jwt = jwtWithSub(keycloakId);
    UpdateMeRequest request = new UpdateMeRequest("12 Rue des Fleurs", null);

    when(utilisateurRepository.findByKeycloakUser(keycloakId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> utilisateurService.updateMe(jwt, request))
        .isInstanceOf(ResourceNotFoundException.class);

    verify(utilisateurRepository, never()).save(any());
  }

  private Jwt jwtWithSub(UUID sub) {
    Jwt jwt = mock(Jwt.class);
    when(jwt.getClaimAsString("sub")).thenReturn(sub.toString());
    return jwt;
  }
}
