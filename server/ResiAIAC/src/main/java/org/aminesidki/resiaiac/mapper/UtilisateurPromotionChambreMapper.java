package org.aminesidki.resiaiac.mapper;

import java.util.List;
import java.util.UUID;

import org.aminesidki.resiaiac.dto.UtilisateurDto;
import org.aminesidki.resiaiac.dto.UtilisateurPromotionChambreDto;
import org.aminesidki.resiaiac.entity.Chambre;
import org.aminesidki.resiaiac.entity.EquipementUpc;
import org.aminesidki.resiaiac.entity.Promotion;
import org.aminesidki.resiaiac.entity.Utilisateur;
import org.aminesidki.resiaiac.entity.UtilisateurPromotionChambre;
import org.aminesidki.resiaiac.entity.id.EquipementUpcId;
import org.aminesidki.resiaiac.repository.ChambreRepository;
import org.aminesidki.resiaiac.repository.EquipementUpcRepository;
import org.aminesidki.resiaiac.repository.PromotionRepository;
import org.aminesidki.resiaiac.repository.UtilisateurRepository;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

/** Mapper for {@link org.aminesidki.resiaiac.entity.UtilisateurPromotionChambre } */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,componentModel = "spring")
public abstract class UtilisateurPromotionChambreMapper {

  @Autowired private EquipementUpcRepository equipementUpcRepo;
  @Autowired private PromotionRepository promotionRepo;
  @Autowired private UtilisateurRepository utilisateurRepo;
  @Autowired private ChambreRepository chambreRepo;

  // Map to DTO
  public abstract UtilisateurPromotionChambreDto toDto(UtilisateurPromotionChambre entity);

  // Map to Entity
  public abstract UtilisateurPromotionChambre toEntity(UtilisateurPromotionChambreDto dto);

  // Update entity with Dto
  public abstract void updateEntityFromDto(UtilisateurPromotionChambreDto dto, @MappingTarget UtilisateurPromotionChambre entity);

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

  protected Promotion mapIdToPromotion(UUID id) {
    if (id == null) {
      return null;
    }
    return promotionRepo.findById(id).orElse(null);
  }

  protected UUID mapPromotionToId(Promotion entity) {
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
