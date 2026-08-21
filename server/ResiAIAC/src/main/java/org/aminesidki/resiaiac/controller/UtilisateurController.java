package org.aminesidki.resiaiac.controller;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.UtilisateurDto;
import org.aminesidki.resiaiac.dto.request.UpdateMeRequest;
import org.aminesidki.resiaiac.dto.request.UtilisateurUpdateRequest;
import org.aminesidki.resiaiac.service.UtilisateurService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/utilisateur")
public class UtilisateurController {
  private final UtilisateurService utilisateurService;

  @GetMapping("/me")
  public ResponseEntity<?> getMe(@AuthenticationPrincipal Jwt jwt) {
    return ResponseEntity.ok(utilisateurService.getMyDto(jwt));
  }

  @PutMapping("/me")
  public ResponseEntity<?> updateMe(
      @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody UpdateMeRequest request) {
    return ResponseEntity.ok(utilisateurService.updateMe(jwt, request));
  }

  @PreAuthorize("hasAnyRole('RESPONSABLE')")
  @GetMapping("/")
  public ResponseEntity<?> getAll(
      @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
    return ResponseEntity.ok(utilisateurService.getAll(pageable));
  }

  @PreAuthorize("hasAnyRole('MANAGER')")
  @GetMapping("/{id}")
  public ResponseEntity<?> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(utilisateurService.getById(id));
  }

  @PreAuthorize("hasAnyRole('RESPONSABLE')")
  @PostMapping("/")
  public ResponseEntity<?> save(@RequestBody @Valid UtilisateurDto dto) {
    return ResponseEntity.ok(utilisateurService.save(dto));
  }

  @PreAuthorize("hasAnyRole('RESPONSABLE')")
  @PutMapping("/")
  public ResponseEntity<?> update(@RequestBody @Valid UtilisateurUpdateRequest request) {
    return ResponseEntity.ok(utilisateurService.update(request.id(), request.dto()));
  }

  @PreAuthorize("hasAnyRole('RESPONSABLE')")
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void delete(@PathVariable UUID id) {
    utilisateurService.delete(id);
  }
}
