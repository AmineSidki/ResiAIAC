package org.aminesidki.resiaiac.mapper;

import org.aminesidki.resiaiac.dto.EquipementUpcDto;
import org.aminesidki.resiaiac.entity.EquipementUpc;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EquipementUpcMapper {

    @Mapping(source = "equipement.id", target = "equipementId")
    @Mapping(source = "upc.id",target = "upcId")
    EquipementUpcDto toDto(EquipementUpc entity);

    @Mapping(target = "equipement", ignore = true)
    @Mapping(target = "upc", ignore = true)
    EquipementUpc toEntity(EquipementUpcDto dto);
}
