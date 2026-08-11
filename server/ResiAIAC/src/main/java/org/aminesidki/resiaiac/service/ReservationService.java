package org.aminesidki.resiaiac.service;

import java.util.UUID;
import org.aminesidki.resiaiac.dto.ReservationDto;

public interface ReservationService {
  ReservationDto save(ReservationDto dto);

  ReservationDto getById(UUID id);

  ReservationDto update(UUID id, ReservationDto dto);

  void delete(UUID id);
}
