package org.aminesidki.resiaiac.controller;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.ReservationDto;
import org.aminesidki.resiaiac.dto.request.ReservationUpdateRequest;
import org.aminesidki.resiaiac.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservation")
public class ReservationController {
  private final ReservationService reservationService;

  @GetMapping("/{id}")
  public ResponseEntity<?> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(reservationService.getById(id));
  }

  @PostMapping("/")
  public ResponseEntity<?> save(@RequestBody ReservationDto dto) {
    return ResponseEntity.ok(reservationService.save(dto));
  }

  @PutMapping("/")
  public ResponseEntity<?> save(@RequestBody ReservationUpdateRequest request) {
    return ResponseEntity.ok(reservationService.update(request.id(), request.dto()));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void delete(@PathVariable UUID id) {
    reservationService.delete(id);
  }
}
