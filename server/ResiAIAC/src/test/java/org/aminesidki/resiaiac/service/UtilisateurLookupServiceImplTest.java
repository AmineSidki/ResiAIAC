package org.aminesidki.resiaiac.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import org.aminesidki.resiaiac.dto.UtilisateurDto;
import org.aminesidki.resiaiac.entity.Utilisateur;
import org.aminesidki.resiaiac.exception.ResourceNotFoundException;
import org.aminesidki.resiaiac.mapper.UtilisateurMapper;
import org.aminesidki.resiaiac.repository.UtilisateurRepository;
import org.aminesidki.resiaiac.service.impl.UtilisateurLookupServiceImpl;
import org.aminesidki.resiaiac.util.ResourceFetcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link UtilisateurLookupService}, exercised through its {@link
 * UtilisateurLookupServiceImpl} implementation.
 *
 * <p>The service is wired manually (rather than via {@code @InjectMocks}) so the field under test
 * can be declared against the interface type.
 *
 * <p>This is a pure Mockito unit test: it verifies the underlying business logic (resolution,
 * exception on not-found) but NOT the actual caching behavior of {@code @Cacheable} /
 * {@code @CacheEvict} — Mockito calls the real method body every time regardless of the cache
 * annotations. Verifying that repeated calls actually hit Redis only once requires a separate
 * {@code @SpringBootTest}-based test with a real cache context.
 *
 * <p>ResourceFetcher.fetchResource is a static method, mocked per-test with Mockito's mockStatic
 * (requires Mockito 5+ / mockito-inline).
 */
@ExtendWith(MockitoExtension.class)
class UtilisateurLookupServiceImplTest {

  @Mock private UtilisateurRepository utilisateurRepository;

  @Mock private UtilisateurMapper utilisateurMapper;

  private UtilisateurLookupService utilisateurLookupService;

  private UUID id;
  private UUID keycloakId;
  private Utilisateur entity;
  private UtilisateurDto dto;

  @BeforeEach
  void setUp() {
    utilisateurLookupService =
        new UtilisateurLookupServiceImpl(utilisateurRepository, utilisateurMapper);

    id = UUID.randomUUID();
    keycloakId = UUID.randomUUID();
    entity = Utilisateur.builder().id(id).keycloakUser(keycloakId).build();
    dto =
        new UtilisateurDto(
            id,
            "Nom",
            "Prenom",
            "CIN",
            "Adresse",
            "0600000000",
            java.util.List.of(),
            java.util.List.of(),
            java.util.List.of(),
            java.util.List.of(),
            null,
            null);
  }

  // ---------- getUtilisateurIdByKeycloakId ----------

  @Test
  void getUtilisateurIdByKeycloakId_shouldReturnResolvedId() {
    when(utilisateurRepository.findByKeycloakUser(keycloakId)).thenReturn(Optional.of(entity));

    UUID result = utilisateurLookupService.getUtilisateurIdByKeycloakId(keycloakId);

    assertThat(result).isEqualTo(id);
    verify(utilisateurRepository).findByKeycloakUser(keycloakId);
  }

  @Test
  void getUtilisateurIdByKeycloakId_shouldThrowWhenNotFound() {
    when(utilisateurRepository.findByKeycloakUser(keycloakId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> utilisateurLookupService.getUtilisateurIdByKeycloakId(keycloakId))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  // ---------- evictUtilisateurIdByKeycloakId ----------

  @Test
  void evictUtilisateurIdByKeycloakId_shouldNotTouchRepositoryOrMapper() {
    utilisateurLookupService.evictUtilisateurIdByKeycloakId(keycloakId);

    verifyNoMoreInteractions(utilisateurRepository, utilisateurMapper);
  }

  // ---------- getUtilisateurDtoById ----------

  @Test
  void getUtilisateurDtoById_shouldFetchAndReturnDto() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, utilisateurRepository, "Utilisateur"))
          .thenReturn(entity);
      when(utilisateurMapper.toDto(entity)).thenReturn(dto);

      UtilisateurDto result = utilisateurLookupService.getUtilisateurDtoById(id);

      assertThat(result).isEqualTo(dto);
      fetcher.verify(() -> ResourceFetcher.fetchResource(id, utilisateurRepository, "Utilisateur"));
      verify(utilisateurMapper).toDto(entity);
      verifyNoMoreInteractions(utilisateurMapper);
    }
  }

  @Test
  void getUtilisateurDtoById_shouldPropagateExceptionWhenNotFound() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      RuntimeException notFound = new RuntimeException("Utilisateur not found");
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, utilisateurRepository, "Utilisateur"))
          .thenThrow(notFound);

      assertThatThrownBy(() -> utilisateurLookupService.getUtilisateurDtoById(id))
          .isSameAs(notFound);

      verifyNoMoreInteractions(utilisateurMapper);
    }
  }

  // ---------- evictUtilisateurDtoById ----------

  @Test
  void evictUtilisateurDtoById_shouldNotTouchRepositoryOrMapper() {
    utilisateurLookupService.evictUtilisateurDtoById(id);

    verifyNoMoreInteractions(utilisateurRepository, utilisateurMapper);
  }
}
