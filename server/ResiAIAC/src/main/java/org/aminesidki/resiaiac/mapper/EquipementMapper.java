package org.aminesidki.resiaiac.mapper;

import java.util.List;

import org.aminesidki.resiaiac.dto.ChambreDto;
import org.aminesidki.resiaiac.dto.EquipementDto;
import org.aminesidki.resiaiac.entity.Chambre;
import org.aminesidki.resiaiac.entity.Equipement;
import org.aminesidki.resiaiac.entity.EquipementReclamation;
import org.aminesidki.resiaiac.entity.EquipementUpc;
import org.aminesidki.resiaiac.entity.id.EquipementReclamationId;
import org.aminesidki.resiaiac.entity.id.EquipementUpcId;
import org.aminesidki.resiaiac.repository.EquipementReclamationRepository;
import org.aminesidki.resiaiac.repository.EquipementUpcRepository;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

/** Mapper for {@link org.aminesidki.resiaiac.entity.Equipement } */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,componentModel = "spring")
public abstract class EquipementMapper {

  @Autowired private EquipementUpcRepository equipementUpcRepo;
  @Autowired private EquipementReclamationRepository equipementReclamationRepo;

  // Map to DTO
  public abstract EquipementDto toDto(Equipement entity);

  // Map to Entity
  public abstract Equipement toEntity(EquipementDto dto);

  // Update entity with Dto
  public abstract void updateEntityFromDto(EquipementDto dto, @MappingTarget Equipement entity);

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

  protected List<EquipementUpc> mapIdToEquipementUpcs(List<EquipementUpcId> ids) {
    if (ids == null) {
      return null;
    }
    return equipementUpcRepo.findAllById(ids);
  }

  protected List<EquipementUpcId> mapEquipementUpcsToIds(List<EquipementUpc> entities) {
    if (entities == null) {
      return null;
    }
    return entities.stream().map(e -> e.getId()).toList();
  }
}
