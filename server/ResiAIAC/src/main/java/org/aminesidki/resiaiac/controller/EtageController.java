package org.aminesidki.resiaiac.controller;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.EtageDto;
import org.aminesidki.resiaiac.dto.request.EtageUpdateRequest;
import org.aminesidki.resiaiac.service.EtageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/etage")
public class EtageController {
  private final EtageService etageService;

  @GetMapping("/")
  public ResponseEntity<?> getAll() {
    return ResponseEntity.ok(etageService.getAll());
  }

  @PreAuthorize("hasAnyRole('MANAGER')")
  @GetMapping("/{id}")
  public ResponseEntity<?> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(etageService.getById(id));
  }

  @PreAuthorize("hasAnyRole('RESPONSABLE')")
  @PostMapping("/")
  public ResponseEntity<?> save(@RequestBody @Valid EtageDto dto) {
    return ResponseEntity.ok(etageService.save(dto));
  }

  @PreAuthorize("hasAnyRole('RESPONSABLE')")
  @PutMapping("/")
  public ResponseEntity<?> update(@RequestBody @Valid EtageUpdateRequest request) {
    return ResponseEntity.ok((etageService.update(request.id(), request.dto())));
  }

  @PreAuthorize("hasAnyRole('RESPONSABLE')")
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void delete(@PathVariable UUID id) {
    etageService.delete(id);
  }
}
