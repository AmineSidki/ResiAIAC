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
import org.aminesidki.resiaiac.dto.EquipementDto;
import org.aminesidki.resiaiac.entity.Equipement;
import org.aminesidki.resiaiac.mapper.EquipementMapper;
import org.aminesidki.resiaiac.repository.EquipementRepository;
import org.aminesidki.resiaiac.service.impl.EquipementServiceImpl;
import org.aminesidki.resiaiac.util.ResourceFetcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link EquipementService}, exercised through its {@link EquipementServiceImpl}
 * implementation.
 *
 * <p>Unlike the other services in this module, Equipement uses a {@code Long} id rather than {@code
 * UUID}.
 *
 * <p>ResourceFetcher.fetchResource is a static method, mocked per-test with Mockito's mockStatic
 * (requires Mockito 5+ / mockito-inline).
 */
@ExtendWith(MockitoExtension.class)
class EquipementServiceTest {

  @Mock private EquipementRepository equipementRepository;

  @Mock private EquipementMapper equipementMapper;

  private EquipementService equipementService;

  private Long id;
  private Equipement entity;
  private EquipementDto dto;

  @BeforeEach
  void setUp() {
    equipementService = new EquipementServiceImpl(equipementRepository, equipementMapper);

    id = 1L;
    entity = Equipement.builder().id(id).nom("Climatiseur").build();
    dto = new EquipementDto(id, "Climatiseur", List.of(), List.of());
  }

  // ---------- save ----------

  @Test
  void save_shouldMapPersistAndReturnDto() {
    EquipementDto inputDto = new EquipementDto(null, "Climatiseur", List.of(), List.of());
    Equipement mappedEntity = Equipement.builder().nom("Climatiseur").build();
    Equipement savedEntity = Equipement.builder().id(id).nom("Climatiseur").build();
    EquipementDto resultDto = new EquipementDto(id, "Climatiseur", List.of(), List.of());

    when(equipementMapper.toEntity(inputDto)).thenReturn(mappedEntity);
    when(equipementRepository.save(mappedEntity)).thenReturn(savedEntity);
    when(equipementMapper.toDto(savedEntity)).thenReturn(resultDto);

    EquipementDto result = equipementService.save(inputDto);

    assertThat(result).isEqualTo(resultDto);
    verify(equipementMapper).toEntity(inputDto);
    verify(equipementRepository).save(mappedEntity);
    verify(equipementMapper).toDto(savedEntity);
    verifyNoMoreInteractions(equipementRepository, equipementMapper);
  }

  // ---------- getById ----------

  @Test
  void getById_shouldFetchAndReturnDto() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, equipementRepository, "Equipement"))
          .thenReturn(entity);
      when(equipementMapper.toDto(entity)).thenReturn(dto);

      EquipementDto result = equipementService.getById(id);

      assertThat(result).isEqualTo(dto);
      fetcher.verify(() -> ResourceFetcher.fetchResource(id, equipementRepository, "Equipement"));
      verify(equipementMapper).toDto(entity);
      verifyNoMoreInteractions(equipementMapper);
    }
  }

  @Test
  void getById_shouldPropagateExceptionWhenNotFound() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      RuntimeException notFound = new RuntimeException("Equipement not found");
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, equipementRepository, "Equipement"))
          .thenThrow(notFound);

      assertThatThrownBy(() -> equipementService.getById(id)).isSameAs(notFound);

      verifyNoMoreInteractions(equipementMapper);
    }
  }

  // ---------- update ----------

  @Test
  void update_shouldFetchMutateSaveAndReturnDto() {
    Equipement savedEntity = Equipement.builder().id(id).nom("Climatiseur - renamed").build();
    EquipementDto resultDto = new EquipementDto(id, "Climatiseur - renamed", List.of(), List.of());

    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, equipementRepository, "Equipement"))
          .thenReturn(entity);
      when(equipementRepository.save(entity)).thenReturn(savedEntity);
      when(equipementMapper.toDto(savedEntity)).thenReturn(resultDto);

      EquipementDto result = equipementService.update(id, dto);

      assertThat(result).isEqualTo(resultDto);
      fetcher.verify(() -> ResourceFetcher.fetchResource(id, equipementRepository, "Equipement"));
      verify(equipementMapper).updateEntityFromDto(dto, entity);
      verify(equipementRepository).save(entity);
      verify(equipementMapper).toDto(savedEntity);
      verifyNoMoreInteractions(equipementRepository, equipementMapper);
    }
  }

  @Test
  void update_shouldNotSaveWhenResourceNotFound() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      RuntimeException notFound = new RuntimeException("Equipement not found");
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, equipementRepository, "Equipement"))
          .thenThrow(notFound);

      assertThatThrownBy(() -> equipementService.update(id, dto)).isSameAs(notFound);

      verify(equipementRepository, never()).save(any());
      verifyNoMoreInteractions(equipementMapper);
    }
  }

  // ---------- delete ----------

  @Test
  void delete_shouldFetchAndDeleteEntity() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, equipementRepository, "Equipement"))
          .thenReturn(entity);

      equipementService.delete(id);

      fetcher.verify(() -> ResourceFetcher.fetchResource(id, equipementRepository, "Equipement"));
      verify(equipementRepository, times(1)).delete(entity);
      verifyNoMoreInteractions(equipementRepository);
    }
  }

  @Test
  void delete_shouldNotDeleteWhenResourceNotFound() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      RuntimeException notFound = new RuntimeException("Equipement not found");
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, equipementRepository, "Equipement"))
          .thenThrow(notFound);

      assertThatThrownBy(() -> equipementService.delete(id)).isSameAs(notFound);

      verify(equipementRepository, never()).delete(any());
    }
  }
}
