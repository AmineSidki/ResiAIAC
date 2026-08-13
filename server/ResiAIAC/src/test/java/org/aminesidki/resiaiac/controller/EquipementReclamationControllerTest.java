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

import java.util.UUID;
import org.aminesidki.resiaiac.dto.EquipementReclamationDto;
import org.aminesidki.resiaiac.dto.request.EquipementReclamationRequest;
import org.aminesidki.resiaiac.entity.id.EquipementReclamationId;
import org.aminesidki.resiaiac.service.EquipementReclamationService;
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
 * Unit tests for {@link EquipementReclamationController}, exercised through {@link MockMvc} against
 * a sliced web context.
 *
 * <p>{@link EquipementReclamationService} is mocked; only request routing, (de)serialization, and
 * status codes are under test here — business logic is covered separately by {@code
 * EquipementReclamationServiceTest}.
 *
 * <p>Unlike the other controllers in this module, {@code getById}/{@code delete} take the composite
 * key's parts as query params ({@code equipementId}, {@code reclamationId}) rather than a path
 * variable, since {@link EquipementReclamationId} has no single-value representation.
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
@WebMvcTest(EquipementReclamationController.class)
@AutoConfigureMockMvc(addFilters = false)
class EquipementReclamationControllerTest {

  private static final String BASE_PATH = "/api/v1/equipement-reclamation";

  @Autowired private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @MockitoBean private EquipementReclamationService equipementReclamationService;

  private Long equipementId;
  private UUID reclamationId;
  private EquipementReclamationId id;
  private EquipementReclamationDto dto;

  @BeforeEach
  void setUp() {
    equipementId = 1L;
    reclamationId = UUID.randomUUID();
    id = new EquipementReclamationId(equipementId, reclamationId);
    dto = new EquipementReclamationDto(id, 5L, equipementId, reclamationId);
  }

  // ---------- getById ----------

  @Test
  void getById_shouldReturnDto() throws Exception {
    when(equipementReclamationService.getById(equipementId, reclamationId)).thenReturn(dto);

    mockMvc
        .perform(
            get(BASE_PATH + "/")
                .param("equipementId", equipementId.toString())
                .param("reclamationId", reclamationId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.quantite").value(5))
        .andExpect(jsonPath("$.equipement").value(equipementId))
        .andExpect(jsonPath("$.reclamation").value(reclamationId.toString()));

    verify(equipementReclamationService).getById(equipementId, reclamationId);
    verifyNoMoreInteractions(equipementReclamationService);
  }

  // ---------- save ----------

  @Test
  void save_shouldPersistAndReturnDto() throws Exception {
    UUID otherReclamationId = UUID.randomUUID();
    EquipementReclamationDto inputDto =
        new EquipementReclamationDto(null, 10L, 2L, otherReclamationId);
    EquipementReclamationDto resultDto =
        new EquipementReclamationDto(id, 10L, equipementId, reclamationId);

    when(equipementReclamationService.save(inputDto)).thenReturn(resultDto);

    mockMvc
        .perform(
            post(BASE_PATH + "/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.quantite").value(10))
        .andExpect(jsonPath("$.equipement").value(equipementId));

    verify(equipementReclamationService).save(inputDto);
    verifyNoMoreInteractions(equipementReclamationService);
  }

  // ---------- update ----------

  @Test
  void update_shouldMutateAndReturnDto() throws Exception {
    EquipementReclamationRequest request = new EquipementReclamationRequest(id, dto);
    EquipementReclamationDto resultDto =
        new EquipementReclamationDto(id, 7L, equipementId, reclamationId);

    when(equipementReclamationService.update(id, dto)).thenReturn(resultDto);

    mockMvc
        .perform(
            put(BASE_PATH + "/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.quantite").value(7));

    verify(equipementReclamationService).update(id, dto);
    verifyNoMoreInteractions(equipementReclamationService);
  }

  // ---------- delete ----------

  @Test
  void delete_shouldReturnOkAndInvokeService() throws Exception {
    mockMvc
        .perform(
            delete(BASE_PATH + "/")
                .param("equipementId", equipementId.toString())
                .param("reclamationId", reclamationId.toString()))
        .andExpect(status().isOk());

    verify(equipementReclamationService, times(1)).delete(equipementId, reclamationId);
    verifyNoMoreInteractions(equipementReclamationService);
  }
}
