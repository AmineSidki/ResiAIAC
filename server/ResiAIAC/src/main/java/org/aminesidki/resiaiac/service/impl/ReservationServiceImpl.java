package org.aminesidki.resiaiac.service.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.ReservationDto;
import org.aminesidki.resiaiac.entity.Reservation;
import org.aminesidki.resiaiac.mapper.ReservationMapper;
import org.aminesidki.resiaiac.repository.ReservationRepository;
import org.aminesidki.resiaiac.service.EmailService;
import org.aminesidki.resiaiac.service.ReservationService;
import org.aminesidki.resiaiac.util.ResourceFetcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class ReservationServiceImpl implements ReservationService {

  private final ReservationRepository reservationRepository;
  private final ReservationMapper reservationMapper;
  private final EmailService emailService;



  @Override
  public Page<ReservationDto> getAll(Pageable pageable) {
    return reservationRepository.findAll(pageable).map(reservationMapper::toDto);
  }

  @Override
  public ReservationDto save(ReservationDto dto) {
    Reservation entity = reservationMapper.toEntity(dto);
    entity = reservationRepository.save(entity);
      emailService.envoyerEmail(
              "etudiant@test.com", // Plus tard on récupérera l'email de l'étudiant
              "Confirmation de Réservation",
              "Votre réservation a été créée avec succès !"
      );
    return reservationMapper.toDto(entity);
  }

  @Transactional(readOnly = true)
  @Override
  public ReservationDto getById(UUID id) {
    Reservation entity = ResourceFetcher.fetchResource(id, reservationRepository, "Reservation");
    return reservationMapper.toDto(entity);
  }

  @Override
  public ReservationDto update(UUID id, ReservationDto dto) {
    Reservation entity = ResourceFetcher.fetchResource(id, reservationRepository, "Reservation");
    reservationMapper.updateEntityFromDto(dto, entity);
    entity = reservationRepository.save(entity);
    return reservationMapper.toDto(entity);
  }

  @Override
  public void delete(UUID id) {
    reservationRepository.delete(
        ResourceFetcher.fetchResource(id, reservationRepository, "Reservation"));
  }
}
