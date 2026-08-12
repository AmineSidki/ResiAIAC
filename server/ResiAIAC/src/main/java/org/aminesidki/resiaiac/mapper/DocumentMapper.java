package org.aminesidki.resiaiac.mapper;

import java.util.UUID;
import org.aminesidki.resiaiac.dto.DocumentDto;
import org.aminesidki.resiaiac.entity.Document;
import org.aminesidki.resiaiac.entity.Utilisateur;
import org.aminesidki.resiaiac.repository.UtilisateurRepository;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

/** Mapper for {@link org.aminesidki.resiaiac.entity.Document } */
@Mapper(
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    componentModel = "spring")
public abstract class DocumentMapper {

  @Autowired private UtilisateurRepository utilisateurRepo;

  // Map to DTO
  public abstract DocumentDto toDto(Document entity);

  // Map to Entity
  public abstract Document toEntity(DocumentDto dto);

  // Update entity with Dto
  public abstract void updateEntityFromDto(DocumentDto dto, @MappingTarget Document entity);

  protected Utilisateur mapIdToUtilisateur(UUID id) {
    if (id == null) {
      return null;
    }
    return utilisateurRepo.findById(id).orElse(null);
  }

  protected UUID mapUtilisateurToId(Utilisateur entity) {
    if (entity == null) {
      return null;
    }
    return entity.getId();
  }
}
