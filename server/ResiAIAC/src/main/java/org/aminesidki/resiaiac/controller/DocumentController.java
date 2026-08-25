package org.aminesidki.resiaiac.controller;

import jakarta.validation.Valid;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.request.DocumentUpdateRequest;
import org.aminesidki.resiaiac.enumeration.EtatDocument;
import org.aminesidki.resiaiac.enumeration.FileType;
import org.aminesidki.resiaiac.service.DocumentService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/document")
public class DocumentController {
  private final DocumentService documentService;

  @GetMapping("/me")
  public ResponseEntity<?> getAllMyDocuments(
      @AuthenticationPrincipal Jwt jwt,
      @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
    return ResponseEntity.ok(documentService.getAllMy(jwt, pageable));
  }

  @GetMapping("/me/by-etat/{etat}")
  public ResponseEntity<?> getAllMyDocumentsByStatus(
      @AuthenticationPrincipal Jwt jwt,
      @PathVariable EtatDocument etat,
      @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
    return ResponseEntity.ok(documentService.getAllMyByStatus(jwt, etat, pageable));
  }

  @GetMapping("/me/{id}/url")
  public ResponseEntity<?> getMyDocumentUrlById(
      @AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
    return ResponseEntity.ok(documentService.getMyFileUrlById(jwt, id));
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getMyDocumentById(
      @AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
    return ResponseEntity.ok(documentService.getMyById(jwt, id));
  }

  @PostMapping("/me/upload/pfp")
  public ResponseEntity<?> uploadProfileImage(
      @AuthenticationPrincipal Jwt jwt, @RequestParam("file") MultipartFile file)
      throws IOException {
    return ResponseEntity.ok(documentService.uploadMyDocument(jwt, FileType.IMAGE, file));
  }

  @PostMapping("/me/upload/cin")
  public ResponseEntity<?> uploadCin(
      @AuthenticationPrincipal Jwt jwt, @RequestParam("file") MultipartFile file)
      throws IOException {
    return ResponseEntity.ok(documentService.uploadMyDocument(jwt, FileType.CIN, file));
  }

  @PostMapping("/me/upload/dip")
  public ResponseEntity<?> uploadDiploma(
      @AuthenticationPrincipal Jwt jwt, @RequestParam("file") MultipartFile file)
      throws IOException {
    return ResponseEntity.ok(documentService.uploadMyDocument(jwt, FileType.DIPLOMA, file));
  }

  @PreAuthorize("hasAnyRole('MANAGER')")
  @GetMapping("/{id}/url")
  public ResponseEntity<?> getDocumentUrlById(@PathVariable UUID id) {
    return ResponseEntity.ok(documentService.getFileUrlById(id));
  }

  @PreAuthorize("hasAnyRole('MANAGER')")
  @GetMapping("/{id}")
  public ResponseEntity<?> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(documentService.getById(id));
  }

  @PreAuthorize("hasAnyRole('RESPONSABLE')")
  @GetMapping("/")
  public ResponseEntity<?> getAll(
      @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
    return ResponseEntity.ok(documentService.getAll(pageable));
  }

  @PreAuthorize("hasAnyRole('RESPONSABLE')")
  @GetMapping("/{etat}")
  public ResponseEntity<?> getAllByStatus(
      @PathVariable EtatDocument etat,
      @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
    return ResponseEntity.ok(documentService.getAllByStatus(etat, pageable));
  }

  @PreAuthorize("hasAnyRole('MANAGER')")
  @PutMapping("/")
  public ResponseEntity<?> update(@RequestBody @Valid DocumentUpdateRequest request) {
    return ResponseEntity.ok(documentService.update(request.id(), request.dto()));
  }

  @PreAuthorize("hasAnyRole('RESPONSABLE')")
  @ResponseStatus(HttpStatus.OK)
  @DeleteMapping("/{id}")
  public void delete(@PathVariable UUID id) {
    documentService.delete(id);
  }
}
