package org.aminesidki.resiaiac.mapper;

import java.util.UUID;
import org.aminesidki.resiaiac.dto.ReservationDto;
import org.aminesidki.resiaiac.entity.Chambre;
import org.aminesidki.resiaiac.entity.Reservation;
import org.aminesidki.resiaiac.entity.Utilisateur;
import org.aminesidki.resiaiac.repository.ChambreRepository;
import org.aminesidki.resiaiac.repository.UtilisateurRepository;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

/** Mapper for {@link org.aminesidki.resiaiac.entity.Reservation } */
@Mapper(
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    componentModel = "spring")
public abstract class ReservationMapper {

  @Autowired private UtilisateurRepository utilisateurRepo;
  @Autowired private ChambreRepository chambreRepo;

  // Map to DTO
  public abstract ReservationDto toDto(Reservation entity);

  // Map to Entity
  public abstract Reservation toEntity(ReservationDto dto);

  // Update entity with Dto
  public abstract void updateEntityFromDto(ReservationDto dto, @MappingTarget Reservation entity);

  protected Utilisateur mapIdToUtilisateur(UUID id) {
    if (id == null) {
      return null;
    }
    return utilisateurRepo.findById(id).orElse(null);
  }

  protected UUID mapUtilisateurToId(Utilisateur entity) {
    if (entity == null) {
      return null;
    }
    return entity.getId();
  }

  protected Chambre mapIdToChambre(UUID id) {
    if (id == null) {
      return null;
    }
    return chambreRepo.findById(id).orElse(null);
  }

  protected UUID mapChambreToId(Chambre entity) {
    if (entity == null) {
      return null;
    }
    return entity.getId();
  }
}
