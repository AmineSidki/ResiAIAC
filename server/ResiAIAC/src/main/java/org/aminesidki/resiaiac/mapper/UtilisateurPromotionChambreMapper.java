package org.aminesidki.resiaiac.mapper;

import org.aminesidki.resiaiac.dto.UtilisateurPromotionChambreDto;
import org.aminesidki.resiaiac.entity.UtilisateurPromotionChambre;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UtilisateurPromotionChambreMapper {

    @Mapping(source = "utilisateur.id",target = "utilisateurId")
    @Mapping(source = "promotion.id",target = "promotionId")
    @Mapping(source = "chambre.id",target = "chambreId")
UtilisateurPromotionChambreDto toDto(UtilisateurPromotionChambre entity);

    @Mapping(target = "utilisateur", ignore = true)
    @Mapping(target = "promotion", ignore = true)
    @Mapping(target = "chambre", ignore = true)
UtilisateurPromotionChambre toEntity(UtilisateurPromotionChambreDto dto);
}
