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
import org.aminesidki.resiaiac.dto.ReclamationDto;
import org.aminesidki.resiaiac.entity.Reclamation;
import org.aminesidki.resiaiac.mapper.ReclamationMapper;
import org.aminesidki.resiaiac.repository.ReclamationRepository;
import org.aminesidki.resiaiac.service.impl.ReclamationServiceImpl;
import org.aminesidki.resiaiac.util.ResourceFetcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link ReclamationService}, exercised through its {@link ReclamationServiceImpl}
 * implementation.
 *
 * <p>The service is wired manually (rather than via {@code @InjectMocks}) so the field under test
 * can be declared against the interface type.
 *
 * <p>ResourceFetcher.fetchResource is a static method, mocked per-test with Mockito's mockStatic
 * (requires Mockito 5+ / mockito-inline).
 */
@ExtendWith(MockitoExtension.class)
class ReclamationServiceTest {

  @Mock private ReclamationRepository reclamationRepository;

  @Mock private ReclamationMapper reclamationMapper;

  @Mock private UtilisateurService utilisateurService;

  @Mock private UtilisateurPromotionChambreService utilisateurPromotionChambreService;

  @Mock private EquipementReclamationService equipementReclamationService;

  private ReclamationService reclamationService;

  private UUID id;
  private Reclamation entity;
  private ReclamationDto dto;

  @BeforeEach
  void setUp() {
    reclamationService =
        new ReclamationServiceImpl(
            utilisateurService,
            utilisateurPromotionChambreService,
            equipementReclamationService,
            reclamationRepository,
            reclamationMapper);

    id = UUID.randomUUID();
    entity = Reclamation.builder().id(id).message("Fuite d'eau").etat(null).build();
    dto = new ReclamationDto(id, "Fuite d'eau", null, null, null, null, List.of(), null, null);
  }

  // ---------- save ----------

  @Test
  void save_shouldMapPersistAndReturnDto() {
    ReclamationDto inputDto =
        new ReclamationDto(null, "Panne electrique", null, null, null, null, List.of(), null, null);
    Reclamation mappedEntity = Reclamation.builder().message("Panne electrique").etat(null).build();
    Reclamation savedEntity =
        Reclamation.builder().id(id).message("Panne electrique").etat(null).build();
    ReclamationDto resultDto =
        new ReclamationDto(id, "Panne electrique", null, null, null, null, List.of(), null, null);

    when(reclamationMapper.toEntity(inputDto)).thenReturn(mappedEntity);
    when(reclamationRepository.save(mappedEntity)).thenReturn(savedEntity);
    when(reclamationMapper.toDto(savedEntity)).thenReturn(resultDto);

    ReclamationDto result = reclamationService.save(inputDto);

    assertThat(result).isEqualTo(resultDto);
    verify(reclamationMapper).toEntity(inputDto);
    verify(reclamationRepository).save(mappedEntity);
    verify(reclamationMapper).toDto(savedEntity);
    verifyNoMoreInteractions(reclamationRepository, reclamationMapper);
  }

  // ---------- getById ----------

  @Test
  void getById_shouldFetchAndReturnDto() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, reclamationRepository, "Reclamation"))
          .thenReturn(entity);
      when(reclamationMapper.toDto(entity)).thenReturn(dto);

      ReclamationDto result = reclamationService.getById(id);

      assertThat(result).isEqualTo(dto);
      fetcher.verify(() -> ResourceFetcher.fetchResource(id, reclamationRepository, "Reclamation"));
      verify(reclamationMapper).toDto(entity);
      verifyNoMoreInteractions(reclamationMapper);
    }
  }

  @Test
  void getById_shouldPropagateExceptionWhenNotFound() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      RuntimeException notFound = new RuntimeException("Reclamation not found");
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, reclamationRepository, "Reclamation"))
          .thenThrow(notFound);

      assertThatThrownBy(() -> reclamationService.getById(id)).isSameAs(notFound);

      verifyNoMoreInteractions(reclamationMapper);
    }
  }

  // ---------- update ----------

  @Test
  void update_shouldFetchMutateSaveAndReturnDto() {
    Reclamation savedEntity =
        Reclamation.builder().id(id).message("Fuite d'eau - resolue").etat(null).build();
    ReclamationDto resultDto =
        new ReclamationDto(
            id, "Fuite d'eau - resolue", null, null, null, null, List.of(), null, null);

    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, reclamationRepository, "Reclamation"))
          .thenReturn(entity);
      when(reclamationRepository.save(entity)).thenReturn(savedEntity);
      when(reclamationMapper.toDto(savedEntity)).thenReturn(resultDto);

      ReclamationDto result = reclamationService.update(id, dto);

      assertThat(result).isEqualTo(resultDto);
      fetcher.verify(() -> ResourceFetcher.fetchResource(id, reclamationRepository, "Reclamation"));
      verify(reclamationMapper).updateEntityFromDto(dto, entity);
      verify(reclamationRepository).save(entity);
      verify(reclamationMapper).toDto(savedEntity);
      verifyNoMoreInteractions(reclamationRepository, reclamationMapper);
    }
  }

  @Test
  void update_shouldNotSaveWhenResourceNotFound() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      RuntimeException notFound = new RuntimeException("Reclamation not found");
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, reclamationRepository, "Reclamation"))
          .thenThrow(notFound);

      assertThatThrownBy(() -> reclamationService.update(id, dto)).isSameAs(notFound);

      verify(reclamationRepository, never()).save(any());
      verifyNoMoreInteractions(reclamationMapper);
    }
  }

  // ---------- delete ----------

  @Test
  void delete_shouldFetchAndDeleteEntity() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, reclamationRepository, "Reclamation"))
          .thenReturn(entity);

      reclamationService.delete(id);

      fetcher.verify(() -> ResourceFetcher.fetchResource(id, reclamationRepository, "Reclamation"));
      verify(reclamationRepository, times(1)).delete(entity);
      verifyNoMoreInteractions(reclamationRepository);
    }
  }

  @Test
  void delete_shouldNotDeleteWhenResourceNotFound() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      RuntimeException notFound = new RuntimeException("Reclamation not found");
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, reclamationRepository, "Reclamation"))
          .thenThrow(notFound);

      assertThatThrownBy(() -> reclamationService.delete(id)).isSameAs(notFound);

      verify(reclamationRepository, never()).delete(any());
    }
  }
}
