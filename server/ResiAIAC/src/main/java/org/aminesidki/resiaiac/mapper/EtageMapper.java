package org.aminesidki.resiaiac.mapper;

import org.aminesidki.resiaiac.dto.EtageDto;
import org.aminesidki.resiaiac.entity.Etage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EtageMapper {

    @Mapping(source = "batiment.id", target = "batimentId")
    EtageDto toDto(Etage entity);

    @Mapping(target = "batiment", ignore = true)
    Etage toEntity(EtageDto dto);
}
