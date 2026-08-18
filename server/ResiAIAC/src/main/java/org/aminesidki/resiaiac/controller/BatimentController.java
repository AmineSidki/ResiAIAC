package org.aminesidki.resiaiac.controller;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.BatimentDto;
import org.aminesidki.resiaiac.dto.request.BatimentUpdateRequest;
import org.aminesidki.resiaiac.service.BatimentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/batiment")
public class BatimentController {
  private final BatimentService batimentService;
  
  @GetMapping("/")
  public ResponseEntity<?> getAll() {
    return ResponseEntity.ok(batimentService.getAll());
  }

  @PreAuthorize("hasAnyRole('MANAGER')")
  @GetMapping("/{id}")
  public ResponseEntity<?> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(batimentService.getById(id));
  }

  @PreAuthorize("hasAnyRole('RESPONSABLE')")
  @PostMapping("/")
  public ResponseEntity<?> save(@RequestBody @Valid BatimentDto dto) {
    return ResponseEntity.ok(batimentService.save(dto));
  }

  @PreAuthorize("hasAnyRole('RESPONSABLE')")
  @PutMapping("/")
  public ResponseEntity<?> update(@RequestBody @Valid BatimentUpdateRequest request) {
    return ResponseEntity.ok(batimentService.update(request.id(), request.dto()));
  }

  @PreAuthorize("hasAnyRole('RESPONSABLE')")
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void delete(@PathVariable UUID id) {
    batimentService.delete(id);
  }
}
