package org.aminesidki.resiaiac.mapper;

import java.util.UUID;
import org.aminesidki.resiaiac.dto.DocumentDto;
import org.aminesidki.resiaiac.entity.Document;
import org.aminesidki.resiaiac.entity.Utilisateur;
import org.aminesidki.resiaiac.repository.UtilisateurRepository;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

/** Mapper for {@link org.aminesidki.resiaiac.entity.Document } */
@Mapper(componentModel = "spring")
public abstract class DocumentMapper {

  @Autowired private UtilisateurRepository utilisateurRepo;

  // Map to DTO
  public abstract DocumentDto toDto(Document entity);

  // Map to Entity
  public abstract Document toEntity(DocumentDto dto);

  public Utilisateur mapIdToUtilisateur(UUID id) {
    if (id == null) {
      return null;
    }
    return utilisateurRepo.findById(id).orElse(null);
  }

  public UUID mapUtilisateurToId(Utilisateur entity) {
    if (entity == null) {
      return null;
    }
    return entity.getId();
  }
}
