package org.aminesidki.resiaiac.controller;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.ChambreDto;
import org.aminesidki.resiaiac.dto.request.ChambreUpdateRequest;
import org.aminesidki.resiaiac.service.ChambreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chambre")
public class ChambreController {
  private final ChambreService chambreService;

  @GetMapping("/{id}")
  public ResponseEntity<?> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(chambreService.getById(id));
  }

  @PostMapping("/")
  public ResponseEntity<?> save(@RequestBody ChambreDto dto) {
    return ResponseEntity.ok(chambreService.save(dto));
  }

  @PutMapping("/")
  public ResponseEntity<?> update(@RequestBody ChambreUpdateRequest request) {
    return ResponseEntity.ok(chambreService.update(request.id(), request.dto()));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void delete(@PathVariable UUID id) {
    chambreService.delete(id);
  }
}
