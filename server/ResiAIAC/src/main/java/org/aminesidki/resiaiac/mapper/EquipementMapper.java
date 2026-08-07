package org.aminesidki.resiaiac.mapper;

import java.util.List;
import org.aminesidki.resiaiac.dto.EquipementDto;
import org.aminesidki.resiaiac.entity.Equipement;
import org.aminesidki.resiaiac.entity.EquipementReclamation;
import org.aminesidki.resiaiac.entity.EquipementUpc;
import org.aminesidki.resiaiac.entity.id.EquipementReclamationId;
import org.aminesidki.resiaiac.entity.id.EquipementUpcId;
import org.aminesidki.resiaiac.repository.EquipementReclamationRepository;
import org.aminesidki.resiaiac.repository.EquipementUpcRepository;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

/** Mapper for {@link org.aminesidki.resiaiac.entity.Equipement } */
@Mapper(componentModel = "spring")
public abstract class EquipementMapper {

  @Autowired private EquipementUpcRepository equipementUpcRepo;
  @Autowired private EquipementReclamationRepository equipementReclamationRepo;

  // Map to DTO
  public abstract EquipementDto toDto(Equipement entity);

  // Map to Entity
  public abstract Equipement toEntity(EquipementDto dto);

  public List<EquipementReclamation> mapIdToEquipementReclamations(
      List<EquipementReclamationId> ids) {
    if (ids == null) {
      return null;
    }
    return equipementReclamationRepo.findAllById(ids);
  }

  public List<EquipementReclamationId> mapEquipementReclamationsToIds(
      List<EquipementReclamation> entities) {
    if (entities == null) {
      return null;
    }
    return entities.stream().map(e -> e.getId()).toList();
  }

  public List<EquipementUpc> mapIdToEquipementUpcs(List<EquipementUpcId> ids) {
    if (ids == null) {
      return null;
    }
    return equipementUpcRepo.findAllById(ids);
  }

  public List<EquipementUpcId> mapEquipementUpcsToIds(List<EquipementUpc> entities) {
    if (entities == null) {
      return null;
    }
    return entities.stream().map(e -> e.getId()).toList();
  }
}
