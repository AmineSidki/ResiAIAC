package org.aminesidki.resiaiac.controller;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.UtilisateurPromotionChambreDto;
import org.aminesidki.resiaiac.dto.request.UtilisateurPromotionChambreUpdateRequest;
import org.aminesidki.resiaiac.service.UtilisateurPromotionChambreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/upc")
@RequiredArgsConstructor
public class UtilisateurPromotionChambreController {

  private final UtilisateurPromotionChambreService utilisateurPromotionChambreService;

  @PreAuthorize("hasAnyRole('MANAGER')")
  @GetMapping("/")
  public ResponseEntity<?> getById(
      @RequestParam UUID utilisateurId,
      @RequestParam UUID promotionId,
      @RequestParam UUID chambreId) {
    return ResponseEntity.ok(
        utilisateurPromotionChambreService.getById(utilisateurId, promotionId, chambreId));
  }

  @PreAuthorize("hasAnyRole('MANAGER')")
  @PostMapping("/")
  public ResponseEntity<?> save(@RequestBody @Valid UtilisateurPromotionChambreDto dto) {
    return ResponseEntity.ok(utilisateurPromotionChambreService.save(dto));
  }

  @PreAuthorize("hasAnyRole('MANAGER')")
  @PutMapping("/")
  public ResponseEntity<?> update(
      @RequestBody @Valid UtilisateurPromotionChambreUpdateRequest request) {
    return ResponseEntity.ok(
        utilisateurPromotionChambreService.update(request.id(), request.dto()));
  }

  @PreAuthorize("hasAnyRole('RESPONSABLE')")
  @DeleteMapping("/")
  @ResponseStatus(HttpStatus.OK)
  public void delete(
      @RequestParam UUID utilisateurId,
      @RequestParam UUID promotionId,
      @RequestParam UUID chambreId) {
    utilisateurPromotionChambreService.delete(utilisateurId, promotionId, chambreId);
  }
}
