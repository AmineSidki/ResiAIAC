package org.aminesidki.resiaiac.mapper;

import java.util.List;
import java.util.UUID;
import org.aminesidki.resiaiac.dto.FiliereDto;
import org.aminesidki.resiaiac.entity.Filiere;
import org.aminesidki.resiaiac.entity.Promotion;
import org.aminesidki.resiaiac.repository.PromotionRepository;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

/** Mapper for {@link org.aminesidki.resiaiac.entity.Filiere } */
@Mapper(componentModel = "spring")
public abstract class FiliereMapper {

  @Autowired private PromotionRepository promotionRepo;

  // Map to DTO
  public abstract FiliereDto toDto(Filiere entity);

  // Map to Entity
  public abstract Filiere toEntity(FiliereDto dto);

  public List<Promotion> mapIdToPromotions(List<UUID> ids) {
    if (ids == null) {
      return null;
    }
    return promotionRepo.findAllById(ids);
  }

  public List<UUID> mapPromotionsToIds(List<Promotion> entities) {
    if (entities == null) {
      return null;
    }
    return entities.stream().map(e -> e.getId()).toList();
  }
}
