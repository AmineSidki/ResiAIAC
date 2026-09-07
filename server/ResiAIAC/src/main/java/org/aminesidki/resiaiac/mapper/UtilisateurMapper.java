package org.aminesidki.resiaiac.mapper;

import org.aminesidki.resiaiac.dto.UtilisateurDto;
import org.aminesidki.resiaiac.dto.request.UpdateMeRequest;
import org.aminesidki.resiaiac.entity.*;
import org.aminesidki.resiaiac.repository.*;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

/** Mapper for {@link org.aminesidki.resiaiac.entity.Utilisateur } */
@Mapper(
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    componentModel = "spring")
public abstract class UtilisateurMapper {

  @Autowired private FiliereRepository filiereRepo;

  // Map to DTO
  public abstract UtilisateurDto toDto(Utilisateur entity);

  // Map to Entity
  public abstract Utilisateur toEntity(UtilisateurDto dto);

  // Update entity with Dto
  public abstract void updateEntityFromDto(UtilisateurDto dto, @MappingTarget Utilisateur entity);

  // Map UpdateMeRequest to Dto
  public abstract UtilisateurDto updateMeRequestToDto(UpdateMeRequest request);

  protected Filiere mapIdToFiliere(Long id) {
    if (id == null) {
      return null;
    }
    return filiereRepo.findById(id).orElse(null);
  }

  protected Long mapFiliereToId(Filiere entity) {
    if (entity == null) {
      return null;
    }
    return entity.getId();
  }
}
