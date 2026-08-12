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
import org.aminesidki.resiaiac.dto.BatimentDto;
import org.aminesidki.resiaiac.entity.Batiment;
import org.aminesidki.resiaiac.mapper.BatimentMapper;
import org.aminesidki.resiaiac.repository.BatimentRepository;
import org.aminesidki.resiaiac.service.impl.BatimentServiceImpl;
import org.aminesidki.resiaiac.util.ResourceFetcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link BatimentService}, exercised through its {@link BatimentServiceImpl}
 * implementation.
 *
 * <p>The service is wired manually (rather than via {@code @InjectMocks}) so the field under test
 * can be declared against the interface type.
 *
 * <p>ResourceFetcher.fetchResource is a static method, mocked per-test with Mockito's mockStatic
 * (requires Mockito 5+ / mockito-inline).
 */
@ExtendWith(MockitoExtension.class)
class BatimentServiceTest {

  @Mock private BatimentRepository batimentRepository;

  @Mock private BatimentMapper batimentMapper;

  private BatimentService batimentService;

  private UUID id;
  private Batiment entity;
  private BatimentDto dto;

  @BeforeEach
  void setUp() {
    batimentService = new BatimentServiceImpl(batimentRepository, batimentMapper);

    id = UUID.randomUUID();
    entity = Batiment.builder().id(id).nom("Batiment A").build();
    dto = new BatimentDto(id, "Batiment A", List.of());
  }

  // ---------- save ----------

  @Test
  void save_shouldMapPersistAndReturnDto() {
    BatimentDto inputDto = new BatimentDto(null, "Nouveau Batiment", List.of());
    Batiment mappedEntity = Batiment.builder().nom("Nouveau Batiment").build();
    Batiment savedEntity = Batiment.builder().id(id).nom("Nouveau Batiment").build();
    BatimentDto resultDto = new BatimentDto(id, "Nouveau Batiment", List.of());

    when(batimentMapper.toEntity(inputDto)).thenReturn(mappedEntity);
    when(batimentRepository.save(mappedEntity)).thenReturn(savedEntity);
    when(batimentMapper.toDto(savedEntity)).thenReturn(resultDto);

    BatimentDto result = batimentService.save(inputDto);

    assertThat(result).isEqualTo(resultDto);
    verify(batimentMapper).toEntity(inputDto);
    verify(batimentRepository).save(mappedEntity);
    verify(batimentMapper).toDto(savedEntity);
    verifyNoMoreInteractions(batimentRepository, batimentMapper);
  }

  // ---------- getById ----------

  @Test
  void getById_shouldFetchAndReturnDto() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, batimentRepository, "Batiment"))
          .thenReturn(entity);
      when(batimentMapper.toDto(entity)).thenReturn(dto);

      BatimentDto result = batimentService.getById(id);

      assertThat(result).isEqualTo(dto);
      fetcher.verify(() -> ResourceFetcher.fetchResource(id, batimentRepository, "Batiment"));
      verify(batimentMapper).toDto(entity);
      verifyNoMoreInteractions(batimentMapper);
    }
  }

  @Test
  void getById_shouldPropagateExceptionWhenNotFound() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      RuntimeException notFound = new RuntimeException("Batiment not found");
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, batimentRepository, "Batiment"))
          .thenThrow(notFound);

      assertThatThrownBy(() -> batimentService.getById(id)).isSameAs(notFound);

      verifyNoMoreInteractions(batimentMapper);
    }
  }

  // ---------- update ----------

  @Test
  void update_shouldFetchMutateSaveAndReturnDto() {
    Batiment savedEntity = Batiment.builder().id(id).nom("Batiment A - renamed").build();
    BatimentDto resultDto = new BatimentDto(id, "Batiment A - renamed", List.of());

    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, batimentRepository, "Batiment"))
          .thenReturn(entity);
      when(batimentRepository.save(entity)).thenReturn(savedEntity);
      when(batimentMapper.toDto(savedEntity)).thenReturn(resultDto);

      BatimentDto result = batimentService.update(id, dto);

      assertThat(result).isEqualTo(resultDto);
      fetcher.verify(() -> ResourceFetcher.fetchResource(id, batimentRepository, "Batiment"));
      verify(batimentMapper).updateEntityFromDto(dto, entity);
      verify(batimentRepository).save(entity);
      verify(batimentMapper).toDto(savedEntity);
      verifyNoMoreInteractions(batimentRepository, batimentMapper);
    }
  }

  @Test
  void update_shouldNotSaveWhenResourceNotFound() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      RuntimeException notFound = new RuntimeException("Batiment not found");
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, batimentRepository, "Batiment"))
          .thenThrow(notFound);

      assertThatThrownBy(() -> batimentService.update(id, dto)).isSameAs(notFound);

      verify(batimentRepository, never()).save(any());
      verifyNoMoreInteractions(batimentMapper);
    }
  }

  // ---------- delete ----------

  @Test
  void delete_shouldFetchAndDeleteEntity() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, batimentRepository, "Batiment"))
          .thenReturn(entity);

      batimentService.delete(id);

      fetcher.verify(() -> ResourceFetcher.fetchResource(id, batimentRepository, "Batiment"));
      verify(batimentRepository, times(1)).delete(entity);
      verifyNoMoreInteractions(batimentRepository);
    }
  }

  @Test
  void delete_shouldNotDeleteWhenResourceNotFound() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      RuntimeException notFound = new RuntimeException("Batiment not found");
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, batimentRepository, "Batiment"))
          .thenThrow(notFound);

      assertThatThrownBy(() -> batimentService.delete(id)).isSameAs(notFound);

      verify(batimentRepository, never()).delete(any());
    }
  }
}
