package org.aminesidki.resiaiac.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import org.aminesidki.resiaiac.dto.UtilisateurDto;
import org.aminesidki.resiaiac.entity.Utilisateur;
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

/**
 * Unit tests for {@link UtilisateurService}, exercised through its {@link UtilisateurServiceImpl}
 * implementation.
 *
 * <p>ResourceFetcher.fetchResource is a static method, mocked per-test with Mockito's mockStatic
 * (requires Mockito 5+ / mockito-inline).
 */
@ExtendWith(MockitoExtension.class)
class UtilisateurServiceTest {

  @Mock private UtilisateurRepository utilisateurRepository;

  @Mock private UtilisateurMapper utilisateurMapper;

  private UtilisateurService utilisateurService;

  private UUID id;
  private Utilisateur entity;
  private UtilisateurDto dto;

  @BeforeEach
  void setUp() {
    utilisateurService = new UtilisateurServiceImpl(utilisateurRepository, utilisateurMapper);

    id = UUID.randomUUID();
    entity = Utilisateur.builder().id(id).nom("Sidki").prenom("Amine").build();
    dto =
        new UtilisateurDto(
            id, "Sidki", "Amine", null, null, null, List.of(), List.of(), List.of(), List.of(),
            null, null);
  }

  // ---------- save ----------

  @Test
  void save_shouldMapPersistAndReturnDto() {
    UtilisateurDto inputDto =
        new UtilisateurDto(
            null, "Sidki", "Amine", null, null, null, List.of(), List.of(), List.of(), List.of(),
            null, null);
    Utilisateur mappedEntity = Utilisateur.builder().nom("Sidki").prenom("Amine").build();
    Utilisateur savedEntity = Utilisateur.builder().id(id).nom("Sidki").prenom("Amine").build();
    UtilisateurDto resultDto =
        new UtilisateurDto(
            id, "Sidki", "Amine", null, null, null, List.of(), List.of(), List.of(), List.of(),
            null, null);

    when(utilisateurMapper.toEntity(inputDto)).thenReturn(mappedEntity);
    when(utilisateurRepository.save(mappedEntity)).thenReturn(savedEntity);
    when(utilisateurMapper.toDto(savedEntity)).thenReturn(resultDto);

    UtilisateurDto result = utilisateurService.save(inputDto);

    assertThat(result).isEqualTo(resultDto);
    verify(utilisateurMapper).toEntity(inputDto);
    verify(utilisateurRepository).save(mappedEntity);
    verify(utilisateurMapper).toDto(savedEntity);
    verifyNoMoreInteractions(utilisateurRepository, utilisateurMapper);
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
    }
  }

  // ---------- update ----------

  @Test
  void update_shouldFetchMutateSaveAndReturnDto() {
    Utilisateur savedEntity =
        Utilisateur.builder().id(id).nom("Sidki").prenom("Amine-renamed").build();
    UtilisateurDto resultDto =
        new UtilisateurDto(
            id,
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
    }
  }

  // ---------- delete ----------

  @Test
  void delete_shouldFetchAndDeleteEntity() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, utilisateurRepository, "Utilisateur"))
          .thenReturn(entity);

      utilisateurService.delete(id);

      fetcher.verify(() -> ResourceFetcher.fetchResource(id, utilisateurRepository, "Utilisateur"));
      verify(utilisateurRepository, times(1)).delete(entity);
      verifyNoMoreInteractions(utilisateurRepository);
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
    }
  }
}
