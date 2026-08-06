package org.aminesidki.resiaiac.mapper;

import org.aminesidki.resiaiac.dto.EquipementDto;
import org.aminesidki.resiaiac.entity.Equipement;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EquipementMapper {
    EquipementDto toDto(Equipement entity);
    Equipement toEntity(EquipementDto dto);
}
