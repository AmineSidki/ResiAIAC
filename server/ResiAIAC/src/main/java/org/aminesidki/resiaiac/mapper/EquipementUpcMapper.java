package org.aminesidki.resiaiac.mapper;

import org.aminesidki.resiaiac.dto.EquipementUpcDto;
import org.aminesidki.resiaiac.entity.Equipement;
import org.aminesidki.resiaiac.entity.EquipementUpc;
import org.aminesidki.resiaiac.entity.UtilisateurPromotionChambre;
import org.aminesidki.resiaiac.entity.id.UtilisateurPromotionChambreId;
import org.aminesidki.resiaiac.repository.EquipementRepository;
import org.aminesidki.resiaiac.repository.UtilisateurPromotionChambreRepository;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

/** Mapper for {@link org.aminesidki.resiaiac.entity.EquipementUpc } */
@Mapper(componentModel = "spring")
public abstract class EquipementUpcMapper {

  @Autowired private UtilisateurPromotionChambreRepository utilisateurPromotionChambreRepo;
  @Autowired private EquipementRepository equipementRepo;

  // Map to DTO
  public abstract EquipementUpcDto toDto(EquipementUpc entity);

  // Map to Entity
  public abstract EquipementUpc toEntity(EquipementUpcDto dto);

  public Equipement mapIdToEquipement(Long id) {
    if (id == null) {
      return null;
    }
    return equipementRepo.findById(id).orElse(null);
  }

  public Long mapEquipementToId(Equipement entity) {
    if (entity == null) {
      return null;
    }
    return entity.getId();
  }

  public UtilisateurPromotionChambre mapIdToUtilisateurPromotionChambre(
      UtilisateurPromotionChambreId id) {
    if (id == null) {
      return null;
    }
    return utilisateurPromotionChambreRepo.findById(id).orElse(null);
  }

  public UtilisateurPromotionChambreId mapUtilisateurPromotionChambreToId(
      UtilisateurPromotionChambre entity) {
    if (entity == null) {
      return null;
    }
    return entity.getId();
  }
}