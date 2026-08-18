package org.aminesidki.resiaiac.controller.test;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.UtilisateurDto;
import org.aminesidki.resiaiac.service.KeycloakService;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Profile("test")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/keycloak-service-test")
public class KeycloakServiceTestController {
  private final KeycloakService keycloakService;

  @PostMapping("/public/create/")
  public ResponseEntity<?> createUtilisateur(@RequestBody UtilisateurDto dto) {
    return ResponseEntity.ok(keycloakService.createUser(dto));
  }

  @PostMapping("/public/delete/")
  @ResponseStatus(HttpStatus.OK)
  public void deleteUtilisateur(@RequestBody UUID id) {
    keycloakService.deleteUser(id);
  }
}
