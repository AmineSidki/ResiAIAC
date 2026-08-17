package org.aminesidki.resiaiac.controller;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.ReclamationDto;
import org.aminesidki.resiaiac.dto.request.ReclamationUpdateRequest;
import org.aminesidki.resiaiac.service.ReclamationService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reclamation")
@RequiredArgsConstructor
public class ReclamationController {
  private final ReclamationService reclamationService;

  @PreAuthorize("hasAnyRole('MANAGER')")
  @GetMapping("/")
  public ResponseEntity<?> getAll(
      @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
    return ResponseEntity.ok(reclamationService.getAll(pageable));
  }

  @PreAuthorize("hasAnyRole('MANAGER')")
  @GetMapping("/{id}")
  public ResponseEntity<?> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(reclamationService.getById(id));
  }

  @PostMapping("/")
  public ResponseEntity<?> save(@RequestBody @Valid ReclamationDto dto) {
    return ResponseEntity.ok(reclamationService.save(dto));
  }

  @PreAuthorize("hasAnyRole('MANAGER')")
  @PutMapping("/")
  public ResponseEntity<?> update(@RequestBody @Valid ReclamationUpdateRequest request) {
    return ResponseEntity.ok(reclamationService.update(request.id(), request.dto()));
  }

  @PreAuthorize("hasAnyRole('MANAGER')")
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void delete(@PathVariable UUID id) {
    reclamationService.delete(id);
  }
}
