package org.aminesidki.resiaiac.controller;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.ChambreDto;
import org.aminesidki.resiaiac.dto.request.ChambreUpdateRequest;
import org.aminesidki.resiaiac.service.ChambreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chambre")
public class ChambreController {
  private final ChambreService chambreService;

  @GetMapping("/")
  public ResponseEntity<?> getAll() {
    return ResponseEntity.ok(chambreService.getAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(chambreService.getById(id));
  }

  @PreAuthorize("hasAnyRole('RESPONSABLE')")
  @PostMapping("/")
  public ResponseEntity<?> save(@RequestBody @Valid ChambreDto dto) {
    return ResponseEntity.ok(chambreService.save(dto));
  }

  @PreAuthorize("hasAnyRole('RESPONSABLE')")
  @PutMapping("/")
  public ResponseEntity<?> update(@RequestBody @Valid ChambreUpdateRequest request) {
    return ResponseEntity.ok(chambreService.update(request.id(), request.dto()));
  }

  @PreAuthorize("hasAnyRole('RESPONSABLE')")
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void delete(@PathVariable UUID id) {
    chambreService.delete(id);
  }
}
