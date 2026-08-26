package org.aminesidki.resiaiac.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
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
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Unit tests for {@link UtilisateurService}, exercised through its {@link UtilisateurServiceImpl}
 * implementation.
 *
 * <p>The service is wired manually (rather than via {@code @InjectMocks}) so the field under test
 * can be declared against the interface type.
 *
 * <p>{@link UtilisateurLookupService} is mocked as an external dependency — it is a separate Spring
 * bean specifically so its {@code @Cacheable}/{@code @CacheEvict} methods always go through the
 * Spring AOP proxy when called from {@code UtilisateurServiceImpl} (self-invocation from within the
 * same bean would silently bypass the cache). This test does not verify caching itself, only that
 * {@code UtilisateurServiceImpl} calls the lookup service correctly.
 *
 * <p>ResourceFetcher.fetchResource is a static method, mocked per-test with Mockito's mockStatic
 * (requires Mockito 5+ / mockito-inline).
 */
@ExtendWith(MockitoExtension.class)
class UtilisateurServiceTest {

  @Mock private KeycloakService keycloakService;

  @Mock private UtilisateurRepository utilisateurRepository;

  @Mock private UtilisateurMapper utilisateurMapper;

  @Mock private UtilisateurLookupService utilisateurLookupService;

  private UtilisateurService utilisateurService;

  private UUID id;
  private UUID keycloakId;
  private Utilisateur entity;
  private UtilisateurDto dto;

  @BeforeEach
  void setUp() {
    utilisateurService =
        new UtilisateurServiceImpl(
            keycloakService, utilisateurRepository, utilisateurMapper, utilisateurLookupService);

    id = UUID.randomUUID();
    keycloakId = UUID.randomUUID();
    entity = Utilisateur.builder().id(id).keycloakUser(keycloakId).build();
    dto =
        new UtilisateurDto(
            id,
            "Nom",
            "Prenom",
            "CIN123",
            "Adresse",
            "0600000000",
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            null,
            null);
  }

  // ---------- getMyEntity ----------

  @Test
  void getMyEntity_shouldResolveIdFromJwtAndFetchEntity() {
    Jwt jwt = mock(Jwt.class);
    when(jwt.getClaimAsString("sub")).thenReturn(keycloakId.toString());
    when(utilisateurLookupService.getUtilisateurIdByKeycloakId(keycloakId)).thenReturn(id);

    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, utilisateurRepository, "Utilisateur"))
          .thenReturn(entity);

      Utilisateur result = utilisateurService.getMyEntity(jwt);

      assertThat(result).isEqualTo(entity);
      verify(utilisateurLookupService).getUtilisateurIdByKeycloakId(keycloakId);
      fetcher.verify(() -> ResourceFetcher.fetchResource(id, utilisateurRepository, "Utilisateur"));
    }
  }

  @Test
  void getMyEntity_shouldThrowWhenJwtHasNoSubClaim() {
    Jwt jwt = mock(Jwt.class);
    when(jwt.getClaimAsString("sub")).thenReturn(null);

    assertThatThrownBy(() -> utilisateurService.getMyEntity(jwt))
        .isInstanceOf(ResourceNotFoundException.class);

    verifyNoMoreInteractions(utilisateurLookupService, utilisateurRepository);
  }

  // ---------- getMyDto ----------

  @Test
  void getMyDto_shouldResolveIdFromJwtAndReturnCachedDto() {
    Jwt jwt = mock(Jwt.class);
    when(jwt.getClaimAsString("sub")).thenReturn(keycloakId.toString());
    when(utilisateurLookupService.getUtilisateurIdByKeycloakId(keycloakId)).thenReturn(id);
    when(utilisateurLookupService.getUtilisateurDtoById(id)).thenReturn(dto);

    UtilisateurDto result = utilisateurService.getMyDto(jwt);

    assertThat(result).isEqualTo(dto);
    verify(utilisateurLookupService).getUtilisateurIdByKeycloakId(keycloakId);
    verify(utilisateurLookupService).getUtilisateurDtoById(id);
  }

  // ---------- updateMe ----------

  @Test
  void updateMe_shouldMutateSaveAndEvictDtoCache() {
    Jwt jwt = mock(Jwt.class);
    when(jwt.getClaimAsString("sub")).thenReturn(keycloakId.toString());
    when(utilisateurLookupService.getUtilisateurIdByKeycloakId(keycloakId)).thenReturn(id);

    UpdateMeRequest request = new UpdateMeRequest("Nouvelle Adresse", "0611111111");
    UtilisateurDto filteredDto =
        new UtilisateurDto(
            null,
            null,
            null,
            null,
            "Nouvelle Adresse",
            "0611111111",
            null,
            null,
            null,
            null,
            null,
            null);
    Utilisateur savedEntity = Utilisateur.builder().id(id).keycloakUser(keycloakId).build();
    UtilisateurDto resultDto =
        new UtilisateurDto(
            id,
            "Nom",
            "Prenom",
            "CIN123",
            "Nouvelle Adresse",
            "0611111111",
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
      when(utilisateurMapper.updateMeRequestToDto(request)).thenReturn(filteredDto);
      when(utilisateurRepository.save(entity)).thenReturn(savedEntity);
      when(utilisateurMapper.toDto(savedEntity)).thenReturn(resultDto);

      UtilisateurDto result = utilisateurService.updateMe(jwt, request);

      assertThat(result).isEqualTo(resultDto);
      verify(utilisateurMapper).updateEntityFromDto(filteredDto, entity);
      verify(utilisateurRepository).save(entity);
      verify(utilisateurLookupService).evictUtilisateurDtoById(id);
    }
  }

  // ---------- getAll ----------

  @Test
  void getAll_shouldReturnMappedPage() {
    Pageable pageable = PageRequest.of(0, 20);
    var page = new PageImpl<>(List.of(entity));

    when(utilisateurRepository.findAll(pageable)).thenReturn(page);
    when(utilisateurMapper.toDto(entity)).thenReturn(dto);

    var result = utilisateurService.getAll(pageable);

    assertThat(result.getContent()).containsExactly(dto);
    verify(utilisateurRepository).findAll(pageable);
  }

  // ---------- save ----------

  @Test
  void save_shouldCreateKeycloakUserThenPersistAndReturnDto() {
    UtilisateurDto inputDto =
        new UtilisateurDto(
            null,
            "Nom",
            "Prenom",
            "CIN123",
            "Adresse",
            "0600000000",
            null,
            null,
            null,
            null,
            null,
            null);
    Utilisateur mappedEntity = Utilisateur.builder().build();
    Utilisateur savedEntity = Utilisateur.builder().id(id).keycloakUser(keycloakId).build();

    when(keycloakService.createUser(inputDto)).thenReturn(keycloakId);
    when(utilisateurMapper.toEntity(inputDto)).thenReturn(mappedEntity);
    when(utilisateurRepository.save(mappedEntity)).thenReturn(savedEntity);
    when(utilisateurMapper.toDto(savedEntity)).thenReturn(dto);

    UtilisateurDto result = utilisateurService.save(inputDto);

    assertThat(result).isEqualTo(dto);
    assertThat(mappedEntity.getKeycloakUser()).isEqualTo(keycloakId);
    verify(keycloakService).createUser(inputDto);
    verify(utilisateurRepository).save(mappedEntity);
  }

  @Test
  void save_shouldRollbackKeycloakUserWhenPersistFails() {
    UtilisateurDto inputDto =
        new UtilisateurDto(
            null,
            "Nom",
            "Prenom",
            "CIN123",
            "Adresse",
            "0600000000",
            null,
            null,
            null,
            null,
            null,
            null);
    Utilisateur mappedEntity = Utilisateur.builder().build();
    RuntimeException dbFailure = new RuntimeException("DB down");

    when(keycloakService.createUser(inputDto)).thenReturn(keycloakId);
    when(utilisateurMapper.toEntity(inputDto)).thenReturn(mappedEntity);
    when(utilisateurRepository.save(mappedEntity)).thenThrow(dbFailure);

    assertThatThrownBy(() -> utilisateurService.save(inputDto)).isSameAs(dbFailure);

    verify(keycloakService).deleteUser(keycloakId);
  }

  // ---------- getById ----------

  @Test
  void getById_shouldDelegateToLookupService() {
    when(utilisateurLookupService.getUtilisateurDtoById(id)).thenReturn(dto);

    UtilisateurDto result = utilisateurService.getById(id);

    assertThat(result).isEqualTo(dto);
    verify(utilisateurLookupService).getUtilisateurDtoById(id);
  }

  // ---------- update ----------

  @Test
  void update_shouldFetchMutateSaveAndEvictDtoCache() {
    UtilisateurDto resultDto =
        new UtilisateurDto(
            id,
            "NomModifie",
            "Prenom",
            "CIN123",
            "Adresse",
            "0600000000",
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
      when(utilisateurRepository.save(entity)).thenReturn(entity);
      when(utilisateurMapper.toDto(entity)).thenReturn(resultDto);

      UtilisateurDto result = utilisateurService.update(id, dto);

      assertThat(result).isEqualTo(resultDto);
      verify(utilisateurMapper).updateEntityFromDto(dto, entity);
      verify(utilisateurRepository).save(entity);
      verify(utilisateurLookupService).evictUtilisateurDtoById(id);
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
      verifyNoMoreInteractions(utilisateurLookupService);
    }
  }

  // ---------- delete ----------

  @Test
  void delete_shouldDeleteFromDbKeycloakAndEvictBothCaches() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, utilisateurRepository, "Utilisateur"))
          .thenReturn(entity);

      utilisateurService.delete(id);

      verify(utilisateurRepository, times(1)).delete(entity);
      verify(keycloakService).deleteUser(keycloakId);
      verify(utilisateurLookupService).evictUtilisateurIdByKeycloakId(keycloakId);
      verify(utilisateurLookupService).evictUtilisateurDtoById(id);
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
      verifyNoMoreInteractions(keycloakService, utilisateurLookupService);
    }
  }
}
