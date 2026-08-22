package org.aminesidki.resiaiac.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import org.aminesidki.resiaiac.dto.EquipementReclamationDto;
import org.aminesidki.resiaiac.dto.ReclamationDto;
import org.aminesidki.resiaiac.dto.entry.EquipementEntry;
import org.aminesidki.resiaiac.dto.request.MyReclamationRequest;
import org.aminesidki.resiaiac.entity.Chambre;
import org.aminesidki.resiaiac.entity.Reclamation;
import org.aminesidki.resiaiac.entity.Utilisateur;
import org.aminesidki.resiaiac.enumeration.EtatReclamation;
import org.aminesidki.resiaiac.exception.ResourceOwnershipMismatchException;
import org.aminesidki.resiaiac.mapper.ReclamationMapper;
import org.aminesidki.resiaiac.repository.ReclamationRepository;
import org.aminesidki.resiaiac.service.impl.ReclamationServiceImpl;
import org.aminesidki.resiaiac.util.ResourceFetcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;

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

  // ---------- getMyById ----------

  @Test
  void getMyById_shouldReturnDtoWhenCallerOwnsTheResource() {
    Jwt jwt = mock(Jwt.class);
    Utilisateur me = Utilisateur.builder().id(UUID.randomUUID()).build();
    Reclamation owned = Reclamation.builder().id(id).message("Fuite d'eau").utilisateur(me).build();

    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, reclamationRepository, "Reclamation"))
          .thenReturn(owned);
      when(utilisateurService.getMyEntity(jwt)).thenReturn(me);
      when(reclamationMapper.toDto(owned)).thenReturn(dto);

      ReclamationDto result = reclamationService.getMyById(jwt, id);

      assertThat(result).isEqualTo(dto);
      fetcher.verify(() -> ResourceFetcher.fetchResource(id, reclamationRepository, "Reclamation"));
      verify(utilisateurService).getMyEntity(jwt);
      verify(reclamationMapper).toDto(owned);
    }
  }

  @Test
  void getMyById_shouldThrowOwnershipMismatchWhenCallerDoesNotOwnTheResource() {
    Jwt jwt = mock(Jwt.class);
    Utilisateur me = Utilisateur.builder().id(UUID.randomUUID()).build();
    Utilisateur someoneElse = Utilisateur.builder().id(UUID.randomUUID()).build();
    Reclamation notOwned =
        Reclamation.builder().id(id).message("Fuite d'eau").utilisateur(someoneElse).build();

    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, reclamationRepository, "Reclamation"))
          .thenReturn(notOwned);
      when(utilisateurService.getMyEntity(jwt)).thenReturn(me);

      assertThatThrownBy(() -> reclamationService.getMyById(jwt, id))
          .isInstanceOf(ResourceOwnershipMismatchException.class);

      verifyNoMoreInteractions(reclamationMapper);
    }
  }

  @Test
  void getMyById_shouldPropagateExceptionWhenNotFound() {
    Jwt jwt = mock(Jwt.class);

    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      RuntimeException notFound = new RuntimeException("Reclamation not found");
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, reclamationRepository, "Reclamation"))
          .thenThrow(notFound);

      assertThatThrownBy(() -> reclamationService.getMyById(jwt, id)).isSameAs(notFound);

      verifyNoMoreInteractions(reclamationMapper, utilisateurService);
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

  // ---------- getAllMy ----------

  @Test
  void getAllMy_shouldResolveUserFromJwtAndReturnTheirReclamations() {
    Jwt jwt = mock(Jwt.class);
    Utilisateur me = Utilisateur.builder().id(UUID.randomUUID()).build();
    Pageable pageable = PageRequest.of(0, 20);
    var page = new PageImpl<>(List.of(entity));

    when(utilisateurService.getMyEntity(jwt)).thenReturn(me);
    when(reclamationRepository.findAllByUtilisateur(me, pageable)).thenReturn(page);
    when(reclamationMapper.toDto(entity)).thenReturn(dto);

    var result = reclamationService.getAllMy(jwt, pageable);

    assertThat(result.getContent()).containsExactly(dto);
    verify(utilisateurService).getMyEntity(jwt);
    verify(reclamationRepository).findAllByUtilisateur(me, pageable);
    verifyNoMoreInteractions(reclamationRepository, utilisateurService);
  }

  // ---------- saveMy ----------

  @Test
  void saveMy_shouldResolveUserAndCurrentRoomThenPersistAndForwardEquipements() {
    Jwt jwt = mock(Jwt.class);
    Utilisateur me = Utilisateur.builder().id(UUID.randomUUID()).build();
    Chambre currentRoom = Chambre.builder().id(UUID.randomUUID()).build();
    List<EquipementEntry> equipements =
        List.of(new EquipementEntry(1L, 2L), new EquipementEntry(2L, 1L));
    MyReclamationRequest request = new MyReclamationRequest("Fuite d'eau", 1L, equipements);
    ReclamationDto mappedDto =
        new ReclamationDto(null, "Fuite d'eau", null, null, null, 1L, null, null, null);
    Reclamation mappedEntity = Reclamation.builder().message("Fuite d'eau").build();
    Reclamation savedEntity = Reclamation.builder().id(id).message("Fuite d'eau").build();

    when(utilisateurService.getMyEntity(jwt)).thenReturn(me);
    when(utilisateurPromotionChambreService.getCurrentRoomByUser(me)).thenReturn(currentRoom);
    when(reclamationMapper.myReclamationToDto(request)).thenReturn(mappedDto);
    when(reclamationMapper.toEntity(mappedDto)).thenReturn(mappedEntity);
    when(reclamationRepository.save(mappedEntity)).thenReturn(savedEntity);
    when(reclamationMapper.toDto(savedEntity)).thenReturn(dto);

    ReclamationDto result = reclamationService.saveMy(jwt, request);

    assertThat(result).isEqualTo(dto);
    // The entity handed to the repository must be forced into EN_ATTENTE, and stamped with
    // the caller's own user/chambre — never whatever (if anything) came in on the DTO.
    assertThat(mappedEntity.getEtat()).isEqualTo(EtatReclamation.EN_ATTENTE);
    assertThat(mappedEntity.getUtilisateur()).isEqualTo(me);
    assertThat(mappedEntity.getChambre()).isEqualTo(currentRoom);

    verify(utilisateurService).getMyEntity(jwt);
    verify(utilisateurPromotionChambreService).getCurrentRoomByUser(me);

    ArgumentCaptor<EquipementReclamationDto> captor =
        ArgumentCaptor.forClass(EquipementReclamationDto.class);
    verify(equipementReclamationService, times(2)).save(captor.capture());
    assertThat(captor.getAllValues())
        .extracting(EquipementReclamationDto::equipement, EquipementReclamationDto::quantite)
        .containsExactly(tuple(1L, 2L), tuple(2L, 1L));
    assertThat(captor.getAllValues()).allSatisfy(e -> assertThat(e.reclamation()).isEqualTo(id));
  }

  @Test
  void saveMy_shouldPropagateExceptionWhenUserHasNoCurrentRoom() {
    Jwt jwt = mock(Jwt.class);
    Utilisateur me = Utilisateur.builder().id(UUID.randomUUID()).build();
    MyReclamationRequest request = new MyReclamationRequest("Fuite d'eau", 1L, List.of());
    RuntimeException notFound = new RuntimeException("No current room for user");

    when(utilisateurService.getMyEntity(jwt)).thenReturn(me);
    when(utilisateurPromotionChambreService.getCurrentRoomByUser(me)).thenThrow(notFound);

    assertThatThrownBy(() -> reclamationService.saveMy(jwt, request)).isSameAs(notFound);

    verify(reclamationRepository, never()).save(any());
    verifyNoMoreInteractions(reclamationMapper, equipementReclamationService);
  }
}
