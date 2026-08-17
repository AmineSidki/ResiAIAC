package org.aminesidki.resiaiac.controller;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.EquipementUpcDto;
import org.aminesidki.resiaiac.dto.request.EquipementUpcUpdateRequest;
import org.aminesidki.resiaiac.service.EquipementUpcService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MANAGER')")
@RequestMapping("/api/v1/equipement-upc")
public class EquipementUpcController {
  private final EquipementUpcService equipementUpcService;

  @GetMapping("/")
  public ResponseEntity<?> getById(
      @RequestParam Long equipementId,
      @RequestParam UUID utilisateurId,
      @RequestParam UUID promotionId,
      @RequestParam UUID chambreId) {
    return ResponseEntity.ok(
        equipementUpcService.getById(equipementId, utilisateurId, promotionId, chambreId));
  }

  @PostMapping("/")
  public ResponseEntity<?> save(@RequestBody @Valid EquipementUpcDto dto) {
    return ResponseEntity.ok(equipementUpcService.save(dto));
  }

  @PutMapping("/")
  public ResponseEntity<?> update(@RequestBody @Valid EquipementUpcUpdateRequest request) {
    return ResponseEntity.ok(equipementUpcService.update(request.id(), request.dto()));
  }

  @DeleteMapping("/")
  @ResponseStatus(HttpStatus.OK)
  public void delete(
      @RequestParam Long equipementId,
      @RequestParam UUID utilisateurId,
      @RequestParam UUID promotionId,
      @RequestParam UUID chambreId) {
    equipementUpcService.delete(equipementId, utilisateurId, promotionId, chambreId);
  }
}
