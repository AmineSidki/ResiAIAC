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
import org.aminesidki.resiaiac.dto.ReservationDto;
import org.aminesidki.resiaiac.entity.Reservation;
import org.aminesidki.resiaiac.mapper.ReservationMapper;
import org.aminesidki.resiaiac.repository.ReservationRepository;
import org.aminesidki.resiaiac.service.impl.ReservationServiceImpl;
import org.aminesidki.resiaiac.util.ResourceFetcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

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
 */
@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

  @Mock private ReservationRepository reservationRepository;

  @Mock private ReservationMapper reservationMapper;

  private ReservationService reservationService;

  private UUID id;
  private Reservation entity;
  private ReservationDto dto;

  @BeforeEach
  void setUp() {
    reservationService = new ReservationServiceImpl(reservationRepository, reservationMapper);

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
    verifyNoMoreInteractions(reservationRepository, reservationMapper);
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
      verifyNoMoreInteractions(reservationMapper);
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

      verifyNoMoreInteractions(reservationMapper);
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
      verifyNoMoreInteractions(reservationRepository, reservationMapper);
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
      verifyNoMoreInteractions(reservationMapper);
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
      verifyNoMoreInteractions(reservationRepository);
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
    }
  }
}
