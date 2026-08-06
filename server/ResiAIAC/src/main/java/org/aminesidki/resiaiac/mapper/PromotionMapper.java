package org.aminesidki.resiaiac.mapper;

import org.aminesidki.resiaiac.dto.PromotionDto;
import org.aminesidki.resiaiac.entity.Promotion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PromotionMapper {

    @Mapping(source = "filiere.id", target = "filiereId")
    PromotionDto toDto(Promotion entity);

    @Mapping(target = "filiere", ignore = true)
    Promotion toEntity(PromotionDto dto);
}
