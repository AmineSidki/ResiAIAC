package org.aminesidki.resiaiac.mapper;

import org.aminesidki.resiaiac.dto.ReclamationDto;
import org.aminesidki.resiaiac.entity.Reclamation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReclamationMapper {

    @Mapping(source = "utilisateur.id",target = "utilisateurId")
    @Mapping(source = "chambre.id",target = "chambreId")
    @Mapping(source = "service.id",target = "serviceId")
    ReclamationDto toDto(Reclamation entity);

    @Mapping(target = "utilisateur", ignore = true)
    @Mapping(target = "chambre", ignore = true)
    @Mapping(target = "service", ignore = true)
    Reclamation toEntity(ReclamationDto dto);
}
