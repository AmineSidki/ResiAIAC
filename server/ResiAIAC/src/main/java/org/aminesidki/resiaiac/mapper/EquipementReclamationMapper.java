package org.aminesidki.resiaiac.mapper;

import org.aminesidki.resiaiac.dto.EquipementReclamationDto;
import org.aminesidki.resiaiac.entity.EquipementReclamation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EquipementReclamationMapper {

    @Mapping(source = "equipement.id", target = "equipementId")
    @Mapping(source = "reclamation.id", target = "reclamationId")
    EquipementReclamationDto toDto(EquipementReclamation entity);

    @Mapping(target = "equipement", ignore = true)
    @Mapping(target = "reclamation", ignore = true)
    EquipementReclamation toEntity(EquipementReclamationDto dto);
}
