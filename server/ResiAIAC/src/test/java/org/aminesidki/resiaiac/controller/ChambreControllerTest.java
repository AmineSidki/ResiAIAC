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

import java.util.List;
import java.util.UUID;
import org.aminesidki.resiaiac.dto.ChambreDto;
import org.aminesidki.resiaiac.dto.request.ChambreUpdateRequest;
import org.aminesidki.resiaiac.enumeration.EtatChambre;
import org.aminesidki.resiaiac.service.ChambreService;
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
 * Unit tests for {@link ChambreController}, exercised through {@link MockMvc} against a sliced web
 * context.
 *
 * <p>{@link ChambreService} is mocked; only request routing, (de)serialization, and status codes
 * are under test here — business logic is covered separately by {@code ChambreServiceTest}.
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
 * <p>{@code etat} is left {@code null} in test fixtures since {@link EtatChambre}'s exact constant
 * names aren't part of this test's dependencies; substitute a specific value (e.g. {@code
 * EtatChambre.LIBRE}) if one is more meaningful for a given scenario.
 */
@WebMvcTest(ChambreController.class)
@AutoConfigureMockMvc(addFilters = false)
class ChambreControllerTest {

  @Autowired private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @MockitoBean private ChambreService chambreService;

  private UUID id;
  private UUID etageId;
  private ChambreDto dto;

  @BeforeEach
  void setUp() {
    id = UUID.randomUUID();
    etageId = UUID.randomUUID();
    dto = new ChambreDto(id, "CH-101", 2L, null, List.of(), List.of(), List.of(), etageId);
  }

  // ---------- getById ----------

  @Test
  void getById_shouldReturnDto() throws Exception {
    when(chambreService.getById(id)).thenReturn(dto);

    mockMvc
        .perform(get("/api/v1/chambre/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.matricule").value("CH-101"))
        .andExpect(jsonPath("$.capacite").value(2));

    verify(chambreService).getById(id);
    verifyNoMoreInteractions(chambreService);
  }

  // ---------- save ----------

  @Test
  void save_shouldPersistAndReturnDto() throws Exception {
    ChambreDto inputDto =
        new ChambreDto(null, "CH-202", 4L, null, List.of(), List.of(), List.of(), etageId);
    ChambreDto resultDto =
        new ChambreDto(id, "CH-202", 4L, null, List.of(), List.of(), List.of(), etageId);

    when(chambreService.save(inputDto)).thenReturn(resultDto);

    mockMvc
        .perform(
            post("/api/v1/chambre/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.matricule").value("CH-202"));

    verify(chambreService).save(inputDto);
    verifyNoMoreInteractions(chambreService);
  }

  // ---------- update ----------

  @Test
  void update_shouldMutateAndReturnDto() throws Exception {
    ChambreUpdateRequest request = new ChambreUpdateRequest(id, dto);
    ChambreDto resultDto =
        new ChambreDto(id, "CH-101 - renamed", 2L, null, List.of(), List.of(), List.of(), etageId);

    when(chambreService.update(id, dto)).thenReturn(resultDto);

    mockMvc
        .perform(
            put("/api/v1/chambre/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.matricule").value("CH-101 - renamed"));

    verify(chambreService).update(id, dto);
    verifyNoMoreInteractions(chambreService);
  }

  // ---------- delete ----------

  @Test
  void delete_shouldReturnOkAndInvokeService() throws Exception {
    mockMvc.perform(delete("/api/v1/chambre/{id}", id)).andExpect(status().isOk());

    verify(chambreService, times(1)).delete(id);
    verifyNoMoreInteractions(chambreService);
  }
}
