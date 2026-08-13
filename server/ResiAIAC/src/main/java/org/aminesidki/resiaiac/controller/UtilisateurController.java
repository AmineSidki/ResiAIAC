package org.aminesidki.resiaiac.controller;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.UtilisateurDto;
import org.aminesidki.resiaiac.dto.request.UtilisateurUpdateRequest;
import org.aminesidki.resiaiac.service.UtilisateurService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/utilisateur")
public class UtilisateurController {
  private final UtilisateurService utilisateurService;

  @GetMapping("/{id}")
  public ResponseEntity<?> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(utilisateurService.getById(id));
  }

  @PostMapping("/")
  public ResponseEntity<?> save(@RequestBody UtilisateurDto dto) {
    return ResponseEntity.ok(utilisateurService.save(dto));
  }

  @PutMapping("/")
  public ResponseEntity<?> update(@RequestBody UtilisateurUpdateRequest request) {
    return ResponseEntity.ok(utilisateurService.update(request.id(), request.dto()));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void delete(@PathVariable UUID id) {
    utilisateurService.delete(id);
  }
}
