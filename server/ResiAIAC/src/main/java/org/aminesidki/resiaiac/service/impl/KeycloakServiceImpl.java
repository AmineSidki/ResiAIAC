package org.aminesidki.resiaiac.service.impl;

import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.Collections;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aminesidki.resiaiac.dto.UtilisateurDto;
import org.aminesidki.resiaiac.exception.CreatedKeycloakUserIdExtractionFail;
import org.aminesidki.resiaiac.exception.KeycloakUserCreationException;
import org.aminesidki.resiaiac.exception.KeycloakUserDeletionException;
import org.aminesidki.resiaiac.service.KeycloakService;
import org.aminesidki.resiaiac.util.StringUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakServiceImpl implements KeycloakService {
  @Value("${keycloak.admin.realm}")
  private String realm;

  private final Keycloak keycloak;

  private UUID extractUserIdFromUri(URI uri) {
    if (uri == null) return null;

    try {
      String id = uri.toString().substring(uri.toString().lastIndexOf('/') + 1);
      return UUID.fromString(id);
    } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
      return null;
    }
  }

  @Override
  public UUID createUser(UtilisateurDto dto) {
    String username = StringUtil.nameToUsername(dto.nom(), dto.prenom());

    UserRepresentation userRepresentation = new UserRepresentation();
    userRepresentation.setLastName(dto.prenom());
    userRepresentation.setFirstName(dto.nom());
    userRepresentation.setEnabled(true);
    userRepresentation.setUsername(username);
    userRepresentation.setEmail(dto.email());
    userRepresentation.setEmailVerified(true);

    CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
    credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
    credentialRepresentation.setValue(username.toLowerCase());
    credentialRepresentation.setTemporary(false);

    userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));

    UsersResource usersResource = keycloak.realm(realm).users();
    try (Response response = usersResource.create(userRepresentation)) {
      if (response.getStatus() != HttpStatus.CREATED.value()) {
        throw new KeycloakUserCreationException(
            "Failed to create keycloak user with username " + username);
      }

      UUID keycloakId = extractUserIdFromUri(response.getLocation());
      if (keycloakId == null) {
        throw new CreatedKeycloakUserIdExtractionFail(
            "Failed to extract id for created user with username " + username);
      }

      log.info(
          "Created user with username {} successfully ! Assigned keycloak ID : {}",
          username,
          keycloakId);
      return keycloakId;
    }
  }

  @Override
  public void deleteUser(UUID id) {
    UsersResource usersResource = keycloak.realm(realm).users();
    try (Response response = usersResource.delete(id.toString())) {
      if (response.getStatus() != HttpStatus.NO_CONTENT.value()) {
        throw new KeycloakUserDeletionException("Failed to delete keycloak user with id " + id);
      }

      log.info("Deleted user with id {} successfully !", id);
    }
  }
}
