package org.aminesidki.resiaiac.mapper;

import org.aminesidki.resiaiac.dto.ReservationDto;
import org.aminesidki.resiaiac.entity.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    @Mapping(source = "utilisateur.id",target = "utilisateurId")
    @Mapping(source = "chambre.id",target = "chambreId")
    ReservationDto toDto(Reservation entity);

    @Mapping(target = "utilisateur", ignore = true)
    @Mapping(target = "chambre", ignore = true)
    Reservation toEntity(ReservationDto dto);
}
