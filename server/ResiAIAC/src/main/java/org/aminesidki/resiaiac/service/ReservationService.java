package org.aminesidki.resiaiac.service;

import java.util.UUID;
import org.aminesidki.resiaiac.dto.ReservationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReservationService {
  Page<ReservationDto> getAll(Pageable pageable);

  ReservationDto save(ReservationDto dto);

  ReservationDto getById(UUID id);

  ReservationDto update(UUID id, ReservationDto dto);

  void delete(UUID id);
}
