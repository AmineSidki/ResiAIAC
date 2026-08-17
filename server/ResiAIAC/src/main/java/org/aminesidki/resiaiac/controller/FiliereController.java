package org.aminesidki.resiaiac.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.FiliereDto;
import org.aminesidki.resiaiac.dto.request.FiliereUpdateRequest;
import org.aminesidki.resiaiac.service.FiliereService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/filiere")
public class FiliereController {
  private final FiliereService filiereService;

  @GetMapping("/{id}")
  public ResponseEntity<?> getById(@PathVariable Long id) {
    return ResponseEntity.ok(filiereService.getById(id));
  }

  @PostMapping("/")
  public ResponseEntity<?> save(@RequestBody @Valid FiliereDto dto) {
    return ResponseEntity.ok(filiereService.save(dto));
  }

  @PutMapping("/")
  public ResponseEntity<?> update(@RequestBody @Valid FiliereUpdateRequest request) {
    return ResponseEntity.ok(filiereService.update(request.id(), request.dto()));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void delete(@PathVariable Long id) {
    filiereService.delete(id);
  }
}
