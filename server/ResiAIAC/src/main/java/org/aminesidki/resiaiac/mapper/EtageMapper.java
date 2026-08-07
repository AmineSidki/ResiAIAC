package org.aminesidki.resiaiac.mapper;

import java.util.List;
import java.util.UUID;
import org.aminesidki.resiaiac.dto.EtageDto;
import org.aminesidki.resiaiac.entity.Batiment;
import org.aminesidki.resiaiac.entity.Chambre;
import org.aminesidki.resiaiac.entity.Etage;
import org.aminesidki.resiaiac.repository.BatimentRepository;
import org.aminesidki.resiaiac.repository.ChambreRepository;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

/** Mapper for {@link org.aminesidki.resiaiac.entity.Etage } */
@Mapper(componentModel = "spring")
public abstract class EtageMapper {

  @Autowired private BatimentRepository batimentRepo;
  @Autowired private ChambreRepository chambreRepo;

  // Map to DTO
  public abstract EtageDto toDto(Etage entity);

  // Map to Entity
  public abstract Etage toEntity(EtageDto dto);

  public Batiment mapIdToBatiment(UUID id) {
    if (id == null) {
      return null;
    }
    return batimentRepo.findById(id).orElse(null);
  }

  public UUID mapBatimentToId(Batiment entity) {
    if (entity == null) {
      return null;
    }
    return entity.getId();
  }

  public List<Chambre> mapIdToChambres(List<UUID> ids) {
    if (ids == null) {
      return null;
    }
    return chambreRepo.findAllById(ids);
  }

  public List<UUID> mapChambresToIds(List<Chambre> entities) {
    if (entities == null) {
      return null;
    }
    return entities.stream().map(e -> e.getId()).toList();
  }
}