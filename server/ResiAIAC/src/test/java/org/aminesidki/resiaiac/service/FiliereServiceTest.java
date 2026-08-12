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
import org.aminesidki.resiaiac.dto.FiliereDto;
import org.aminesidki.resiaiac.entity.Filiere;
import org.aminesidki.resiaiac.mapper.FiliereMapper;
import org.aminesidki.resiaiac.repository.FiliereRepository;
import org.aminesidki.resiaiac.service.impl.FiliereServiceImpl;
import org.aminesidki.resiaiac.util.ResourceFetcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link FiliereService}, exercised through its {@link FiliereServiceImpl}
 * implementation.
 *
 * <p>Unlike most other services in this module, Filiere uses a {@code Long} id rather than {@code
 * UUID}.
 *
 * <p>ResourceFetcher.fetchResource is a static method, mocked per-test with Mockito's mockStatic
 * (requires Mockito 5+ / mockito-inline).
 */
@ExtendWith(MockitoExtension.class)
class FiliereServiceTest {

  @Mock private FiliereRepository filiereRepository;

  @Mock private FiliereMapper filiereMapper;

  private FiliereService filiereService;

  private Long id;
  private Filiere entity;
  private FiliereDto dto;

  @BeforeEach
  void setUp() {
    filiereService = new FiliereServiceImpl(filiereRepository, filiereMapper);

    id = 1L;
    entity = Filiere.builder().id(id).nom("Genie Informatique").niveauMaximal(5).build();
    dto = new FiliereDto(id, "Genie Informatique", 5, List.of());
  }

  // ---------- save ----------

  @Test
  void save_shouldMapPersistAndReturnDto() {
    FiliereDto inputDto = new FiliereDto(null, "Genie Informatique", 5, List.of());
    Filiere mappedEntity = Filiere.builder().nom("Genie Informatique").niveauMaximal(5).build();
    Filiere savedEntity =
        Filiere.builder().id(id).nom("Genie Informatique").niveauMaximal(5).build();
    FiliereDto resultDto = new FiliereDto(id, "Genie Informatique", 5, List.of());

    when(filiereMapper.toEntity(inputDto)).thenReturn(mappedEntity);
    when(filiereRepository.save(mappedEntity)).thenReturn(savedEntity);
    when(filiereMapper.toDto(savedEntity)).thenReturn(resultDto);

    FiliereDto result = filiereService.save(inputDto);

    assertThat(result).isEqualTo(resultDto);
    verify(filiereMapper).toEntity(inputDto);
    verify(filiereRepository).save(mappedEntity);
    verify(filiereMapper).toDto(savedEntity);
    verifyNoMoreInteractions(filiereRepository, filiereMapper);
  }

  // ---------- getById ----------

  @Test
  void getById_shouldFetchAndReturnDto() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, filiereRepository, "Filiere"))
          .thenReturn(entity);
      when(filiereMapper.toDto(entity)).thenReturn(dto);

      FiliereDto result = filiereService.getById(id);

      assertThat(result).isEqualTo(dto);
      fetcher.verify(() -> ResourceFetcher.fetchResource(id, filiereRepository, "Filiere"));
      verify(filiereMapper).toDto(entity);
      verifyNoMoreInteractions(filiereMapper);
    }
  }

  @Test
  void getById_shouldPropagateExceptionWhenNotFound() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      RuntimeException notFound = new RuntimeException("Filiere not found");
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, filiereRepository, "Filiere"))
          .thenThrow(notFound);

      assertThatThrownBy(() -> filiereService.getById(id)).isSameAs(notFound);

      verifyNoMoreInteractions(filiereMapper);
    }
  }

  // ---------- update ----------

  @Test
  void update_shouldFetchMutateSaveAndReturnDto() {
    Filiere savedEntity =
        Filiere.builder().id(id).nom("Genie Informatique - renamed").niveauMaximal(5).build();
    FiliereDto resultDto = new FiliereDto(id, "Genie Informatique - renamed", 5, List.of());

    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, filiereRepository, "Filiere"))
          .thenReturn(entity);
      when(filiereRepository.save(entity)).thenReturn(savedEntity);
      when(filiereMapper.toDto(savedEntity)).thenReturn(resultDto);

      FiliereDto result = filiereService.update(id, dto);

      assertThat(result).isEqualTo(resultDto);
      fetcher.verify(() -> ResourceFetcher.fetchResource(id, filiereRepository, "Filiere"));
      verify(filiereMapper).updateEntityFromDto(dto, entity);
      verify(filiereRepository).save(entity);
      verify(filiereMapper).toDto(savedEntity);
      verifyNoMoreInteractions(filiereRepository, filiereMapper);
    }
  }

  @Test
  void update_shouldNotSaveWhenResourceNotFound() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      RuntimeException notFound = new RuntimeException("Filiere not found");
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, filiereRepository, "Filiere"))
          .thenThrow(notFound);

      assertThatThrownBy(() -> filiereService.update(id, dto)).isSameAs(notFound);

      verify(filiereRepository, never()).save(any());
      verifyNoMoreInteractions(filiereMapper);
    }
  }

  // ---------- delete ----------

  @Test
  void delete_shouldFetchAndDeleteEntity() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, filiereRepository, "Filiere"))
          .thenReturn(entity);

      filiereService.delete(id);

      fetcher.verify(() -> ResourceFetcher.fetchResource(id, filiereRepository, "Filiere"));
      verify(filiereRepository, times(1)).delete(entity);
      verifyNoMoreInteractions(filiereRepository);
    }
  }

  @Test
  void delete_shouldNotDeleteWhenResourceNotFound() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      RuntimeException notFound = new RuntimeException("Filiere not found");
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, filiereRepository, "Filiere"))
          .thenThrow(notFound);

      assertThatThrownBy(() -> filiereService.delete(id)).isSameAs(notFound);

      verify(filiereRepository, never()).delete(any());
    }
  }
}
