package org.aminesidki.resiaiac.mapper;

import java.util.UUID;
import org.aminesidki.resiaiac.dto.EquipementReclamationDto;
import org.aminesidki.resiaiac.entity.Equipement;
import org.aminesidki.resiaiac.entity.EquipementReclamation;
import org.aminesidki.resiaiac.entity.Reclamation;
import org.aminesidki.resiaiac.repository.EquipementRepository;
import org.aminesidki.resiaiac.repository.ReclamationRepository;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

/** Mapper for {@link org.aminesidki.resiaiac.entity.EquipementReclamation } */
@Mapper(
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    componentModel = "spring")
public abstract class EquipementReclamationMapper {

  @Autowired private ReclamationRepository reclamationRepo;
  @Autowired private EquipementRepository equipementRepo;

  // Map to DTO
  public abstract EquipementReclamationDto toDto(EquipementReclamation entity);

  // Map to Entity
  public abstract EquipementReclamation toEntity(EquipementReclamationDto dto);

  // Update entity with Dto
  public abstract void updateEntityFromDto(
      EquipementReclamationDto dto, @MappingTarget EquipementReclamation entity);

  protected Equipement mapIdToEquipement(Long id) {
    if (id == null) {
      return null;
    }
    return equipementRepo.findById(id).orElse(null);
  }

  protected Long mapEquipementToId(Equipement entity) {
    if (entity == null) {
      return null;
    }
    return entity.getId();
  }

  protected Reclamation mapIdToReclamation(UUID id) {
    if (id == null) {
      return null;
    }
    return reclamationRepo.findById(id).orElse(null);
  }

  protected UUID mapReclamationToId(Reclamation entity) {
    if (entity == null) {
      return null;
    }
    return entity.getId();
  }
}
