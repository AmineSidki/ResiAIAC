package org.aminesidki.resiaiac.mapper;

import java.util.List;
import java.util.UUID;
import org.aminesidki.resiaiac.dto.ServiceDto;
import org.aminesidki.resiaiac.entity.Reclamation;
import org.aminesidki.resiaiac.entity.Service;
import org.aminesidki.resiaiac.repository.ReclamationRepository;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

/** Mapper for {@link org.aminesidki.resiaiac.entity.Service } */
@Mapper(
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    componentModel = "spring")
public abstract class ServiceMapper {

  @Autowired private ReclamationRepository reclamationRepo;

  // Map to DTO
  public abstract ServiceDto toDto(Service entity);

  // Map to Entity
  public abstract Service toEntity(ServiceDto dto);

  // Update entity with Dto
  public abstract void updateEntityFromDto(ServiceDto dto, @MappingTarget Service entity);

  protected List<Reclamation> mapIdToReclamations(List<UUID> ids) {
    if (ids == null) {
      return null;
    }
    return reclamationRepo.findAllById(ids);
  }

  protected List<UUID> mapReclamationsToIds(List<Reclamation> entities) {
    if (entities == null) {
      return null;
    }
    return entities.stream().map(e -> e.getId()).toList();
  }
}
