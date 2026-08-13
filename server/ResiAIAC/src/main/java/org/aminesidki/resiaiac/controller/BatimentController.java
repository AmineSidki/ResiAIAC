package org.aminesidki.resiaiac.controller;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.BatimentDto;
import org.aminesidki.resiaiac.dto.request.BatimentUpdateRequest;
import org.aminesidki.resiaiac.service.BatimentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/batiment")
public class BatimentController {
  private final BatimentService batimentService;

  @GetMapping("/{id}")
  public ResponseEntity<?> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(batimentService.getById(id));
  }

  @PostMapping("/")
  public ResponseEntity<?> save(@RequestBody BatimentDto dto) {
    return ResponseEntity.ok(batimentService.save(dto));
  }

  @PutMapping("/")
  public ResponseEntity<?> update(@RequestBody BatimentUpdateRequest request) {
    return ResponseEntity.ok(batimentService.update(request.id(), request.dto()));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void delete(@PathVariable UUID id) {
    batimentService.delete(id);
  }
}
