package org.aminesidki.resiaiac.controller;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.ReservationDto;
import org.aminesidki.resiaiac.dto.request.MyReservationRequest;
import org.aminesidki.resiaiac.dto.request.ReservationUpdateRequest;
import org.aminesidki.resiaiac.service.ReservationService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservation")
public class ReservationController {
  private final ReservationService reservationService;

  @GetMapping("/me")
  public ResponseEntity<?> getAllMyReservations(
      @AuthenticationPrincipal Jwt jwt,
      @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
    return ResponseEntity.ok(reservationService.getAllMy(jwt, pageable));
  }

  @PostMapping("/")
  public ResponseEntity<?> saveMyReservation(
      @AuthenticationPrincipal Jwt jwt, @RequestBody @Valid MyReservationRequest request) {
    return ResponseEntity.ok(reservationService.saveMy(jwt, request));
  }

  @PreAuthorize("hasAnyRole('MANAGER')")
  @GetMapping("/")
  public ResponseEntity<?> getAll(
      @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
    return ResponseEntity.ok(reservationService.getAll(pageable));
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(reservationService.getById(id));
  }

  @PreAuthorize("hasAnyRole('MANAGER')")
  @PostMapping("/")
  public ResponseEntity<?> save(@RequestBody @Valid ReservationDto dto) {
    return ResponseEntity.ok(reservationService.save(dto));
  }

  @PreAuthorize("hasAnyRole('MANAGER')")
  @PutMapping("/")
  public ResponseEntity<?> update(@RequestBody @Valid ReservationUpdateRequest request) {
    return ResponseEntity.ok(reservationService.update(request.id(), request.dto()));
  }

  @PreAuthorize("hasAnyRole('MANAGER')")
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void delete(@PathVariable UUID id) {
    reservationService.delete(id);
  }
}
