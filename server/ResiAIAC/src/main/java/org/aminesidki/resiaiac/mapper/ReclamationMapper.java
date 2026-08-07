package org.aminesidki.resiaiac.mapper;

import java.util.List;
import java.util.UUID;
import org.aminesidki.resiaiac.dto.ReclamationDto;
import org.aminesidki.resiaiac.entity.Chambre;
import org.aminesidki.resiaiac.entity.EquipementReclamation;
import org.aminesidki.resiaiac.entity.Reclamation;
import org.aminesidki.resiaiac.entity.Service;
import org.aminesidki.resiaiac.entity.Utilisateur;
import org.aminesidki.resiaiac.entity.id.EquipementReclamationId;
import org.aminesidki.resiaiac.repository.ChambreRepository;
import org.aminesidki.resiaiac.repository.EquipementReclamationRepository;
import org.aminesidki.resiaiac.repository.ServiceRepository;
import org.aminesidki.resiaiac.repository.UtilisateurRepository;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

/** Mapper for {@link org.aminesidki.resiaiac.entity.Reclamation } */
@Mapper(componentModel = "spring")
public abstract class ReclamationMapper {

  @Autowired private ServiceRepository serviceRepo;
  @Autowired private EquipementReclamationRepository equipementReclamationRepo;
  @Autowired private UtilisateurRepository utilisateurRepo;
  @Autowired private ChambreRepository chambreRepo;

  // Map to DTO
  public abstract ReclamationDto toDto(Reclamation entity);

  // Map to Entity
  public abstract Reclamation toEntity(ReclamationDto dto);

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

  public Chambre mapIdToChambre(UUID id) {
    if (id == null) {
      return null;
    }
    return chambreRepo.findById(id).orElse(null);
  }

  public UUID mapChambreToId(Chambre entity) {
    if (entity == null) {
      return null;
    }
    return entity.getId();
  }

  public Service mapIdToService(Long id) {
    if (id == null) {
      return null;
    }
    return serviceRepo.findById(id).orElse(null);
  }

  public Long mapServiceToId(Service entity) {
    if (entity == null) {
      return null;
    }
    return entity.getId();
  }

  public List<EquipementReclamation> mapIdToEquipementReclamations(
      List<EquipementReclamationId> ids) {
    if (ids == null) {
      return null;
    }
    return equipementReclamationRepo.findAllById(ids);
  }

  public List<EquipementReclamationId> mapEquipementReclamationsToIds(
      List<EquipementReclamation> entities) {
    if (entities == null) {
      return null;
    }
    return entities.stream().map(e -> e.getId()).toList();
  }
}