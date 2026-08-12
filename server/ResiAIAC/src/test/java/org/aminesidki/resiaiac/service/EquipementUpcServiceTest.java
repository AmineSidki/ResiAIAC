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

import java.util.UUID;
import org.aminesidki.resiaiac.dto.EquipementUpcDto;
import org.aminesidki.resiaiac.entity.EquipementUpc;
import org.aminesidki.resiaiac.entity.id.EquipementUpcId;
import org.aminesidki.resiaiac.entity.id.UtilisateurPromotionChambreId;
import org.aminesidki.resiaiac.mapper.EquipementUpcMapper;
import org.aminesidki.resiaiac.repository.EquipementUpcRepository;
import org.aminesidki.resiaiac.service.impl.EquipementUpcServiceImpl;
import org.aminesidki.resiaiac.util.ResourceFetcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link EquipementUpcService}, exercised through its {@link
 * EquipementUpcServiceImpl} implementation.
 *
 * <p>The service is wired manually (rather than via {@code @InjectMocks}) so the field under test
 * can be declared against the interface type.
 *
 * <p>ResourceFetcher.fetchResource is a static method, mocked per-test with Mockito's mockStatic
 * (requires Mockito 5+ / mockito-inline).
 *
 * <p>getById/delete take the 4 raw values (equipementId + 3 UUIDs) and build the two-level
 * composite id internally; update still takes the composite id directly (matches the current
 * implementation).
 */
@ExtendWith(MockitoExtension.class)
class EquipementUpcServiceTest {

  @Mock private EquipementUpcRepository repository;

  @Mock private EquipementUpcMapper mapper;

  private EquipementUpcService service;

  private Long equipementId;
  private UUID utilisateurId;
  private UUID promotionId;
  private UUID chambreId;
  private EquipementUpcId id;
  private EquipementUpc entity;
  private EquipementUpcDto dto;

  @BeforeEach
  void setUp() {
    service = new EquipementUpcServiceImpl(repository, mapper);

    equipementId = 1L;
    utilisateurId = UUID.randomUUID();
    promotionId = UUID.randomUUID();
    chambreId = UUID.randomUUID();

    UtilisateurPromotionChambreId upcId =
        new UtilisateurPromotionChambreId(utilisateurId, promotionId, chambreId);
    id = new EquipementUpcId(equipementId, upcId);

    entity = EquipementUpc.builder().id(id).quantite(2L).build();
    dto = new EquipementUpcDto(id, 2L, equipementId, upcId);
  }

  // ---------- save ----------

  @Test
  void save_shouldMapPersistAndReturnDto() {
    EquipementUpcDto inputDto = dto;
    EquipementUpc mappedEntity = EquipementUpc.builder().id(id).quantite(2L).build();
    EquipementUpc savedEntity = EquipementUpc.builder().id(id).quantite(2L).build();
    EquipementUpcDto resultDto = dto;

    when(mapper.toEntity(inputDto)).thenReturn(mappedEntity);
    when(repository.save(mappedEntity)).thenReturn(savedEntity);
    when(mapper.toDto(savedEntity)).thenReturn(resultDto);

    EquipementUpcDto result = service.save(inputDto);

    assertThat(result).isEqualTo(resultDto);
    verify(mapper).toEntity(inputDto);
    verify(repository).save(mappedEntity);
    verify(mapper).toDto(savedEntity);
    verifyNoMoreInteractions(repository, mapper);
  }

  // ---------- getById ----------

  @Test
  void getById_shouldFetchAndReturnDto() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, repository, "EquipementUpc"))
          .thenReturn(entity);
      when(mapper.toDto(entity)).thenReturn(dto);

      EquipementUpcDto result =
          service.getById(equipementId, utilisateurId, promotionId, chambreId);

      assertThat(result).isEqualTo(dto);
      fetcher.verify(() -> ResourceFetcher.fetchResource(id, repository, "EquipementUpc"));
      verify(mapper).toDto(entity);
      verifyNoMoreInteractions(mapper);
    }
  }

  @Test
  void getById_shouldPropagateExceptionWhenNotFound() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      RuntimeException notFound = new RuntimeException("EquipementUpc not found");
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, repository, "EquipementUpc"))
          .thenThrow(notFound);

      assertThatThrownBy(() -> service.getById(equipementId, utilisateurId, promotionId, chambreId))
          .isSameAs(notFound);

      verifyNoMoreInteractions(mapper);
    }
  }

  // ---------- update ----------

  @Test
  void update_shouldFetchMutateSaveAndReturnDto() {
    EquipementUpc savedEntity = EquipementUpc.builder().id(id).quantite(5L).build();
    EquipementUpcDto resultDto = new EquipementUpcDto(id, 5L, equipementId, dto.upc());

    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, repository, "EquipementUpc"))
          .thenReturn(entity);
      when(repository.save(entity)).thenReturn(savedEntity);
      when(mapper.toDto(savedEntity)).thenReturn(resultDto);

      EquipementUpcDto result = service.update(id, dto);

      assertThat(result).isEqualTo(resultDto);
      fetcher.verify(() -> ResourceFetcher.fetchResource(id, repository, "EquipementUpc"));
      verify(mapper).updateEntityFromDto(dto, entity);
      verify(repository).save(entity);
      verify(mapper).toDto(savedEntity);
      verifyNoMoreInteractions(repository, mapper);
    }
  }

  @Test
  void update_shouldNotSaveWhenResourceNotFound() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      RuntimeException notFound = new RuntimeException("EquipementUpc not found");
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, repository, "EquipementUpc"))
          .thenThrow(notFound);

      assertThatThrownBy(() -> service.update(id, dto)).isSameAs(notFound);

      verify(repository, never()).save(any());
      verifyNoMoreInteractions(mapper);
    }
  }

  // ---------- delete ----------

  @Test
  void delete_shouldFetchAndDeleteEntity() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, repository, "EquipementUpc"))
          .thenReturn(entity);

      service.delete(equipementId, utilisateurId, promotionId, chambreId);

      fetcher.verify(() -> ResourceFetcher.fetchResource(id, repository, "EquipementUpc"));
      verify(repository, times(1)).delete(entity);
      verifyNoMoreInteractions(repository);
    }
  }

  @Test
  void delete_shouldNotDeleteWhenResourceNotFound() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      RuntimeException notFound = new RuntimeException("EquipementUpc not found");
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, repository, "EquipementUpc"))
          .thenThrow(notFound);

      assertThatThrownBy(() -> service.delete(equipementId, utilisateurId, promotionId, chambreId))
          .isSameAs(notFound);

      verify(repository, never()).delete(any());
    }
  }
}
