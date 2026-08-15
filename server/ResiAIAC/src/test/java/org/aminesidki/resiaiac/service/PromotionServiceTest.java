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
import org.aminesidki.resiaiac.dto.PromotionDto;
import org.aminesidki.resiaiac.entity.Promotion;
import org.aminesidki.resiaiac.mapper.PromotionMapper;
import org.aminesidki.resiaiac.repository.PromotionRepository;
import org.aminesidki.resiaiac.service.impl.PromotionServiceImpl;
import org.aminesidki.resiaiac.util.ResourceFetcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link PromotionService}, exercised through its {@link PromotionServiceImpl}
 * implementation.
 *
 * <p>The service is wired manually (rather than via {@code @InjectMocks}) so the field under test
 * can be declared against the interface type.
 *
 * <p>ResourceFetcher.fetchResource is a static method, mocked per-test with Mockito's mockStatic
 * (requires Mockito 5+ / mockito-inline).
 */
@ExtendWith(MockitoExtension.class)
class PromotionServiceTest {

  @Mock private PromotionRepository promotionRepository;

  @Mock private PromotionMapper promotionMapper;

  private PromotionService promotionService;

  private UUID id;
  private Promotion entity;
  private PromotionDto dto;

  @BeforeEach
  void setUp() {
    promotionService = new PromotionServiceImpl(promotionRepository, promotionMapper);

    id = UUID.randomUUID();
    entity = Promotion.builder().id(id).anneeDeDepart(2024L).anneeDeFin(2027L).niveau(1).build();
    dto = new PromotionDto(id, 2024L, 2027L, 1, null, List.of());
  }

  // ---------- save ----------

  @Test
  void save_shouldMapPersistAndReturnDto() {
    PromotionDto inputDto = new PromotionDto(null, 2025L, 2028L, 1, null, List.of());
    Promotion mappedEntity =
        Promotion.builder().anneeDeDepart(2025L).anneeDeFin(2028L).niveau(1).build();
    Promotion savedEntity =
        Promotion.builder().id(id).anneeDeDepart(2025L).anneeDeFin(2028L).niveau(1).build();
    PromotionDto resultDto = new PromotionDto(id, 2025L, 2028L, 1, null, List.of());

    when(promotionMapper.toEntity(inputDto)).thenReturn(mappedEntity);
    when(promotionRepository.save(mappedEntity)).thenReturn(savedEntity);
    when(promotionMapper.toDto(savedEntity)).thenReturn(resultDto);

    PromotionDto result = promotionService.save(inputDto);

    assertThat(result).isEqualTo(resultDto);
    verify(promotionMapper).toEntity(inputDto);
    verify(promotionRepository).save(mappedEntity);
    verify(promotionMapper).toDto(savedEntity);
    verifyNoMoreInteractions(promotionRepository, promotionMapper);
  }

  // ---------- getById ----------

  @Test
  void getById_shouldFetchAndReturnDto() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, promotionRepository, "Promotion"))
          .thenReturn(entity);
      when(promotionMapper.toDto(entity)).thenReturn(dto);

      PromotionDto result = promotionService.getById(id);

      assertThat(result).isEqualTo(dto);
      fetcher.verify(() -> ResourceFetcher.fetchResource(id, promotionRepository, "Promotion"));
      verify(promotionMapper).toDto(entity);
      verifyNoMoreInteractions(promotionMapper);
    }
  }

  @Test
  void getById_shouldPropagateExceptionWhenNotFound() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      RuntimeException notFound = new RuntimeException("Promotion not found");
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, promotionRepository, "Promotion"))
          .thenThrow(notFound);

      assertThatThrownBy(() -> promotionService.getById(id)).isSameAs(notFound);

      verifyNoMoreInteractions(promotionMapper);
    }
  }

  // ---------- update ----------

  @Test
  void update_shouldFetchMutateSaveAndReturnDto() {
    Promotion savedEntity =
        Promotion.builder().id(id).anneeDeDepart(2024L).anneeDeFin(2027L).niveau(2).build();
    PromotionDto resultDto = new PromotionDto(id, 2024L, 2027L, 2, null, List.of());

    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, promotionRepository, "Promotion"))
          .thenReturn(entity);
      when(promotionRepository.save(entity)).thenReturn(savedEntity);
      when(promotionMapper.toDto(savedEntity)).thenReturn(resultDto);

      PromotionDto result = promotionService.update(id, dto);

      assertThat(result).isEqualTo(resultDto);
      fetcher.verify(() -> ResourceFetcher.fetchResource(id, promotionRepository, "Promotion"));
      verify(promotionMapper).updateEntityFromDto(dto, entity);
      verify(promotionRepository).save(entity);
      verify(promotionMapper).toDto(savedEntity);
      verifyNoMoreInteractions(promotionRepository, promotionMapper);
    }
  }

  @Test
  void update_shouldNotSaveWhenResourceNotFound() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      RuntimeException notFound = new RuntimeException("Promotion not found");
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, promotionRepository, "Promotion"))
          .thenThrow(notFound);

      assertThatThrownBy(() -> promotionService.update(id, dto)).isSameAs(notFound);

      verify(promotionRepository, never()).save(any());
      verifyNoMoreInteractions(promotionMapper);
    }
  }

  // ---------- delete ----------

  @Test
  void delete_shouldFetchAndDeleteEntity() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, promotionRepository, "Promotion"))
          .thenReturn(entity);

      promotionService.delete(id);

      fetcher.verify(() -> ResourceFetcher.fetchResource(id, promotionRepository, "Promotion"));
      verify(promotionRepository, times(1)).delete(entity);
      verifyNoMoreInteractions(promotionRepository);
    }
  }

  @Test
  void delete_shouldNotDeleteWhenResourceNotFound() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      RuntimeException notFound = new RuntimeException("Promotion not found");
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, promotionRepository, "Promotion"))
          .thenThrow(notFound);

      assertThatThrownBy(() -> promotionService.delete(id)).isSameAs(notFound);

      verify(promotionRepository, never()).delete(any());
    }
  }
}
