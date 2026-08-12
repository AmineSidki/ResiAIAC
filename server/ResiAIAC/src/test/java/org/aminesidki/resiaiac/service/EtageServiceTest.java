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
import org.aminesidki.resiaiac.dto.EtageDto;
import org.aminesidki.resiaiac.entity.Etage;
import org.aminesidki.resiaiac.mapper.EtageMapper;
import org.aminesidki.resiaiac.repository.EtageRepository;
import org.aminesidki.resiaiac.service.impl.EtageServiceImpl;
import org.aminesidki.resiaiac.util.ResourceFetcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link EtageService}, exercised through its {@link EtageServiceImpl}
 * implementation.
 *
 * <p>The service is wired manually (rather than via {@code @InjectMocks}) so the field under test
 * can be declared against the interface type.
 *
 * <p>ResourceFetcher.fetchResource is a static method, mocked per-test with Mockito's mockStatic
 * (requires Mockito 5+ / mockito-inline).
 */
@ExtendWith(MockitoExtension.class)
class EtageServiceTest {

  @Mock private EtageRepository etageRepository;

  @Mock private EtageMapper etageMapper;

  private EtageService etageService;

  private UUID id;
  private Etage entity;
  private EtageDto dto;

  @BeforeEach
  void setUp() {
    etageService = new EtageServiceImpl(etageRepository, etageMapper);

    id = UUID.randomUUID();
    entity = Etage.builder().id(id).numero("1").build();
    dto = new EtageDto(id, "1", null, List.of());
  }

  // ---------- save ----------

  @Test
  void save_shouldMapPersistAndReturnDto() {
    EtageDto inputDto = new EtageDto(null, "2", null, List.of());
    Etage mappedEntity = Etage.builder().numero("2").build();
    Etage savedEntity = Etage.builder().id(id).numero("2").build();
    EtageDto resultDto = new EtageDto(id, "2", null, List.of());

    when(etageMapper.toEntity(inputDto)).thenReturn(mappedEntity);
    when(etageRepository.save(mappedEntity)).thenReturn(savedEntity);
    when(etageMapper.toDto(savedEntity)).thenReturn(resultDto);

    EtageDto result = etageService.save(inputDto);

    assertThat(result).isEqualTo(resultDto);
    verify(etageMapper).toEntity(inputDto);
    verify(etageRepository).save(mappedEntity);
    verify(etageMapper).toDto(savedEntity);
    verifyNoMoreInteractions(etageRepository, etageMapper);
  }

  // ---------- getById ----------

  @Test
  void getById_shouldFetchAndReturnDto() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, etageRepository, "Etage"))
          .thenReturn(entity);
      when(etageMapper.toDto(entity)).thenReturn(dto);

      EtageDto result = etageService.getById(id);

      assertThat(result).isEqualTo(dto);
      fetcher.verify(() -> ResourceFetcher.fetchResource(id, etageRepository, "Etage"));
      verify(etageMapper).toDto(entity);
      verifyNoMoreInteractions(etageMapper);
    }
  }

  @Test
  void getById_shouldPropagateExceptionWhenNotFound() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      RuntimeException notFound = new RuntimeException("Etage not found");
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, etageRepository, "Etage"))
          .thenThrow(notFound);

      assertThatThrownBy(() -> etageService.getById(id)).isSameAs(notFound);

      verifyNoMoreInteractions(etageMapper);
    }
  }

  // ---------- update ----------

  @Test
  void update_shouldFetchMutateSaveAndReturnDto() {
    Etage savedEntity = Etage.builder().id(id).numero("1 - renamed").build();
    EtageDto resultDto = new EtageDto(id, "1 - renamed", null, List.of());

    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, etageRepository, "Etage"))
          .thenReturn(entity);
      when(etageRepository.save(entity)).thenReturn(savedEntity);
      when(etageMapper.toDto(savedEntity)).thenReturn(resultDto);

      EtageDto result = etageService.update(id, dto);

      assertThat(result).isEqualTo(resultDto);
      fetcher.verify(() -> ResourceFetcher.fetchResource(id, etageRepository, "Etage"));
      verify(etageMapper).updateEntityFromDto(dto, entity);
      verify(etageRepository).save(entity);
      verify(etageMapper).toDto(savedEntity);
      verifyNoMoreInteractions(etageRepository, etageMapper);
    }
  }

  @Test
  void update_shouldNotSaveWhenResourceNotFound() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      RuntimeException notFound = new RuntimeException("Etage not found");
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, etageRepository, "Etage"))
          .thenThrow(notFound);

      assertThatThrownBy(() -> etageService.update(id, dto)).isSameAs(notFound);

      verify(etageRepository, never()).save(any());
      verifyNoMoreInteractions(etageMapper);
    }
  }

  // ---------- delete ----------

  @Test
  void delete_shouldFetchAndDeleteEntity() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, etageRepository, "Etage"))
          .thenReturn(entity);

      etageService.delete(id);

      fetcher.verify(() -> ResourceFetcher.fetchResource(id, etageRepository, "Etage"));
      verify(etageRepository, times(1)).delete(entity);
      verifyNoMoreInteractions(etageRepository);
    }
  }

  @Test
  void delete_shouldNotDeleteWhenResourceNotFound() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      RuntimeException notFound = new RuntimeException("Etage not found");
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, etageRepository, "Etage"))
          .thenThrow(notFound);

      assertThatThrownBy(() -> etageService.delete(id)).isSameAs(notFound);

      verify(etageRepository, never()).delete(any());
    }
  }
}
