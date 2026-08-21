package org.aminesidki.resiaiac.service;

import java.util.UUID;
import org.aminesidki.resiaiac.dto.ReservationDto;
import org.aminesidki.resiaiac.dto.request.MyReservationRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;

public interface ReservationService {
  Page<ReservationDto> getAllMy(Jwt jwt, Pageable pageable);

  ReservationDto saveMy(Jwt jwt, MyReservationRequest request);

  Page<ReservationDto> getAll(Pageable pageable);

  ReservationDto save(ReservationDto dto);

  ReservationDto getById(UUID id);

  ReservationDto update(UUID id, ReservationDto dto);

  void delete(UUID id);
}
