package org.aminesidki.resiaiac.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("test")
@RestController
@RequestMapping("/api/v1/auth-test")
public class AuthTestController {
  @GetMapping("/public")
  public String publicRoute() {
    return "Hello world !";
  }

  @GetMapping("/private")
  public String privateRoute() {
    return "Hello, you are authenticated !";
  }

  @PreAuthorize("hasAuthority('ROLE_ADMINISTRATEUR')")
  @GetMapping("/private/admin")
  public String privateAdminRoute() {
    return "Hello, administrator !";
  }

  @PreAuthorize("hasRole('ADMINISTRATEUR')")
  @GetMapping("/private/admin/username")
  public String privateAdminRouteTestCredentialExtraction(@AuthenticationPrincipal Jwt jwt) {
    return "Hello, " + jwt.getClaimAsString("preferred_username") + " !";
  }
}
