package org.aminesidki.resiaiac.service.impl;

import java.util.UUID;
import org.aminesidki.resiaiac.dto.ReservationDto;
import org.aminesidki.resiaiac.service.ReservationService;
import org.springframework.stereotype.Service;

@Service
public class ReservationServiceImpl implements ReservationService {
  @Override
  public ReservationDto save(ReservationDto dto) {
    return null;
  }

  @Override
  public ReservationDto getById(UUID id) {
    return null;
  }

  @Override
  public ReservationDto update(UUID id, ReservationDto dto) {
    return null;
  }

  @Override
  public void delete(UUID id) {}
}
