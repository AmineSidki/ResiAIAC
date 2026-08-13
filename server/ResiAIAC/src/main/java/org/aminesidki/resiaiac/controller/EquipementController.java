package org.aminesidki.resiaiac.controller;

import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.EquipementDto;
import org.aminesidki.resiaiac.dto.request.EquipementUpdateRequest;
import org.aminesidki.resiaiac.service.EquipementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/equipement")
public class EquipementController {
  private final EquipementService equipementService;

  @GetMapping("/{id}")
  public ResponseEntity<?> getById(@PathVariable Long id) {
    return ResponseEntity.ok(equipementService.getById(id));
  }

  @PostMapping("/")
  public ResponseEntity<?> save(@RequestBody EquipementDto dto) {
    return ResponseEntity.ok(equipementService.save(dto));
  }

  @PutMapping("/")
  public ResponseEntity<?> save(@RequestBody EquipementUpdateRequest request) {
    return ResponseEntity.ok(equipementService.update(request.id(), request.dto()));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void delete(@PathVariable Long id) {
    equipementService.delete(id);
  }
}
