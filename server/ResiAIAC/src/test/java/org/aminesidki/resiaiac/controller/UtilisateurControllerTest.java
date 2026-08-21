package org.aminesidki.resiaiac.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;
import org.aminesidki.resiaiac.dto.UtilisateurDto;
import org.aminesidki.resiaiac.dto.request.UpdateMeRequest;
import org.aminesidki.resiaiac.dto.request.UtilisateurUpdateRequest;
import org.aminesidki.resiaiac.service.UtilisateurService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

/**
 * Unit tests for {@link UtilisateurController}, exercised through {@link MockMvc} against a sliced
 * web context.
 *
 * <p>{@link UtilisateurService} is mocked; only request routing, (de)serialization, and status
 * codes are under test here — business logic is covered separately by {@code
 * UtilisateurServiceTest}.
 *
 * <p>Security filters are disabled ({@code addFilters = false}) since Keycloak-backed
 * authentication is out of scope for these tests; adjust if endpoint-level authorization rules need
 * coverage too.
 *
 * <p>{@code ObjectMapper} is instantiated directly rather than {@code @Autowired}, to keep this
 * test independent of how Jackson auto-configuration wires beans in the app context.
 *
 * <p>Uses {@code @MockitoBean} (Spring Framework's {@code spring-test}), not the removed Spring
 * Boot {@code @MockBean}.
 */
@WebMvcTest(UtilisateurController.class)
@AutoConfigureMockMvc(addFilters = false)
class UtilisateurControllerTest {

  private static final String BASE_PATH = "/api/v1/utilisateur";

  @Autowired private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @MockitoBean private UtilisateurService utilisateurService;

  private UUID id;
  private UtilisateurDto dto;

  @BeforeEach
  void setUp() {
    id = UUID.randomUUID();
    dto =
        new UtilisateurDto(
            id,
            "Sidki",
            "Amine",
            "AB123456",
            "Casablanca",
            "0600000000",
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            null,
            null);
  }

  // ---------- getById ----------

  @Test
  void getById_shouldReturnDto() throws Exception {
    when(utilisateurService.getById(id)).thenReturn(dto);

    mockMvc
        .perform(get(BASE_PATH + "/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.nom").value("Sidki"))
        .andExpect(jsonPath("$.prenom").value("Amine"));

    verify(utilisateurService).getById(id);
    verifyNoMoreInteractions(utilisateurService);
  }

  // ---------- save ----------

  @Test
  void save_shouldPersistAndReturnDto() throws Exception {
    UtilisateurDto inputDto =
        new UtilisateurDto(
            null,
            "Sidki",
            "Amine",
            "AB123456",
            "Casablanca",
            "0600000000",
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            null,
            null);
    UtilisateurDto resultDto =
        new UtilisateurDto(
            id,
            "Sidki",
            "Amine",
            "AB123456",
            "Casablanca",
            "0600000000",
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            null,
            null);

    when(utilisateurService.save(inputDto)).thenReturn(resultDto);

    mockMvc
        .perform(
            post(BASE_PATH + "/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.nom").value("Sidki"));

    verify(utilisateurService).save(inputDto);
    verifyNoMoreInteractions(utilisateurService);
  }

  // ---------- update ----------

  @Test
  void update_shouldMutateAndReturnDto() throws Exception {
    UtilisateurUpdateRequest request = new UtilisateurUpdateRequest(id, dto);
    UtilisateurDto resultDto =
        new UtilisateurDto(
            id,
            "Sidki",
            "Amine - renamed",
            "AB123456",
            "Casablanca",
            "0600000000",
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            null,
            null);

    when(utilisateurService.update(id, dto)).thenReturn(resultDto);

    mockMvc
        .perform(
            put(BASE_PATH + "/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.prenom").value("Amine - renamed"));

    verify(utilisateurService).update(id, dto);
    verifyNoMoreInteractions(utilisateurService);
  }

  // ---------- delete ----------

  @Test
  void delete_shouldReturnOkAndInvokeService() throws Exception {
    mockMvc.perform(delete(BASE_PATH + "/{id}", id)).andExpect(status().isOk());

    verify(utilisateurService, times(1)).delete(id);
    verifyNoMoreInteractions(utilisateurService);
  }

  // ---------- getMe ----------

  @Test
  void getMe_shouldReturnCallersDto() throws Exception {
    when(utilisateurService.getMyDto(any())).thenReturn(dto);

    mockMvc
        .perform(get(BASE_PATH + "/me").with(jwt()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.nom").value("Sidki"));

    verify(utilisateurService).getMyDto(any());
    verifyNoMoreInteractions(utilisateurService);
  }

  // ---------- updateMe ----------

  @Test
  void updateMe_shouldMutateAndReturnDto() throws Exception {
    UpdateMeRequest request = new UpdateMeRequest("Rabat", "+212600000000");
    UtilisateurDto resultDto =
        new UtilisateurDto(
            id,
            "Sidki",
            "Amine",
            "AB123456",
            "Rabat",
            "+212600000000",
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            null,
            null);

    when(utilisateurService.updateMe(any(), eq(request))).thenReturn(resultDto);

    mockMvc
        .perform(
            put(BASE_PATH + "/me")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.adresse").value("Rabat"));

    verify(utilisateurService).updateMe(any(), eq(request));
    verifyNoMoreInteractions(utilisateurService);
  }

  @Test
  void updateMe_shouldReturnBadRequestForInvalidPhoneNumber() throws Exception {
    // "telephone" must match the phone @Pattern on UpdateMeRequest — this must be rejected
    // before the service layer is ever reached.
    String bodyWithInvalidPhone = "{\"adresse\":\"Rabat\",\"telephone\":\"not-a-phone-number\"}";

    mockMvc
        .perform(
            put(BASE_PATH + "/me")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyWithInvalidPhone))
        .andExpect(status().isBadRequest());

    verifyNoMoreInteractions(utilisateurService);
  }
}
