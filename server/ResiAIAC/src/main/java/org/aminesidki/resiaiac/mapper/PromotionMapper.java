package org.aminesidki.resiaiac.mapper;

import java.util.List;
import org.aminesidki.resiaiac.dto.PromotionDto;
import org.aminesidki.resiaiac.entity.Filiere;
import org.aminesidki.resiaiac.entity.Promotion;
import org.aminesidki.resiaiac.entity.UtilisateurPromotionChambre;
import org.aminesidki.resiaiac.entity.id.UtilisateurPromotionChambreId;
import org.aminesidki.resiaiac.repository.FiliereRepository;
import org.aminesidki.resiaiac.repository.UtilisateurPromotionChambreRepository;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

/** Mapper for {@link org.aminesidki.resiaiac.entity.Promotion } */
@Mapper(componentModel = "spring")
public abstract class PromotionMapper {

  @Autowired private FiliereRepository filiereRepo;
  @Autowired private UtilisateurPromotionChambreRepository utilisateurPromotionChambreRepo;

  // Map to DTO
  public abstract PromotionDto toDto(Promotion entity);

  // Map to Entity
  public abstract Promotion toEntity(PromotionDto dto);

  public Filiere mapIdToFiliere(Long id) {
    if (id == null) {
      return null;
    }
    return filiereRepo.findById(id).orElse(null);
  }

  public Long mapFiliereToId(Filiere entity) {
    if (entity == null) {
      return null;
    }
    return entity.getId();
  }

  public List<UtilisateurPromotionChambre> mapIdToUtilisateurPromotionChambres(
      List<UtilisateurPromotionChambreId> ids) {
    if (ids == null) {
      return null;
    }
    return utilisateurPromotionChambreRepo.findAllById(ids);
  }

  public List<UtilisateurPromotionChambreId> mapUtilisateurPromotionChambresToIds(
      List<UtilisateurPromotionChambre> entities) {
    if (entities == null) {
      return null;
    }
    return entities.stream().map(e -> e.getId()).toList();
  }
}