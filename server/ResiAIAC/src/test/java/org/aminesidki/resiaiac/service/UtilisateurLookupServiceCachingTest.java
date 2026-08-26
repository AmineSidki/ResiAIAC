package org.aminesidki.resiaiac.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import org.aminesidki.resiaiac.configuration.TestCacheConfiguration;
import org.aminesidki.resiaiac.entity.Utilisateur;
import org.aminesidki.resiaiac.mapper.UtilisateurMapper;
import org.aminesidki.resiaiac.repository.UtilisateurRepository;
import org.aminesidki.resiaiac.service.impl.UtilisateurLookupServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

/**
 * Verifies the ACTUAL caching behavior (not just business logic) of {@link
 * UtilisateurLookupServiceImpl}: that a second call with the same argument is served from cache
 * (repository hit only once), and that eviction forces a real repository call again.
 *
 * <p>Loads a minimal Spring context — only this service + {@link TestCacheConfiguration} — rather
 * than the full application context ({@code @SpringBootTest}), so the Spring AOP cache proxy is
 * real while startup stays fast. The fake in-memory cache manager (from {@link
 * TestCacheConfiguration}) replaces Redis so no Docker/connection is needed to run this test.
 *
 * <p>{@code UtilisateurRepository}/{@code UtilisateurMapper} are declared with
 * {@code @MockitoBean}, not plain Mockito {@code @Mock}: this class boots a real (minimal) Spring
 * context via {@code @SpringJUnitConfig}, not {@code MockitoExtension}, so the mocks must be
 * registered as actual Spring beans for {@code UtilisateurLookupServiceImpl}'s constructor
 * injection to resolve them — a plain {@code @Mock} field here is never wired into the context and
 * Spring would fail to find a bean for the constructor parameter.
 *
 * <p>Business logic itself (exception handling, resolution correctness) is covered separately by
 * {@code UtilisateurLookupServiceImplTest}.
 */
@SpringJUnitConfig
@ContextConfiguration(classes = {UtilisateurLookupServiceImpl.class, TestCacheConfiguration.class})
class UtilisateurLookupServiceCachingTest {

  @Autowired private UtilisateurLookupService utilisateurLookupService;
  @Autowired private CacheManager cacheManager;

  @MockitoBean private UtilisateurRepository utilisateurRepository;

  @MockitoBean private UtilisateurMapper utilisateurMapper;

  @Test
  void getUtilisateurIdByKeycloakId_secondCallShouldNotHitRepositoryAgain() {
    UUID keycloakId = UUID.randomUUID();
    UUID id = UUID.randomUUID();
    Utilisateur entity = Utilisateur.builder().id(id).keycloakUser(keycloakId).build();

    when(utilisateurRepository.findByKeycloakUser(keycloakId)).thenReturn(Optional.of(entity));

    UUID first = utilisateurLookupService.getUtilisateurIdByKeycloakId(keycloakId);
    UUID second = utilisateurLookupService.getUtilisateurIdByKeycloakId(keycloakId);

    assertThat(first).isEqualTo(id);
    assertThat(second).isEqualTo(id);
    // The real proof the cache works: repository only hit once for two identical calls.
    verify(utilisateurRepository, times(1)).findByKeycloakUser(keycloakId);
  }

  @Test
  void evictUtilisateurIdByKeycloakId_shouldForceRepositoryToBeHitAgain() {
    UUID keycloakId = UUID.randomUUID();
    UUID id = UUID.randomUUID();
    Utilisateur entity = Utilisateur.builder().id(id).keycloakUser(keycloakId).build();

    when(utilisateurRepository.findByKeycloakUser(keycloakId)).thenReturn(Optional.of(entity));

    utilisateurLookupService.getUtilisateurIdByKeycloakId(keycloakId); // caches it
    utilisateurLookupService.evictUtilisateurIdByKeycloakId(keycloakId); // clears it
    utilisateurLookupService.getUtilisateurIdByKeycloakId(keycloakId); // must hit repo again

    verify(utilisateurRepository, times(2)).findByKeycloakUser(keycloakId);
  }

  @Test
  void getUtilisateurDtoById_secondCallShouldNotHitRepositoryAgain() {
    UUID id = UUID.randomUUID();
    Utilisateur entity = Utilisateur.builder().id(id).build();
    org.aminesidki.resiaiac.dto.UtilisateurDto dto =
        new org.aminesidki.resiaiac.dto.UtilisateurDto(
            id,
            "Nom",
            "Prenom",
            "CIN123",
            "Adresse",
            "0600000000",
            java.util.List.of(),
            java.util.List.of(),
            java.util.List.of(),
            java.util.List.of(),
            null,
            null);

    when(utilisateurRepository.findById(id)).thenReturn(Optional.of(entity));
    when(utilisateurMapper.toDto(entity)).thenReturn(dto);

    utilisateurLookupService.getUtilisateurDtoById(id);
    utilisateurLookupService.getUtilisateurDtoById(id);

    verify(utilisateurRepository, times(1)).findById(id);
  }
}
