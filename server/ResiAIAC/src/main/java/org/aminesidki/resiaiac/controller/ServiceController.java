package org.aminesidki.resiaiac.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.ServiceDto;
import org.aminesidki.resiaiac.dto.request.ServiceUpdateRequest;
import org.aminesidki.resiaiac.service.ServiceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/service")
@RequiredArgsConstructor
public class ServiceController {
  private final ServiceService serviceService;

  @GetMapping("/{id}")
  public ResponseEntity<?> getById(@PathVariable Long id) {
    return ResponseEntity.ok(serviceService.getById(id));
  }

  @PostMapping("/")
  public ResponseEntity<?> save(@RequestBody @Valid ServiceDto dto) {
    return ResponseEntity.ok(serviceService.save(dto));
  }

  @PutMapping("/")
  public ResponseEntity<?> update(@RequestBody @Valid ServiceUpdateRequest request) {
    return ResponseEntity.ok(serviceService.update(request.id(), request.dto()));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void delete(@PathVariable Long id) {
    serviceService.delete(id);
  }
}
