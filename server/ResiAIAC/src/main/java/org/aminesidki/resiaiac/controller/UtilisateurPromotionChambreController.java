package org.aminesidki.resiaiac.controller;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.UtilisateurPromotionChambreDto;
import org.aminesidki.resiaiac.dto.request.UtilisateurPromotionChambreUpdateRequest;
import org.aminesidki.resiaiac.service.UtilisateurPromotionChambreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/upc")
@RequiredArgsConstructor
public class UtilisateurPromotionChambreController {

  private final UtilisateurPromotionChambreService utilisateurPromotionChambreService;

  @GetMapping("/")
  public ResponseEntity<?> getById(
      @RequestParam UUID utilisateurId,
      @RequestParam UUID promotionId,
      @RequestParam UUID chambreId) {
    return ResponseEntity.ok(
        utilisateurPromotionChambreService.getById(utilisateurId, promotionId, chambreId));
  }

  @PostMapping("/")
  public ResponseEntity<?> save(@RequestBody UtilisateurPromotionChambreDto dto) {
    return ResponseEntity.ok(utilisateurPromotionChambreService.save(dto));
  }

  @PutMapping("/")
  public ResponseEntity<?> update(@RequestBody UtilisateurPromotionChambreUpdateRequest request) {
    return ResponseEntity.ok(
        utilisateurPromotionChambreService.update(request.id(), request.dto()));
  }

  @DeleteMapping("/")
  @ResponseStatus(HttpStatus.OK)
  public void delete(
      @RequestParam UUID utilisateurId,
      @RequestParam UUID promotionId,
      @RequestParam UUID chambreId) {
    utilisateurPromotionChambreService.delete(utilisateurId, promotionId, chambreId);
  }
}
