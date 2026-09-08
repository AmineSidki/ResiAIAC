package org.aminesidki.resiaiac.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.UUID;
import org.aminesidki.resiaiac.dto.UtilisateurDto;
import org.aminesidki.resiaiac.enumeration.Role;
import org.aminesidki.resiaiac.exception.CreatedKeycloakUserIdExtractionFail;
import org.aminesidki.resiaiac.exception.KeycloakUserCreationException;
import org.aminesidki.resiaiac.exception.KeycloakUserDeletionException;
import org.aminesidki.resiaiac.service.impl.KeycloakServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RoleScopeResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Unit tests for {@link KeycloakService}.
 *
 * <p>The {@code Keycloak} / {@code RealmResource} / {@code UsersResource} / {@code Response} chain
 * is mocked directly rather than exercised against a real server. This is enough to lock in the
 * current implementation's logic (status-code handling, UUID extraction from the {@code Location}
 * header) but won't catch a mismatch against what a real Keycloak instance actually returns — worth
 * a Testcontainers-backed integration test later if that risk matters for this project.
 *
 * <p>{@code realm} is injected via {@link ReflectionTestUtils} because {@code KeycloakServiceImpl}
 * populates it through {@code @Value}, which is not available in a plain unit test.
 */
@ExtendWith(MockitoExtension.class)
class KeycloakServiceTest {

  private static final String REALM = "resiaiac";

  @Mock private Keycloak keycloak;
  @Mock private RealmResource realmResource;
  @Mock private UsersResource usersResource;
  @Mock private Response response;
  @Mock private RolesResource rolesResource;
  @Mock private RoleResource roleResource;
  @Mock private UserResource userResource;
  @Mock private RoleMappingResource roleMappingResource;
  @Mock private RoleScopeResource roleScopeResource;

  private KeycloakService keycloakService; // typed as the interface

  private UtilisateurDto dto;

  @BeforeEach
  void setUp() {
    KeycloakServiceImpl impl = new KeycloakServiceImpl(keycloak);
    ReflectionTestUtils.setField(impl, "realm", REALM);
    keycloakService = impl;

    dto =
        new UtilisateurDto(
            null,
            Role.ETUDIANT,
            "amine.sidki@example.com",
            "Sidki",
            "Amine",
            null,
            null,
            null,
            null,
            null,
            null);

    when(keycloak.realm(REALM)).thenReturn(realmResource);
    when(realmResource.users()).thenReturn(usersResource);
  }

  /**
   * {@code createUser} looks up the Keycloak role for the DTO's {@code role} right after checking
   * the creation status, before it even extracts the new user's id. Every test whose {@code
   * response.getStatus()} is 201 exercises this lookup, so it needs to be stubbed or the call
   * throws a NullPointerException instead of the exception the test is actually asserting on.
   */
  private void stubRoleLookup() {
    RoleRepresentation roleRepresentation = new RoleRepresentation();
    roleRepresentation.setName(Role.ETUDIANT.name());

    when(realmResource.roles()).thenReturn(rolesResource);
    when(rolesResource.get(Role.ETUDIANT.name())).thenReturn(roleResource);
    when(roleResource.toRepresentation()).thenReturn(roleRepresentation);
  }

  // ---------- createUser ----------

  @Test
  void createUser_shouldReturnKeycloakIdOnSuccess() {
    UUID expectedId = UUID.randomUUID();
    stubRoleLookup();
    when(usersResource.create(any())).thenReturn(response);
    when(response.getStatus()).thenReturn(201);
    when(response.getLocation())
        .thenReturn(URI.create("http://localhost:8080/admin/realms/resiaiac/users/" + expectedId));
    when(usersResource.get(expectedId.toString())).thenReturn(userResource);
    when(userResource.roles()).thenReturn(roleMappingResource);
    when(roleMappingResource.realmLevel()).thenReturn(roleScopeResource);

    UUID result = keycloakService.createUser(dto);

    assertThat(result).isEqualTo(expectedId);
    verify(usersResource).create(any());
    verify(roleScopeResource).add(any());
  }

  @Test
  void createUser_shouldThrowWhenStatusIsNotCreated() {
    when(usersResource.create(any())).thenReturn(response);
    when(response.getStatus()).thenReturn(409); // e.g. username already taken

    assertThatThrownBy(() -> keycloakService.createUser(dto))
        .isInstanceOf(KeycloakUserCreationException.class);
  }

  @Test
  void createUser_shouldThrowWhenLocationIdIsNotAValidUuid() {
    stubRoleLookup();
    when(usersResource.create(any())).thenReturn(response);
    when(response.getStatus()).thenReturn(201);
    when(response.getLocation())
        .thenReturn(URI.create("http://localhost:8080/admin/realms/resiaiac/users/not-a-uuid"));

    assertThatThrownBy(() -> keycloakService.createUser(dto))
        .isInstanceOf(CreatedKeycloakUserIdExtractionFail.class);
  }

  @Test
  void createUser_shouldThrowWhenLocationHeaderIsMissing() {
    stubRoleLookup();
    when(usersResource.create(any())).thenReturn(response);
    when(response.getStatus()).thenReturn(201);
    when(response.getLocation()).thenReturn(null);

    assertThatThrownBy(() -> keycloakService.createUser(dto))
        .isInstanceOf(CreatedKeycloakUserIdExtractionFail.class);
  }

  // ---------- deleteUser ----------

  @Test
  void deleteUser_shouldSucceedOnNoContent() {
    UUID id = UUID.randomUUID();
    when(usersResource.delete(id.toString())).thenReturn(response);
    when(response.getStatus()).thenReturn(204);

    keycloakService.deleteUser(id);

    verify(usersResource).delete(id.toString());
  }

  @Test
  void deleteUser_shouldThrowWhenStatusIsNotNoContent() {
    UUID id = UUID.randomUUID();
    when(usersResource.delete(id.toString())).thenReturn(response);
    when(response.getStatus()).thenReturn(404);

    assertThatThrownBy(() -> keycloakService.deleteUser(id))
        .isInstanceOf(KeycloakUserDeletionException.class);
  }
}
