package org.aminesidki.resiaiac.controller;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.PromotionDto;
import org.aminesidki.resiaiac.dto.request.PromotionUpdateRequest;
import org.aminesidki.resiaiac.service.PromotionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/promotion")
@RequiredArgsConstructor
public class PromotionController {
  private final PromotionService promotionService;

  @GetMapping("/{id}")
  public ResponseEntity<?> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(promotionService.getById(id));
  }

  @PostMapping("/")
  public ResponseEntity<?> save(@RequestBody PromotionDto dto) {
    return ResponseEntity.ok(promotionService.save(dto));
  }

  @PutMapping("/")
  public ResponseEntity<?> update(@RequestBody PromotionUpdateRequest request) {
    return ResponseEntity.ok(promotionService.update(request.id(), request.dto()));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void delete(@PathVariable UUID id) {
    promotionService.delete(id);
  }
}
