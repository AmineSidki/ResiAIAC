package org.aminesidki.resiaiac.controller;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.EquipementReclamationDto;
import org.aminesidki.resiaiac.dto.request.EquipementReclamationRequest;
import org.aminesidki.resiaiac.service.EquipementReclamationService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MANAGER')")
@RequestMapping("/api/v1/equipement-reclamation")
public class EquipementReclamationController {
  private final EquipementReclamationService equipementReclamationService;

  @GetMapping("/by-Equipement/{id}")
  public ResponseEntity<?> getAllByEquipementId(
      @PathVariable Long id,
      @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
    return ResponseEntity.ok(equipementReclamationService.getAllByEquipementId(id, pageable));
  }

  @GetMapping("/by-reclamation/{id}")
  public ResponseEntity<?> getAllByEquipementId(@PathVariable UUID id) {
    return ResponseEntity.ok(equipementReclamationService.getAllByReclamationId(id));
  }

  @GetMapping("/")
  public ResponseEntity<?> getById(
      @RequestParam Long equipementId, @RequestParam UUID reclamationId) {
    return ResponseEntity.ok(equipementReclamationService.getById(equipementId, reclamationId));
  }

  @PostMapping("/")
  public ResponseEntity<?> save(@RequestBody @Valid EquipementReclamationDto dto) {
    return ResponseEntity.ok(equipementReclamationService.save(dto));
  }

  @PutMapping("/")
  public ResponseEntity<?> update(@RequestBody @Valid EquipementReclamationRequest request) {
    return ResponseEntity.ok(equipementReclamationService.update(request.id(), request.dto()));
  }

  @DeleteMapping("/")
  @ResponseStatus(HttpStatus.OK)
  public void delete(@RequestParam Long equipementId, @RequestParam UUID reclamationId) {
    equipementReclamationService.delete(equipementId, reclamationId);
  }
}
