package org.aminesidki.resiaiac.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import org.aminesidki.resiaiac.dto.ReclamationDto;
import org.aminesidki.resiaiac.dto.request.ReclamationUpdateRequest;
import org.aminesidki.resiaiac.enumeration.EtatReclamation;
import org.aminesidki.resiaiac.service.ReclamationService;
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
 * Unit tests for {@link ReclamationController}, exercised through {@link MockMvc} against a sliced
 * web context.
 *
 * <p>{@link ReclamationService} is mocked; only request routing, (de)serialization, and status
 * codes are under test here — business logic is covered separately by {@code
 * ReclamationServiceTest}.
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
 *
 * <p>{@code etat} is left {@code null} in test fixtures since {@link EtatReclamation}'s exact
 * constant names aren't part of this test's dependencies; substitute a specific value (e.g. {@code
 * EtatReclamation.EN_ATTENTE}) if one is more meaningful for a given scenario. {@code updatedAt} is
 * likewise left {@code null} — only {@code createdAt} is exercised here.
 */
@WebMvcTest(ReclamationController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReclamationControllerTest {

  @Autowired private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @MockitoBean private ReclamationService reclamationService;

  private UUID id;
  private UUID utilisateurId;
  private UUID chambreId;
  private Long serviceId;
  private Timestamp createdAt;
  private ReclamationDto dto;

  @BeforeEach
  void setUp() {
    id = UUID.randomUUID();
    utilisateurId = UUID.randomUUID();
    chambreId = UUID.randomUUID();
    serviceId = 1L;
    createdAt = new Timestamp(System.currentTimeMillis());
    dto =
        new ReclamationDto(
            id, "Fuite d'eau", null, utilisateurId, chambreId, serviceId, List.of(), null, null);
  }

  // ---------- getById ----------

  @Test
  void getById_shouldReturnDto() throws Exception {
    when(reclamationService.getById(id)).thenReturn(dto);

    mockMvc
        .perform(get("/api/v1/reclamation/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.message").value("Fuite d'eau"));

    verify(reclamationService).getById(id);
    verifyNoMoreInteractions(reclamationService);
  }

  // ---------- save ----------

  @Test
  void save_shouldPersistAndReturnDto() throws Exception {
    ReclamationDto inputDto =
        new ReclamationDto(
            null,
            "Chauffage en panne",
            null,
            utilisateurId,
            chambreId,
            serviceId,
            List.of(),
            null,
            null);
    ReclamationDto resultDto =
        new ReclamationDto(
            id,
            "Chauffage en panne",
            null,
            utilisateurId,
            chambreId,
            serviceId,
            List.of(),
            createdAt,
            null);

    when(reclamationService.save(inputDto)).thenReturn(resultDto);

    mockMvc
        .perform(
            post("/api/v1/reclamation/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.message").value("Chauffage en panne"));

    verify(reclamationService).save(inputDto);
    verifyNoMoreInteractions(reclamationService);
  }

  // ---------- update ----------

  @Test
  void update_shouldMutateAndReturnDto() throws Exception {
    ReclamationUpdateRequest request = new ReclamationUpdateRequest(id, dto);
    ReclamationDto resultDto =
        new ReclamationDto(
            id,
            "Fuite d'eau - resolue",
            null,
            utilisateurId,
            chambreId,
            serviceId,
            List.of(),
            null,
            null);

    when(reclamationService.update(id, dto)).thenReturn(resultDto);

    mockMvc
        .perform(
            put("/api/v1/reclamation/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.message").value("Fuite d'eau - resolue"));

    verify(reclamationService).update(id, dto);
    verifyNoMoreInteractions(reclamationService);
  }

  // ---------- delete ----------

  @Test
  void delete_shouldReturnOkAndInvokeService() throws Exception {
    mockMvc.perform(delete("/api/v1/reclamation/{id}", id)).andExpect(status().isOk());

    verify(reclamationService, times(1)).delete(id);
    verifyNoMoreInteractions(reclamationService);
  }
}
