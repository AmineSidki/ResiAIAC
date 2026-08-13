package org.aminesidki.resiaiac.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.EquipementDto;
import org.aminesidki.resiaiac.dto.request.EquipementUpdateRequest;
import org.aminesidki.resiaiac.service.EquipementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/equipement")
public class EquipementController {
  private final EquipementService equipementService;

  @GetMapping("/")
  public ResponseEntity<?> getAll() {
    return ResponseEntity.ok(equipementService.getAll());
  }

  @PreAuthorize("hasAnyRole('MANAGER')")
  @GetMapping("/{id}")
  public ResponseEntity<?> getById(@PathVariable Long id) {
    return ResponseEntity.ok(equipementService.getById(id));
  }

  @PreAuthorize("hasAnyRole('MANAGER')")
  @PostMapping("/")
  public ResponseEntity<?> save(@RequestBody @Valid EquipementDto dto) {
    return ResponseEntity.ok(equipementService.save(dto));
  }

  @PreAuthorize("hasAnyRole('MANAGER')")
  @PutMapping("/")
  public ResponseEntity<?> update(@RequestBody @Valid EquipementUpdateRequest request) {
    return ResponseEntity.ok(equipementService.update(request.id(), request.dto()));
  }

  @PreAuthorize("hasAnyRole('MANAGER')")
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void delete(@PathVariable Long id) {
    equipementService.delete(id);
  }
}
