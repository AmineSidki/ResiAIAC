package org.aminesidki.resiaiac.mapper;

import org.aminesidki.resiaiac.dto.FiliereDto;
import org.aminesidki.resiaiac.entity.Filiere;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FiliereMapper {

    FiliereDto toDto(Filiere entity);

    Filiere toEntity(FiliereDto dto);
}
