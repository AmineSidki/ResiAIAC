package org.aminesidki.resiaiac.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import org.aminesidki.resiaiac.dto.ReservationDto;
import org.aminesidki.resiaiac.dto.request.MyReservationRequest;
import org.aminesidki.resiaiac.dto.response.EmailResponse;
import org.aminesidki.resiaiac.entity.Chambre;
import org.aminesidki.resiaiac.entity.Reservation;
import org.aminesidki.resiaiac.entity.Utilisateur;
import org.aminesidki.resiaiac.enumeration.EtatChambre;
import org.aminesidki.resiaiac.enumeration.EtatReservation;
import org.aminesidki.resiaiac.exception.ResourceOwnershipMismatchException;
import org.aminesidki.resiaiac.exception.RoomFullException;
import org.aminesidki.resiaiac.mapper.ReservationMapper;
import org.aminesidki.resiaiac.repository.ReservationRepository;
import org.aminesidki.resiaiac.service.impl.ReservationServiceImpl;
import org.aminesidki.resiaiac.util.ResourceFetcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Unit tests for {@link ReservationService}, exercised through its {@link ReservationServiceImpl}
 * implementation.
 *
 * <p>{@code etat} (an {@code EtatReservation} enum) is left {@code null} in test fixtures
 * throughout — the service never branches on it, and the mapper is mocked, so the concrete enum
 * constants aren't needed here.
 *
 * <p>ResourceFetcher.fetchResource is a static method, mocked per-test with Mockito's mockStatic
 * (requires Mockito 5+ / mockito-inline).
 *
 * <p>{@link EmailService} is mocked; {@code save()} triggers a confirmation email as a side-effect,
 * verified separately from the persistence flow.
 */
@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

  @Mock private ReservationRepository reservationRepository;

  @Mock private ReservationMapper reservationMapper;

  @Mock private UtilisateurService utilisateurService;

  @Mock private ChambreService chambreService;

  @Mock private EmailService emailService;

  private ReservationService reservationService;

  private UUID id;
  private Reservation entity;
  private ReservationDto dto;

  @BeforeEach
  void setUp() {
    reservationService =
        new ReservationServiceImpl(
            utilisateurService,
            chambreService,
            reservationRepository,
            reservationMapper,
            emailService);

    id = UUID.randomUUID();
    entity = Reservation.builder().id(id).build();
    dto = new ReservationDto(id, null, UUID.randomUUID(), UUID.randomUUID(), null, null);
  }

  // ---------- save ----------

  @Test
  void save_shouldMapPersistAndReturnDto() {
    ReservationDto inputDto =
        new ReservationDto(null, null, UUID.randomUUID(), UUID.randomUUID(), null, null);
    Reservation mappedEntity = Reservation.builder().build();
    Reservation savedEntity = Reservation.builder().id(id).build();
    ReservationDto resultDto =
        new ReservationDto(id, null, UUID.randomUUID(), UUID.randomUUID(), null, null);

    when(reservationMapper.toEntity(inputDto)).thenReturn(mappedEntity);
    when(reservationRepository.save(mappedEntity)).thenReturn(savedEntity);
    when(reservationMapper.toDto(savedEntity)).thenReturn(resultDto);

    ReservationDto result = reservationService.save(inputDto);

    assertThat(result).isEqualTo(resultDto);
    verify(reservationMapper).toEntity(inputDto);
    verify(reservationRepository).save(mappedEntity);
    verify(reservationMapper).toDto(savedEntity);
    verify(emailService).envoyerEmail(any(EmailResponse.class));
    verifyNoMoreInteractions(reservationRepository, reservationMapper, emailService);
  }

  // ---------- getById ----------

  @Test
  void getById_shouldFetchAndReturnDto() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, reservationRepository, "Reservation"))
          .thenReturn(entity);
      when(reservationMapper.toDto(entity)).thenReturn(dto);

      ReservationDto result = reservationService.getById(id);

      assertThat(result).isEqualTo(dto);
      fetcher.verify(() -> ResourceFetcher.fetchResource(id, reservationRepository, "Reservation"));
      verify(reservationMapper).toDto(entity);
      verifyNoMoreInteractions(reservationMapper, emailService);
    }
  }

  @Test
  void getById_shouldPropagateExceptionWhenNotFound() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      RuntimeException notFound = new RuntimeException("Reservation not found");
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, reservationRepository, "Reservation"))
          .thenThrow(notFound);

      assertThatThrownBy(() -> reservationService.getById(id)).isSameAs(notFound);

      verifyNoMoreInteractions(reservationMapper, emailService);
    }
  }

  // ---------- getMyById ----------

  @Test
  void getMyById_shouldReturnDtoWhenCallerOwnsTheResource() {
    Jwt jwt = mock(Jwt.class);
    Utilisateur me = Utilisateur.builder().id(UUID.randomUUID()).build();
    Reservation owned = Reservation.builder().id(id).utilisateur(me).build();

    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, reservationRepository, "Reservation"))
          .thenReturn(owned);
      when(utilisateurService.getMyEntity(jwt)).thenReturn(me);
      when(reservationMapper.toDto(owned)).thenReturn(dto);

      ReservationDto result = reservationService.getMyById(jwt, id);

      assertThat(result).isEqualTo(dto);
      fetcher.verify(() -> ResourceFetcher.fetchResource(id, reservationRepository, "Reservation"));
      verify(utilisateurService).getMyEntity(jwt);
      verify(reservationMapper).toDto(owned);
    }
  }

  @Test
  void getMyById_shouldThrowOwnershipMismatchWhenCallerDoesNotOwnTheResource() {
    Jwt jwt = mock(Jwt.class);
    Utilisateur me = Utilisateur.builder().id(UUID.randomUUID()).build();
    Utilisateur someoneElse = Utilisateur.builder().id(UUID.randomUUID()).build();
    Reservation notOwned = Reservation.builder().id(id).utilisateur(someoneElse).build();

    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, reservationRepository, "Reservation"))
          .thenReturn(notOwned);
      when(utilisateurService.getMyEntity(jwt)).thenReturn(me);

      assertThatThrownBy(() -> reservationService.getMyById(jwt, id))
          .isInstanceOf(ResourceOwnershipMismatchException.class);

      verifyNoMoreInteractions(reservationMapper, emailService);
    }
  }

  @Test
  void getMyById_shouldPropagateExceptionWhenNotFound() {
    Jwt jwt = mock(Jwt.class);

    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      RuntimeException notFound = new RuntimeException("Reservation not found");
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, reservationRepository, "Reservation"))
          .thenThrow(notFound);

      assertThatThrownBy(() -> reservationService.getMyById(jwt, id)).isSameAs(notFound);

      verifyNoMoreInteractions(reservationMapper, utilisateurService, emailService);
    }
  }

  // ---------- update ----------

  @Test
  void update_shouldFetchMutateSaveAndReturnDto() {
    Reservation savedEntity = Reservation.builder().id(id).build();
    ReservationDto resultDto =
        new ReservationDto(id, null, UUID.randomUUID(), UUID.randomUUID(), null, null);

    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, reservationRepository, "Reservation"))
          .thenReturn(entity);
      when(reservationRepository.save(entity)).thenReturn(savedEntity);
      when(reservationMapper.toDto(savedEntity)).thenReturn(resultDto);

      ReservationDto result = reservationService.update(id, dto);

      assertThat(result).isEqualTo(resultDto);
      fetcher.verify(() -> ResourceFetcher.fetchResource(id, reservationRepository, "Reservation"));
      verify(reservationMapper).updateEntityFromDto(dto, entity);
      verify(reservationRepository).save(entity);
      verify(reservationMapper).toDto(savedEntity);
      verifyNoMoreInteractions(reservationRepository, reservationMapper, emailService);
    }
  }

  @Test
  void update_shouldNotSaveWhenResourceNotFound() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      RuntimeException notFound = new RuntimeException("Reservation not found");
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, reservationRepository, "Reservation"))
          .thenThrow(notFound);

      assertThatThrownBy(() -> reservationService.update(id, dto)).isSameAs(notFound);

      verify(reservationRepository, never()).save(any());
      verifyNoMoreInteractions(reservationMapper, emailService);
    }
  }

  // ---------- delete ----------

  @Test
  void delete_shouldFetchAndDeleteEntity() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, reservationRepository, "Reservation"))
          .thenReturn(entity);

      reservationService.delete(id);

      fetcher.verify(() -> ResourceFetcher.fetchResource(id, reservationRepository, "Reservation"));
      verify(reservationRepository, times(1)).delete(entity);
      verifyNoMoreInteractions(reservationRepository, emailService);
    }
  }

  @Test
  void delete_shouldNotDeleteWhenResourceNotFound() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      RuntimeException notFound = new RuntimeException("Reservation not found");
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, reservationRepository, "Reservation"))
          .thenThrow(notFound);

      assertThatThrownBy(() -> reservationService.delete(id)).isSameAs(notFound);

      verify(reservationRepository, never()).delete(any());
      verifyNoMoreInteractions(emailService);
    }
  }

  // ---------- getAllMy ----------

  @Test
  void getAllMy_shouldResolveUserFromJwtAndReturnTheirReservations() {
    Jwt jwt = mock(Jwt.class);
    Utilisateur me = Utilisateur.builder().id(UUID.randomUUID()).build();
    Pageable pageable = PageRequest.of(0, 20);
    var page = new PageImpl<>(List.of(entity));

    when(utilisateurService.getMyEntity(jwt)).thenReturn(me);
    when(reservationRepository.findAllByUtilisateur(me, pageable)).thenReturn(page);
    when(reservationMapper.toDto(entity)).thenReturn(dto);

    var result = reservationService.getAllMy(jwt, pageable);

    assertThat(result.getContent()).containsExactly(dto);
    verify(utilisateurService).getMyEntity(jwt);
    verify(reservationRepository).findAllByUtilisateur(me, pageable);
  }

  // ---------- saveMy ----------

  @Test
  void saveMy_shouldRejectWhenRoomIsAlreadyFull() {
    Jwt jwt = mock(Jwt.class);
    Utilisateur me = Utilisateur.builder().id(UUID.randomUUID()).build();
    UUID chambreId = UUID.randomUUID();
    Chambre fullRoom =
        Chambre.builder().id(chambreId).matricule("B1-101").etat(EtatChambre.OCCUPEE).build();
    MyReservationRequest request = new MyReservationRequest(chambreId);

    when(utilisateurService.getMyEntity(jwt)).thenReturn(me);
    when(chambreService.getEntityById(chambreId)).thenReturn(fullRoom);

    assertThatThrownBy(() -> reservationService.saveMy(jwt, request))
        .isInstanceOf(RoomFullException.class);

    verify(reservationRepository, never()).save(any());
    verify(chambreService, never()).updateEtatChambre(any(), any());
  }

  /**
   * Covers the état transition for the room's *first* reservation, across capacities — including
   * the capacite == 1 edge case, where the room must go straight to OCCUPEE instead of
   * PARTIELLEMENT_LIBRE.
   */
  @ParameterizedTest(name = "capacite={0} -> {1}")
  @CsvSource({"1,OCCUPEE", "2,PARTIELLEMENT_LIBRE", "4,PARTIELLEMENT_LIBRE"})
  void saveMy_shouldDeriveRoomEtatFromCapaciteOnFirstReservation(
      long capacite, EtatChambre expectedEtat) {
    Jwt jwt = mock(Jwt.class);
    Utilisateur me = Utilisateur.builder().id(UUID.randomUUID()).build();
    UUID chambreId = UUID.randomUUID();
    Chambre chambre =
        Chambre.builder()
            .id(chambreId)
            .matricule("B1-101")
            .etat(EtatChambre.LIBRE)
            .capacite(capacite)
            .reservations(List.of())
            .build();
    MyReservationRequest request = new MyReservationRequest(chambreId);
    ReservationDto mappedDto = new ReservationDto(null, null, null, null, null, null);

    when(utilisateurService.getMyEntity(jwt)).thenReturn(me);
    when(chambreService.getEntityById(chambreId)).thenReturn(chambre);
    when(reservationMapper.myReservationToDto(request)).thenReturn(mappedDto);
    when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));
    when(reservationMapper.toDto(any(Reservation.class))).thenReturn(dto);

    reservationService.saveMy(jwt, request);

    verify(chambreService).updateEtatChambre(chambreId, expectedEtat);
  }

  @Test
  void saveMy_shouldTransitionToOccupeeWhenLastAvailableSpotIsTaken() {
    Jwt jwt = mock(Jwt.class);
    Utilisateur me = Utilisateur.builder().id(UUID.randomUUID()).build();
    UUID chambreId = UUID.randomUUID();
    // capacite 2, already 1 active reservation -> this one fills it up
    Chambre chambre =
        Chambre.builder()
            .id(chambreId)
            .matricule("B1-101")
            .etat(EtatChambre.PARTIELLEMENT_LIBRE)
            .capacite(2L)
            .reservations(List.of(Reservation.builder().id(UUID.randomUUID()).build()))
            .build();
    MyReservationRequest request = new MyReservationRequest(chambreId);
    ReservationDto mappedDto = new ReservationDto(null, null, null, null, null, null);

    when(utilisateurService.getMyEntity(jwt)).thenReturn(me);
    when(chambreService.getEntityById(chambreId)).thenReturn(chambre);
    when(reservationMapper.myReservationToDto(request)).thenReturn(mappedDto);
    when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));
    when(reservationMapper.toDto(any(Reservation.class))).thenReturn(dto);

    reservationService.saveMy(jwt, request);

    verify(chambreService).updateEtatChambre(chambreId, EtatChambre.OCCUPEE);
  }

  @Test
  void saveMy_shouldPersistWithActiveEtatOwnerAndRoomFromJwt() {
    Jwt jwt = mock(Jwt.class);
    Utilisateur me = Utilisateur.builder().id(UUID.randomUUID()).build();
    UUID chambreId = UUID.randomUUID();
    Chambre chambre =
        Chambre.builder()
            .id(chambreId)
            .matricule("B1-101")
            .etat(EtatChambre.LIBRE)
            .capacite(4L)
            .reservations(List.of())
            .build();
    MyReservationRequest request = new MyReservationRequest(chambreId);
    ReservationDto mappedDto = new ReservationDto(null, null, null, null, null, null);

    when(utilisateurService.getMyEntity(jwt)).thenReturn(me);
    when(chambreService.getEntityById(chambreId)).thenReturn(chambre);
    when(reservationMapper.myReservationToDto(request)).thenReturn(mappedDto);
    when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));
    when(reservationMapper.toDto(any(Reservation.class))).thenReturn(dto);

    ReservationDto result = reservationService.saveMy(jwt, request);

    assertThat(result).isEqualTo(dto);
    verify(reservationRepository)
        .save(
            argThat(
                r ->
                    r.getEtat() == EtatReservation.ACTIVE
                        && r.getUtilisateur() == me
                        && r.getChambre() == chambre));
    verifyNoMoreInteractions(emailService);
  }
}
