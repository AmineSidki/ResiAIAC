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
import org.aminesidki.resiaiac.dto.ChambreDto;
import org.aminesidki.resiaiac.entity.Chambre;
import org.aminesidki.resiaiac.mapper.ChambreMapper;
import org.aminesidki.resiaiac.repository.ChambreRepository;
import org.aminesidki.resiaiac.service.impl.ChambreServiceImpl;
import org.aminesidki.resiaiac.util.ResourceFetcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link ChambreService}, exercised through its {@link ChambreServiceImpl}
 * implementation.
 *
 * <p>The service is wired manually (rather than via {@code @InjectMocks}) so the field under test
 * can be declared against the interface type.
 *
 * <p>ResourceFetcher.fetchResource is a static method, mocked per-test with Mockito's mockStatic
 * (requires Mockito 5+ / mockito-inline).
 */
@ExtendWith(MockitoExtension.class)
class ChambreServiceTest {

  @Mock private ChambreRepository chambreRepository;

  @Mock private ChambreMapper chambreMapper;

  private ChambreService chambreService;

  private UUID id;
  private Chambre entity;
  private ChambreDto dto;

  @BeforeEach
  void setUp() {
    chambreService = new ChambreServiceImpl(chambreRepository, chambreMapper);

    id = UUID.randomUUID();
    entity = Chambre.builder().id(id).matricule("A101").capacite(2L).etat(null).build();
    dto = new ChambreDto(id, "A101", 2L, null, List.of(), List.of(), List.of(), null);
  }

  // ---------- save ----------

  @Test
  void save_shouldMapPersistAndReturnDto() {
    ChambreDto inputDto =
        new ChambreDto(null, "B202", 3L, null, List.of(), List.of(), List.of(), null);
    Chambre mappedEntity = Chambre.builder().matricule("B202").capacite(3L).etat(null).build();
    Chambre savedEntity =
        Chambre.builder().id(id).matricule("B202").capacite(3L).etat(null).build();
    ChambreDto resultDto =
        new ChambreDto(id, "B202", 3L, null, List.of(), List.of(), List.of(), null);

    when(chambreMapper.toEntity(inputDto)).thenReturn(mappedEntity);
    when(chambreRepository.save(mappedEntity)).thenReturn(savedEntity);
    when(chambreMapper.toDto(savedEntity)).thenReturn(resultDto);

    ChambreDto result = chambreService.save(inputDto);

    assertThat(result).isEqualTo(resultDto);
    verify(chambreMapper).toEntity(inputDto);
    verify(chambreRepository).save(mappedEntity);
    verify(chambreMapper).toDto(savedEntity);
    verifyNoMoreInteractions(chambreRepository, chambreMapper);
  }

  // ---------- getById ----------

  @Test
  void getById_shouldFetchAndReturnDto() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, chambreRepository, "Chambre"))
          .thenReturn(entity);
      when(chambreMapper.toDto(entity)).thenReturn(dto);

      ChambreDto result = chambreService.getById(id);

      assertThat(result).isEqualTo(dto);
      fetcher.verify(() -> ResourceFetcher.fetchResource(id, chambreRepository, "Chambre"));
      verify(chambreMapper).toDto(entity);
      verifyNoMoreInteractions(chambreMapper);
    }
  }

  @Test
  void getById_shouldPropagateExceptionWhenNotFound() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      RuntimeException notFound = new RuntimeException("Chambre not found");
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, chambreRepository, "Chambre"))
          .thenThrow(notFound);

      assertThatThrownBy(() -> chambreService.getById(id)).isSameAs(notFound);

      verifyNoMoreInteractions(chambreMapper);
    }
  }

  // ---------- update ----------

  @Test
  void update_shouldFetchMutateSaveAndReturnDto() {
    Chambre savedEntity =
        Chambre.builder().id(id).matricule("A101 - renamed").capacite(2L).etat(null).build();
    ChambreDto resultDto =
        new ChambreDto(id, "A101 - renamed", 2L, null, List.of(), List.of(), List.of(), null);

    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, chambreRepository, "Chambre"))
          .thenReturn(entity);
      when(chambreRepository.save(entity)).thenReturn(savedEntity);
      when(chambreMapper.toDto(savedEntity)).thenReturn(resultDto);

      ChambreDto result = chambreService.update(id, dto);

      assertThat(result).isEqualTo(resultDto);
      fetcher.verify(() -> ResourceFetcher.fetchResource(id, chambreRepository, "Chambre"));
      verify(chambreMapper).updateEntityFromDto(dto, entity);
      verify(chambreRepository).save(entity);
      verify(chambreMapper).toDto(savedEntity);
      verifyNoMoreInteractions(chambreRepository, chambreMapper);
    }
  }

  @Test
  void update_shouldNotSaveWhenResourceNotFound() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      RuntimeException notFound = new RuntimeException("Chambre not found");
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, chambreRepository, "Chambre"))
          .thenThrow(notFound);

      assertThatThrownBy(() -> chambreService.update(id, dto)).isSameAs(notFound);

      verify(chambreRepository, never()).save(any());
      verifyNoMoreInteractions(chambreMapper);
    }
  }

  // ---------- delete ----------

  @Test
  void delete_shouldFetchAndDeleteEntity() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, chambreRepository, "Chambre"))
          .thenReturn(entity);

      chambreService.delete(id);

      fetcher.verify(() -> ResourceFetcher.fetchResource(id, chambreRepository, "Chambre"));
      verify(chambreRepository, times(1)).delete(entity);
      verifyNoMoreInteractions(chambreRepository);
    }
  }

  @Test
  void delete_shouldNotDeleteWhenResourceNotFound() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      RuntimeException notFound = new RuntimeException("Chambre not found");
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, chambreRepository, "Chambre"))
          .thenThrow(notFound);

      assertThatThrownBy(() -> chambreService.delete(id)).isSameAs(notFound);

      verify(chambreRepository, never()).delete(any());
    }
  }
}
