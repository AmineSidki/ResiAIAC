package org.aminesidki.resiaiac.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

class JwtAuthenticationConverterConfigurationTest {

  private final JwtAuthenticationConverterConfiguration configuration =
      new JwtAuthenticationConverterConfiguration();

  private Jwt.Builder baseJwt() {
    return Jwt.withTokenValue("token")
        .header("alg", "none")
        .issuedAt(Instant.now())
        .expiresAt(Instant.now().plusSeconds(60))
        .claim(OAuth2TokenIntrospectionClaimNames.SUB, "user-id");
  }

  @Test
  void mapsRealmAccessRolesToPrefixedAuthorities() {
    Jwt jwt =
        baseJwt()
            .claim("realm_access", Map.of("roles", List.of("ADMINISTRATEUR", "ETUDIANT")))
            .build();

    JwtAuthenticationConverter converter = configuration.jwtAuthenticationConverter();

    Collection<GrantedAuthority> authorities = converter.convert(jwt).getAuthorities();

    assertThat(authorities)
        .contains(
            new SimpleGrantedAuthority("ROLE_ADMINISTRATEUR"),
            new SimpleGrantedAuthority("ROLE_ETUDIANT"));
  }

  @Test
  void returnsNoAuthoritiesWhenRealmAccessHasNoRolesEntry() {
    Jwt jwt = baseJwt().claim("realm_access", Map.of()).build();

    JwtAuthenticationConverter converter = configuration.jwtAuthenticationConverter();

    assertThat(converter.convert(jwt).getAuthorities())
        .doesNotContainAnyElementsOf(
            List.of(
                new SimpleGrantedAuthority("ROLE_ADMINISTRATEUR"),
                new SimpleGrantedAuthority("ROLE_RESPONSABLE"),
                new SimpleGrantedAuthority("ROLE_MANAGER"),
                new SimpleGrantedAuthority("ROLE_ETUDIANT")));
  }
}
