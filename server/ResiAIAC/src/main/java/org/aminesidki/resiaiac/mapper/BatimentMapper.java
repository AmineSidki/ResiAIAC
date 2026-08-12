package org.aminesidki.resiaiac.mapper;

import java.util.List;
import java.util.UUID;
import org.aminesidki.resiaiac.dto.BatimentDto;
import org.aminesidki.resiaiac.entity.Batiment;
import org.aminesidki.resiaiac.entity.Etage;
import org.aminesidki.resiaiac.repository.EtageRepository;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

/** Mapper for {@link org.aminesidki.resiaiac.entity.Batiment } */
@Mapper(
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    componentModel = "spring")
public abstract class BatimentMapper {

  @Autowired private EtageRepository etageRepo;

  // Map to DTO
  public abstract BatimentDto toDto(Batiment entity);

  // Map to Entity
  public abstract Batiment toEntity(BatimentDto dto);

  // Update entity with Dto
  public abstract void updateEntityFromDto(BatimentDto dto, @MappingTarget Batiment entity);

  protected List<Etage> mapIdToEtages(List<UUID> ids) {
    if (ids == null) {
      return null;
    }
    return etageRepo.findAllById(ids);
  }

  protected List<UUID> mapEtagesToIds(List<Etage> entities) {
    if (entities == null) {
      return null;
    }
    return entities.stream().map(e -> e.getId()).toList();
  }
}
