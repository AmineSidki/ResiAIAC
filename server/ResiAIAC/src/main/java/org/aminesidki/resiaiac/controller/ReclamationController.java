package org.aminesidki.resiaiac.controller;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.ReclamationDto;
import org.aminesidki.resiaiac.dto.request.ReclamationUpdateRequest;
import org.aminesidki.resiaiac.service.ReclamationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reclamation")
@RequiredArgsConstructor
public class ReclamationController {
  private final ReclamationService reclamationService;

  @GetMapping("/{id}")
  public ResponseEntity<?> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(reclamationService.getById(id));
  }

  @PostMapping("/")
  public ResponseEntity<?> save(@RequestBody @Valid ReclamationDto dto) {
    return ResponseEntity.ok(reclamationService.save(dto));
  }

  @PutMapping("/")
  public ResponseEntity<?> update(@RequestBody @Valid ReclamationUpdateRequest request) {
    return ResponseEntity.ok(reclamationService.update(request.id(), request.dto()));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void delete(@PathVariable UUID id) {
    reclamationService.delete(id);
  }
}
