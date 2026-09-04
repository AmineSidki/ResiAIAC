package org.aminesidki.resiaiac.service.impl;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.ReservationDto;
import org.aminesidki.resiaiac.dto.request.MyReservationRequest;
import org.aminesidki.resiaiac.entity.Chambre;
import org.aminesidki.resiaiac.entity.Reservation;
import org.aminesidki.resiaiac.entity.Utilisateur;
import org.aminesidki.resiaiac.enumeration.EtatChambre;
import org.aminesidki.resiaiac.enumeration.EtatReservation;
import org.aminesidki.resiaiac.exception.ResourceOwnershipMismatchException;
import org.aminesidki.resiaiac.exception.RoomFullException;
import org.aminesidki.resiaiac.exception.UserHasReservationException;
import org.aminesidki.resiaiac.mapper.ReservationMapper;
import org.aminesidki.resiaiac.repository.ReservationRepository;
import org.aminesidki.resiaiac.service.ChambreService;
import org.aminesidki.resiaiac.service.EmailTemplateService;
import org.aminesidki.resiaiac.service.ReservationService;
import org.aminesidki.resiaiac.service.UtilisateurService;
import org.aminesidki.resiaiac.util.ResourceFetcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class ReservationServiceImpl implements ReservationService {

  private final UtilisateurService utilisateurService;
  private final ChambreService chambreService;
  private final ReservationRepository reservationRepository;
  private final ReservationMapper reservationMapper;
  private final EmailTemplateService emailTemplateService;

  @Transactional(readOnly = true)
  @Override
  public Page<ReservationDto> getAllMy(Jwt jwt, Pageable pageable) {
    Utilisateur id = utilisateurService.getMyEntityByJwt(jwt);
    return reservationRepository.findAllByUtilisateur(id, pageable).map(reservationMapper::toDto);
  }

  @Transactional(readOnly = true)
  @Override
  public ReservationDto getMyById(Jwt jwt, UUID id) {
    Reservation entity = ResourceFetcher.fetchResource(id, reservationRepository, "Reservation");
    Utilisateur utilisateur = utilisateurService.getMyEntityByJwt(jwt);
    if (entity.getUtilisateur().getId().equals(utilisateur.getId())) {
      return reservationMapper.toDto(entity);
    }
    throw new ResourceOwnershipMismatchException(
        "Queried resource does not belong to querying user !");
  }

  @Override
  public ReservationDto saveMy(Jwt jwt, MyReservationRequest request) {
    // Fetch user info
    // Check if user made any reservations prior
    // Check if room is available (chambre.reservations.size())
    // Add reservation and change room status to AVAILABLE -> PARTIALLY_AVAILABLE ;
    // PARTIALLY_AVAILABLE -> OCCUPIED
    Utilisateur id = utilisateurService.getMyEntityByJwt(jwt);
    List<Reservation> userOpenReservations =
        reservationRepository.findAllByUtilisateurAndEtat(id, EtatReservation.ACTIVE);
    if (!userOpenReservations.isEmpty())
      throw new UserHasReservationException(
          "L'utilisateur "
              + id.getNom()
              + " "
              + id.getPrenom()
              + " possède dejà une reservation toujours ouverte !");
    Chambre chambre = chambreService.getEntityById(request.chambre());

    if (chambre.getEtat().equals(EtatChambre.OCCUPEE)) {
      throw new RoomFullException(
          "La chambre " + chambre.getMatricule() + " est totalement occupee !");
    } else {
      ReservationDto dto = reservationMapper.myReservationToDto(request);
      Reservation entity =
          Reservation.builder()
              .etat(EtatReservation.ACTIVE)
              .chambre(chambre)
              .utilisateur(id)
              .build();
      chambreService.updateEtatChambre(
          chambre.getId(),
          (chambre.getReservations().size() + 1) >= chambre.getCapacite()
              ? EtatChambre.OCCUPEE
              : EtatChambre.PARTIELLEMENT_LIBRE);
      reservationMapper.updateEntityFromDto(dto, entity);
      entity = reservationRepository.save(entity);

      emailTemplateService.envoyerReservationCreee(id, chambre);

      return reservationMapper.toDto(entity);
    }
  }

  @Transactional(readOnly = true)
  @Override
  public Page<ReservationDto> getAll(Pageable pageable) {
    return reservationRepository.findAll(pageable).map(reservationMapper::toDto);
  }

  @Override
  public ReservationDto save(ReservationDto dto) {
    Reservation entity = reservationMapper.toEntity(dto);
    Utilisateur id = utilisateurService.getMyEntityById(dto.utilisateur());
    entity.setEtat(EtatReservation.ACTIVE);
    entity = reservationRepository.save(entity);
    emailTemplateService.envoyerReservationCreee(id, entity.getChambre());
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
