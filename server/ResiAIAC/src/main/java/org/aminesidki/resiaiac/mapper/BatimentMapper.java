package org.aminesidki.resiaiac.mapper;

import org.aminesidki.resiaiac.dto.BatimentDto;
import org.aminesidki.resiaiac.entity.Batiment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BatimentMapper {
    BatimentDto toDto(Batiment entity);
    Batiment toEntity(BatimentDto dto);
}
