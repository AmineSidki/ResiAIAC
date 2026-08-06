package org.aminesidki.resiaiac.mapper;

import org.aminesidki.resiaiac.dto.UtilisateurDto;
import org.aminesidki.resiaiac.entity.Utilisateur;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UtilisateurMapper {

    UtilisateurDto toDto(Utilisateur entity);

    Utilisateur toEntity(UtilisateurDto dto);
}
