package org.aminesidki.resiaiac.mapper;

import java.util.List;
import java.util.UUID;
import org.aminesidki.resiaiac.dto.UtilisateurDto;
import org.aminesidki.resiaiac.entity.*;
import org.aminesidki.resiaiac.entity.id.UtilisateurPromotionChambreId;
import org.aminesidki.resiaiac.repository.DocumentRepository;
import org.aminesidki.resiaiac.repository.ReclamationRepository;
import org.aminesidki.resiaiac.repository.ReservationRepository;
import org.aminesidki.resiaiac.repository.UtilisateurPromotionChambreRepository;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

/** Mapper for {@link org.aminesidki.resiaiac.entity.Utilisateur } */
@Mapper(
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    componentModel = "spring")
public abstract class UtilisateurMapper {

  @Autowired private UtilisateurPromotionChambreRepository utilisateurPromotionChambreRepo;
  @Autowired private ReservationRepository reservationRepo;
  @Autowired private ReclamationRepository reclamationRepo;
  @Autowired private DocumentRepository documentRepo;

  // Map to DTO
  public abstract UtilisateurDto toDto(Utilisateur entity);

  // Map to Entity
  public abstract Utilisateur toEntity(UtilisateurDto dto);

  // Update entity with Dto
  public abstract void updateEntityFromDto(UtilisateurDto dto, @MappingTarget Utilisateur entity);

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

  protected List<Document> mapIdToDocuments(List<UUID> ids) {
    if (ids == null) {
      return null;
    }
    return documentRepo.findAllById(ids);
  }

  protected List<UUID> mapDocumentsToIds(List<Document> entities) {
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
}
