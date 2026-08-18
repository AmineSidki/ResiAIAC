package org.aminesidki.resiaiac.configuration;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakAdminConfiguration {

  @Value("${keycloak.admin.server-url}")
  private String serverUrl;

  @Value("${keycloak.admin.realm}")
  private String realm;

  @Value("${keycloak.admin.client-id}")
  private String clientId;

  @Value("${keycloak.admin.secret}")
  private String clientSecret;

  @Bean
  public Keycloak keycloakAdminClient() {
    return KeycloakBuilder.builder()
        .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
        .serverUrl(serverUrl)
        .realm(realm)
        .clientId(clientId)
        .clientSecret(clientSecret)
        .build();
  }
}
