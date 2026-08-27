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
import java.util.Optional;
import java.util.UUID;
import org.aminesidki.resiaiac.dto.UtilisateurPromotionChambreDto;
import org.aminesidki.resiaiac.entity.Chambre;
import org.aminesidki.resiaiac.entity.Utilisateur;
import org.aminesidki.resiaiac.entity.UtilisateurPromotionChambre;
import org.aminesidki.resiaiac.entity.id.UtilisateurPromotionChambreId;
import org.aminesidki.resiaiac.exception.ResourceNotFoundException;
import org.aminesidki.resiaiac.mapper.UtilisateurPromotionChambreMapper;
import org.aminesidki.resiaiac.repository.UtilisateurPromotionChambreRepository;
import org.aminesidki.resiaiac.service.impl.UtilisateurPromotionChambreServiceImpl;
import org.aminesidki.resiaiac.util.ResourceFetcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link UtilisateurPromotionChambreService}, exercised through its {@link
 * UtilisateurPromotionChambreServiceImpl} implementation.
 *
 * <p>The service is wired manually (rather than via {@code @InjectMocks}) so the field under test
 * can be declared against the interface type.
 *
 * <p>ResourceFetcher.fetchResource is a static method, mocked per-test with Mockito's mockStatic
 * (requires Mockito 5+ / mockito-inline).
 *
 * <p>getById/delete take the 3 raw UUIDs and build the composite id internally; update still takes
 * the composite id directly (matches the current implementation).
 *
 * <p>{@code UtilisateurService}/{@code ChambreService} were added as constructor dependencies
 * alongside {@code getAllByUserId}/{@code getAllByChambreId}, which resolve the parent entity
 * through those services before delegating to the repository.
 */
@ExtendWith(MockitoExtension.class)
class UtilisateurPromotionChambreServiceTest {

  @Mock private UtilisateurService utilisateurService;

  @Mock private ChambreService chambreService;

  @Mock private UtilisateurPromotionChambreRepository repository;

  @Mock private UtilisateurPromotionChambreMapper mapper;

  private UtilisateurPromotionChambreService service;

  private UUID utilisateurId;
  private UUID promotionId;
  private UUID chambreId;
  private UtilisateurPromotionChambreId id;
  private UtilisateurPromotionChambre entity;
  private UtilisateurPromotionChambreDto dto;

  @BeforeEach
  void setUp() {
    service =
        new UtilisateurPromotionChambreServiceImpl(
            utilisateurService, chambreService, repository, mapper);

    utilisateurId = UUID.randomUUID();
    promotionId = UUID.randomUUID();
    chambreId = UUID.randomUUID();
    id = new UtilisateurPromotionChambreId(utilisateurId, promotionId, chambreId);

    entity = UtilisateurPromotionChambre.builder().id(id).retard(false).note("RAS").build();
    dto =
        new UtilisateurPromotionChambreDto(
            id, false, "RAS", utilisateurId, promotionId, chambreId, List.of());
  }

  // ---------- getAllByChambreId ----------

  @Test
  void getAllByChambreId_shouldResolveChambreThenMapResults() {
    Chambre chambre = Chambre.builder().id(chambreId).build();

    when(chambreService.getEntityById(chambreId)).thenReturn(chambre);
    when(repository.findAllByChambre(chambre)).thenReturn(List.of(entity));
    when(mapper.toDto(entity)).thenReturn(dto);

    var result = service.getAllByChambreId(chambreId);

    assertThat(result).containsExactly(dto);
    verify(chambreService).getEntityById(chambreId);
    verify(repository).findAllByChambre(chambre);
    verify(mapper).toDto(entity);
  }

  // ---------- getAllByUserId ----------

  @Test
  void getAllByUserId_shouldResolveUserThenMapResults() {
    Utilisateur utilisateur = Utilisateur.builder().id(utilisateurId).build();

    when(utilisateurService.getMyEntityById(utilisateurId)).thenReturn(utilisateur);
    when(repository.findAllByUtilisateur(utilisateur)).thenReturn(List.of(entity));
    when(mapper.toDto(entity)).thenReturn(dto);

    var result = service.getAllByUserId(utilisateurId);

    assertThat(result).containsExactly(dto);
    verify(utilisateurService).getMyEntityById(utilisateurId);
    verify(repository).findAllByUtilisateur(utilisateur);
    verify(mapper).toDto(entity);
  }

  // ---------- getEntityById ----------

  @Test
  void getEntityById_shouldFetchEntity() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, repository, "UtilisateurPromotionChambre"))
          .thenReturn(entity);

      UtilisateurPromotionChambre result = service.getEntityById(id);

      assertThat(result).isEqualTo(entity);
      fetcher.verify(
          () -> ResourceFetcher.fetchResource(id, repository, "UtilisateurPromotionChambre"));
    }
  }

  // ---------- getCurrentRoomByUser ----------

  @Test
  void getCurrentRoomByUser_shouldReturnChambreOfMostRecentUpc() {
    Utilisateur utilisateur = Utilisateur.builder().id(utilisateurId).build();
    Chambre chambre = Chambre.builder().id(chambreId).build();
    UtilisateurPromotionChambre mostRecent =
        UtilisateurPromotionChambre.builder().id(id).chambre(chambre).build();

    when(repository.findTopByUtilisateurOrderByPromotion_AnneeDeFinDesc(utilisateur))
        .thenReturn(Optional.of(mostRecent));

    Chambre result = service.getCurrentRoomByUser(utilisateur);

    assertThat(result).isEqualTo(chambre);
    verify(repository).findTopByUtilisateurOrderByPromotion_AnneeDeFinDesc(utilisateur);
  }

  @Test
  void getCurrentRoomByUser_shouldThrowResourceNotFoundWhenUserHasNoUpc() {
    Utilisateur utilisateur = Utilisateur.builder().id(utilisateurId).build();

    when(repository.findTopByUtilisateurOrderByPromotion_AnneeDeFinDesc(utilisateur))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.getCurrentRoomByUser(utilisateur))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  // ---------- save ----------

  @Test
  void save_shouldMapPersistAndReturnDto() {
    UtilisateurPromotionChambreDto inputDto =
        new UtilisateurPromotionChambreDto(
            id, true, "En retard", utilisateurId, promotionId, chambreId, List.of());
    UtilisateurPromotionChambre mappedEntity =
        UtilisateurPromotionChambre.builder().id(id).retard(true).note("En retard").build();
    UtilisateurPromotionChambre savedEntity =
        UtilisateurPromotionChambre.builder().id(id).retard(true).note("En retard").build();
    UtilisateurPromotionChambreDto resultDto =
        new UtilisateurPromotionChambreDto(
            id, true, "En retard", utilisateurId, promotionId, chambreId, List.of());

    when(mapper.toEntity(inputDto)).thenReturn(mappedEntity);
    when(repository.save(mappedEntity)).thenReturn(savedEntity);
    when(mapper.toDto(savedEntity)).thenReturn(resultDto);

    UtilisateurPromotionChambreDto result = service.save(inputDto);

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
          .when(() -> ResourceFetcher.fetchResource(id, repository, "UtilisateurPromotionChambre"))
          .thenReturn(entity);
      when(mapper.toDto(entity)).thenReturn(dto);

      UtilisateurPromotionChambreDto result =
          service.getById(utilisateurId, promotionId, chambreId);

      assertThat(result).isEqualTo(dto);
      fetcher.verify(
          () -> ResourceFetcher.fetchResource(id, repository, "UtilisateurPromotionChambre"));
      verify(mapper).toDto(entity);
      verifyNoMoreInteractions(mapper);
    }
  }

  @Test
  void getById_shouldPropagateExceptionWhenNotFound() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      RuntimeException notFound = new RuntimeException("UtilisateurPromotionChambre not found");
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, repository, "UtilisateurPromotionChambre"))
          .thenThrow(notFound);

      assertThatThrownBy(() -> service.getById(utilisateurId, promotionId, chambreId))
          .isSameAs(notFound);

      verifyNoMoreInteractions(mapper);
    }
  }

  // ---------- update ----------

  @Test
  void update_shouldFetchMutateSaveAndReturnDto() {
    UtilisateurPromotionChambre savedEntity =
        UtilisateurPromotionChambre.builder().id(id).retard(true).note("Mis a jour").build();
    UtilisateurPromotionChambreDto resultDto =
        new UtilisateurPromotionChambreDto(
            id, true, "Mis a jour", utilisateurId, promotionId, chambreId, List.of());

    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, repository, "UtilisateurPromotionChambre"))
          .thenReturn(entity);
      when(repository.save(entity)).thenReturn(savedEntity);
      when(mapper.toDto(savedEntity)).thenReturn(resultDto);

      UtilisateurPromotionChambreDto result = service.update(id, dto);

      assertThat(result).isEqualTo(resultDto);
      fetcher.verify(
          () -> ResourceFetcher.fetchResource(id, repository, "UtilisateurPromotionChambre"));
      verify(mapper).updateEntityFromDto(dto, entity);
      verify(repository).save(entity);
      verify(mapper).toDto(savedEntity);
      verifyNoMoreInteractions(repository, mapper);
    }
  }

  @Test
  void update_shouldNotSaveWhenResourceNotFound() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      RuntimeException notFound = new RuntimeException("UtilisateurPromotionChambre not found");
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, repository, "UtilisateurPromotionChambre"))
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
          .when(() -> ResourceFetcher.fetchResource(id, repository, "UtilisateurPromotionChambre"))
          .thenReturn(entity);

      service.delete(utilisateurId, promotionId, chambreId);

      fetcher.verify(
          () -> ResourceFetcher.fetchResource(id, repository, "UtilisateurPromotionChambre"));
      verify(repository, times(1)).delete(entity);
      verifyNoMoreInteractions(repository);
    }
  }

  @Test
  void delete_shouldNotDeleteWhenResourceNotFound() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      RuntimeException notFound = new RuntimeException("UtilisateurPromotionChambre not found");
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, repository, "UtilisateurPromotionChambre"))
          .thenThrow(notFound);

      assertThatThrownBy(() -> service.delete(utilisateurId, promotionId, chambreId))
          .isSameAs(notFound);

      verify(repository, never()).delete(any());
    }
  }
}
