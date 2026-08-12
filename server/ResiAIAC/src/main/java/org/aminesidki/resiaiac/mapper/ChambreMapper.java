package org.aminesidki.resiaiac.mapper;

import java.util.List;
import java.util.UUID;
import org.aminesidki.resiaiac.dto.ChambreDto;
import org.aminesidki.resiaiac.entity.*;
import org.aminesidki.resiaiac.entity.id.UtilisateurPromotionChambreId;
import org.aminesidki.resiaiac.repository.EtageRepository;
import org.aminesidki.resiaiac.repository.ReclamationRepository;
import org.aminesidki.resiaiac.repository.ReservationRepository;
import org.aminesidki.resiaiac.repository.UtilisateurPromotionChambreRepository;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

/** Mapper for {@link org.aminesidki.resiaiac.entity.Chambre } */
@Mapper(
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    componentModel = "spring")
public abstract class ChambreMapper {

  @Autowired private UtilisateurPromotionChambreRepository utilisateurPromotionChambreRepo;
  @Autowired private ReservationRepository reservationRepo;
  @Autowired private ReclamationRepository reclamationRepo;
  @Autowired private EtageRepository etageRepo;

  // Map to DTO
  public abstract ChambreDto toDto(Chambre entity);

  // Map to Entity
  public abstract Chambre toEntity(ChambreDto dto);

  // Update entity with Dto
  public abstract void updateEntityFromDto(ChambreDto dto, @MappingTarget Chambre entity);

  protected List<Reservation> mapIdToReservations(List<UUID> ids) {
    if (ids == null) {
      return null;
    }
    return reservationRepo.findAllById(ids);
  }

  protected List<UUID> mapReservationsToIds(List<Reservation> entities) {
    if (entities == null) {
      return null;
    }
    return entities.stream().map(e -> e.getId()).toList();
  }

  protected List<Reclamation> mapIdToReclamations(List<UUID> ids) {
    if (ids == null) {
      return null;
    }
    return reclamationRepo.findAllById(ids);
  }

  protected List<UUID> mapReclamationsToIds(List<Reclamation> entities) {
    if (entities == null) {
      return null;
    }
    return entities.stream().map(e -> e.getId()).toList();
  }

  protected List<UtilisateurPromotionChambre> mapIdToUtilisateurPromotionChambres(
      List<UtilisateurPromotionChambreId> ids) {
    if (ids == null) {
      return null;
    }
    return utilisateurPromotionChambreRepo.findAllById(ids);
  }

  protected List<UtilisateurPromotionChambreId> mapUtilisateurPromotionChambresToIds(
      List<UtilisateurPromotionChambre> entities) {
    if (entities == null) {
      return null;
    }
    return entities.stream().map(e -> e.getId()).toList();
  }

  protected Etage mapIdToEtage(UUID id) {
    if (id == null) {
      return null;
    }
    return etageRepo.findById(id).orElse(null);
  }

  protected UUID mapEtageToId(Etage entity) {
    if (entity == null) {
      return null;
    }
    return entity.getId();
  }
}
