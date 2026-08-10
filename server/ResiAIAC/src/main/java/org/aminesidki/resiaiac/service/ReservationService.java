package org.aminesidki.resiaiac.service;

import org.aminesidki.resiaiac.dto.ReservationDto;

import java.util.UUID;

public interface ReservationService {
    ReservationDto save(ReservationDto dto);
    ReservationDto getById(UUID id);
    ReservationDto update(UUID id,ReservationDto dto);
    void delete(UUID id);
}
