package org.aminesidki.resiaiac.service.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.ReservationDto;
import org.aminesidki.resiaiac.mapper.ReservationMapper;
import org.aminesidki.resiaiac.repository.ReservationRepository;
import org.aminesidki.resiaiac.service.ReservationService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ReservationServiceImpl implements ReservationService {

  private final ReservationRepository reservationRepository;
  private final ReservationMapper reservationMapper;

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
