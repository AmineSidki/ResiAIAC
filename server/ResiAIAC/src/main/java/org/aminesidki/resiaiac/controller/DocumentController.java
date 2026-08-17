package org.aminesidki.resiaiac.controller;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.DocumentDto;
import org.aminesidki.resiaiac.dto.request.DocumentUpdateRequest;
import org.aminesidki.resiaiac.service.DocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/document")
public class DocumentController {
  private final DocumentService documentService;

  @GetMapping("/{id}")
  public ResponseEntity<?> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(documentService.getById(id));
  }

  @PostMapping("/")
  public ResponseEntity<?> save(@RequestBody @Valid DocumentDto dto) {
    return ResponseEntity.ok(documentService.save(dto));
  }

  @PutMapping("/")
  public ResponseEntity<?> update(@RequestBody @Valid DocumentUpdateRequest request) {
    return ResponseEntity.ok(documentService.update(request.id(), request.dto()));
  }

  @ResponseStatus(HttpStatus.OK)
  @DeleteMapping("/{id}")
  public void delete(@PathVariable UUID id) {
    documentService.delete(id);
  }
}
