package org.aminesidki.resiaiac.mapper;

import org.aminesidki.resiaiac.dto.ServiceDto;
import org.aminesidki.resiaiac.entity.Service;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ServiceMapper {
    ServiceDto toDto(Service entity);
    Service atoEntity(ServiceDto dto);
}
