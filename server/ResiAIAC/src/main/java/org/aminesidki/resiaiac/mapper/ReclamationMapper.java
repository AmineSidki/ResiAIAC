package org.aminesidki.resiaiac.mapper;

import java.util.List;
import java.util.UUID;

import org.aminesidki.resiaiac.dto.PromotionDto;
import org.aminesidki.resiaiac.dto.ReclamationDto;
import org.aminesidki.resiaiac.entity.*;
import org.aminesidki.resiaiac.entity.id.EquipementReclamationId;
import org.aminesidki.resiaiac.repository.ChambreRepository;
import org.aminesidki.resiaiac.repository.EquipementReclamationRepository;
import org.aminesidki.resiaiac.repository.ServiceRepository;
import org.aminesidki.resiaiac.repository.UtilisateurRepository;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

/** Mapper for {@link org.aminesidki.resiaiac.entity.Reclamation } */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,componentModel = "spring")
public abstract class ReclamationMapper {

  @Autowired private ServiceRepository serviceRepo;
  @Autowired private EquipementReclamationRepository equipementReclamationRepo;
  @Autowired private UtilisateurRepository utilisateurRepo;
  @Autowired private ChambreRepository chambreRepo;

  // Map to DTO
  public abstract ReclamationDto toDto(Reclamation entity);

  // Map to Entity
  public abstract Reclamation toEntity(ReclamationDto dto);

  // Update entity with Dto
  public abstract void updateEntityFromDto(ReclamationDto dto, @MappingTarget Reclamation entity);

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

  protected Service mapIdToService(Long id) {
    if (id == null) {
      return null;
    }
    return serviceRepo.findById(id).orElse(null);
  }

  protected Long mapServiceToId(Service entity) {
    if (entity == null) {
      return null;
    }
    return entity.getId();
  }

  protected List<EquipementReclamation> mapIdToEquipementReclamations(
      List<EquipementReclamationId> ids) {
    if (ids == null) {
      return null;
    }
    return equipementReclamationRepo.findAllById(ids);
  }

  protected List<EquipementReclamationId> mapEquipementReclamationsToIds(
      List<EquipementReclamation> entities) {
    if (entities == null) {
      return null;
    }
    return entities.stream().map(e -> e.getId()).toList();
  }
}
