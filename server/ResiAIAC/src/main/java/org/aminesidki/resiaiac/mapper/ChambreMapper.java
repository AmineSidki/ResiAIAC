package org.aminesidki.resiaiac.mapper;

import org.aminesidki.resiaiac.dto.ChambreDto;
import org.aminesidki.resiaiac.entity.Chambre;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChambreMapper {

    @Mapping(source = "etage.id", target = "etageId")
    ChambreDto toDto(Chambre entity);

    @Mapping(target = "etage", ignore = true)
    Chambre toEntity(ChambreDto dto);
}
