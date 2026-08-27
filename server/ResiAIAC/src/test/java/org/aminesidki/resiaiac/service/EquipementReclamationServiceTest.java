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
import org.aminesidki.resiaiac.dto.EquipementReclamationDto;
import org.aminesidki.resiaiac.entity.Equipement;
import org.aminesidki.resiaiac.entity.EquipementReclamation;
import org.aminesidki.resiaiac.entity.Reclamation;
import org.aminesidki.resiaiac.entity.id.EquipementReclamationId;
import org.aminesidki.resiaiac.mapper.EquipementReclamationMapper;
import org.aminesidki.resiaiac.repository.EquipementReclamationRepository;
import org.aminesidki.resiaiac.service.impl.EquipementReclamationServiceImpl;
import org.aminesidki.resiaiac.util.ResourceFetcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * Unit tests for {@link EquipementReclamationService}, exercised through its {@link
 * EquipementReclamationServiceImpl} implementation.
 *
 * <p>The service is wired manually (rather than via {@code @InjectMocks}) so the field under test
 * can be declared against the interface type.
 *
 * <p>EquipementReclamation uses a composite {@link EquipementReclamationId} key ({@code Long}
 * equipementId + {@code UUID} reclamationId), unlike the other services in this module. {@code
 * EquipementReclamationId} declares {@code @EqualsAndHashCode}, so a locally constructed instance
 * is equal to the one built internally by the service, letting Mockito's static-mock matching (and
 * {@code verify}) work by value.
 *
 * <p>ResourceFetcher.fetchResource is a static method, mocked per-test with Mockito's mockStatic
 * (requires Mockito 5+ / mockito-inline).
 *
 * <p>{@code EquipementService}/{@code ReclamationService} were added as constructor dependencies
 * alongside {@code getAllByEquipementId}/{@code getAllByReclamationId}, which resolve the parent
 * entity through those services before delegating to the repository.
 */
@ExtendWith(MockitoExtension.class)
class EquipementReclamationServiceTest {

  @Mock private EquipementService equipementService;

  @Mock private ReclamationService reclamationService;

  @Mock private EquipementReclamationRepository equipementReclamationRepository;

  @Mock private EquipementReclamationMapper equipementReclamationMapper;

  private EquipementReclamationService equipementReclamationService;

  private Long equipementId;
  private UUID reclamationId;
  private EquipementReclamationId id;
  private EquipementReclamation entity;
  private EquipementReclamationDto dto;

  @BeforeEach
  void setUp() {
    equipementReclamationService =
        new EquipementReclamationServiceImpl(
            equipementService,
            reclamationService,
            equipementReclamationRepository,
            equipementReclamationMapper);

    equipementId = 1L;
    reclamationId = UUID.randomUUID();
    id = new EquipementReclamationId(equipementId, reclamationId);
    entity = EquipementReclamation.builder().id(id).quantite(5L).build();
    dto = new EquipementReclamationDto(id, 5L, equipementId, reclamationId);
  }

  // ---------- getAllByEquipementId ----------

  @Test
  void getAllByEquipementId_shouldResolveEquipementThenFilterAndMapResults() {
    Equipement equipement = Equipement.builder().id(equipementId).build();
    Pageable pageable = PageRequest.of(0, 20);
    var page = new PageImpl<>(List.of(entity));

    when(equipementService.getEntityById(equipementId)).thenReturn(equipement);
    when(equipementReclamationRepository.findAllByEquipement(equipement, pageable))
        .thenReturn(page);
    when(equipementReclamationMapper.toDto(entity)).thenReturn(dto);

    var result = equipementReclamationService.getAllByEquipementId(equipementId, pageable);

    assertThat(result.getContent()).containsExactly(dto);
    verify(equipementService).getEntityById(equipementId);
    verify(equipementReclamationRepository).findAllByEquipement(equipement, pageable);
    verify(equipementReclamationMapper).toDto(entity);
  }

  // ---------- getAllByReclamationId ----------

  @Test
  void getAllByReclamationId_shouldResolveReclamationThenMapResults() {
    Reclamation reclamation = Reclamation.builder().id(reclamationId).build();

    when(reclamationService.getEntityById(reclamationId)).thenReturn(reclamation);
    when(equipementReclamationRepository.findAllByReclamation(reclamation))
        .thenReturn(List.of(entity));
    when(equipementReclamationMapper.toDto(entity)).thenReturn(dto);

    var result = equipementReclamationService.getAllByReclamationId(reclamationId);

    assertThat(result).containsExactly(dto);
    verify(reclamationService).getEntityById(reclamationId);
    verify(equipementReclamationRepository).findAllByReclamation(reclamation);
    verify(equipementReclamationMapper).toDto(entity);
  }

  // ---------- save ----------

  @Test
  void save_shouldMapPersistAndReturnDto() {
    EquipementReclamationDto inputDto =
        new EquipementReclamationDto(null, 10L, equipementId, reclamationId);
    EquipementReclamation mappedEntity = EquipementReclamation.builder().quantite(10L).build();
    EquipementReclamation savedEntity =
        EquipementReclamation.builder().id(id).quantite(10L).build();
    EquipementReclamationDto resultDto =
        new EquipementReclamationDto(id, 10L, equipementId, reclamationId);

    when(equipementReclamationMapper.toEntity(inputDto)).thenReturn(mappedEntity);
    when(equipementReclamationRepository.save(mappedEntity)).thenReturn(savedEntity);
    when(equipementReclamationMapper.toDto(savedEntity)).thenReturn(resultDto);

    EquipementReclamationDto result = equipementReclamationService.save(inputDto);

    assertThat(result).isEqualTo(resultDto);
    verify(equipementReclamationMapper).toEntity(inputDto);
    verify(equipementReclamationRepository).save(mappedEntity);
    verify(equipementReclamationMapper).toDto(savedEntity);
    verifyNoMoreInteractions(equipementReclamationRepository, equipementReclamationMapper);
  }

  // ---------- getById ----------

  @Test
  void getById_shouldFetchAndReturnDto() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(
              () ->
                  ResourceFetcher.fetchResource(
                      id, equipementReclamationRepository, "EquipementReclamation"))
          .thenReturn(entity);
      when(equipementReclamationMapper.toDto(entity)).thenReturn(dto);

      EquipementReclamationDto result =
          equipementReclamationService.getById(equipementId, reclamationId);

      assertThat(result).isEqualTo(dto);
      fetcher.verify(
          () ->
              ResourceFetcher.fetchResource(
                  id, equipementReclamationRepository, "EquipementReclamation"));
      verify(equipementReclamationMapper).toDto(entity);
      verifyNoMoreInteractions(equipementReclamationMapper);
    }
  }

  @Test
  void getById_shouldPropagateExceptionWhenNotFound() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      RuntimeException notFound = new RuntimeException("EquipementReclamation not found");
      fetcher
          .when(
              () ->
                  ResourceFetcher.fetchResource(
                      id, equipementReclamationRepository, "EquipementReclamation"))
          .thenThrow(notFound);

      assertThatThrownBy(() -> equipementReclamationService.getById(equipementId, reclamationId))
          .isSameAs(notFound);

      verifyNoMoreInteractions(equipementReclamationMapper);
    }
  }

  // ---------- update ----------

  @Test
  void update_shouldFetchMutateSaveAndReturnDto() {
    EquipementReclamation savedEntity = EquipementReclamation.builder().id(id).quantite(7L).build();
    EquipementReclamationDto resultDto =
        new EquipementReclamationDto(id, 7L, equipementId, reclamationId);

    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(
              () ->
                  ResourceFetcher.fetchResource(
                      id, equipementReclamationRepository, "EquipementReclamation"))
          .thenReturn(entity);
      when(equipementReclamationRepository.save(entity)).thenReturn(savedEntity);
      when(equipementReclamationMapper.toDto(savedEntity)).thenReturn(resultDto);

      EquipementReclamationDto result = equipementReclamationService.update(id, dto);

      assertThat(result).isEqualTo(resultDto);
      fetcher.verify(
          () ->
              ResourceFetcher.fetchResource(
                  id, equipementReclamationRepository, "EquipementReclamation"));
      verify(equipementReclamationMapper).updateEntityFromDto(dto, entity);
      verify(equipementReclamationRepository).save(entity);
      verify(equipementReclamationMapper).toDto(savedEntity);
      verifyNoMoreInteractions(equipementReclamationRepository, equipementReclamationMapper);
    }
  }

  @Test
  void update_shouldNotSaveWhenResourceNotFound() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      RuntimeException notFound = new RuntimeException("EquipementReclamation not found");
      fetcher
          .when(
              () ->
                  ResourceFetcher.fetchResource(
                      id, equipementReclamationRepository, "EquipementReclamation"))
          .thenThrow(notFound);

      assertThatThrownBy(() -> equipementReclamationService.update(id, dto)).isSameAs(notFound);

      verify(equipementReclamationRepository, never()).save(any());
      verifyNoMoreInteractions(equipementReclamationMapper);
    }
  }

  // ---------- delete ----------

  @Test
  void delete_shouldFetchAndDeleteEntity() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(
              () ->
                  ResourceFetcher.fetchResource(
                      id, equipementReclamationRepository, "EquipementReclamation"))
          .thenReturn(entity);

      equipementReclamationService.delete(equipementId, reclamationId);

      fetcher.verify(
          () ->
              ResourceFetcher.fetchResource(
                  id, equipementReclamationRepository, "EquipementReclamation"));
      verify(equipementReclamationRepository, times(1)).delete(entity);
      verifyNoMoreInteractions(equipementReclamationRepository);
    }
  }

  @Test
  void delete_shouldNotDeleteWhenResourceNotFound() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      RuntimeException notFound = new RuntimeException("EquipementReclamation not found");
      fetcher
          .when(
              () ->
                  ResourceFetcher.fetchResource(
                      id, equipementReclamationRepository, "EquipementReclamation"))
          .thenThrow(notFound);

      assertThatThrownBy(() -> equipementReclamationService.delete(equipementId, reclamationId))
          .isSameAs(notFound);

      verify(equipementReclamationRepository, never()).delete(any());
    }
  }
}
