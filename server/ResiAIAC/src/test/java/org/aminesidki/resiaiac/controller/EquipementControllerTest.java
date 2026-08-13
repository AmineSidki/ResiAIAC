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
import org.aminesidki.resiaiac.dto.EquipementDto;
import org.aminesidki.resiaiac.dto.request.EquipementUpdateRequest;
import org.aminesidki.resiaiac.service.EquipementService;
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
 * Unit tests for {@link EquipementController}, exercised through {@link MockMvc} against a sliced
 * web context.
 *
 * <p>{@link EquipementService} is mocked; only request routing, (de)serialization, and status codes
 * are under test here — business logic is covered separately by {@code EquipementServiceTest}.
 *
 * <p>Unlike most other controllers in this module, Equipement uses a {@code Long} id rather than
 * {@code UUID}.
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
@WebMvcTest(EquipementController.class)
@AutoConfigureMockMvc(addFilters = false)
class EquipementControllerTest {

  private static final String BASE_PATH = "/api/v1/equipement";

  @Autowired private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @MockitoBean private EquipementService equipementService;

  private Long id;
  private EquipementDto dto;

  @BeforeEach
  void setUp() {
    id = 1L;
    dto = new EquipementDto(id, "Climatiseur", List.of(), List.of());
  }

  // ---------- getById ----------

  @Test
  void getById_shouldReturnDto() throws Exception {
    when(equipementService.getById(id)).thenReturn(dto);

    mockMvc
        .perform(get(BASE_PATH + "/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.nom").value("Climatiseur"));

    verify(equipementService).getById(id);
    verifyNoMoreInteractions(equipementService);
  }

  // ---------- save ----------

  @Test
  void save_shouldPersistAndReturnDto() throws Exception {
    EquipementDto inputDto = new EquipementDto(null, "Climatiseur", List.of(), List.of());
    EquipementDto resultDto = new EquipementDto(id, "Climatiseur", List.of(), List.of());

    when(equipementService.save(inputDto)).thenReturn(resultDto);

    mockMvc
        .perform(
            post(BASE_PATH + "/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.nom").value("Climatiseur"));

    verify(equipementService).save(inputDto);
    verifyNoMoreInteractions(equipementService);
  }

  // ---------- update ----------

  @Test
  void update_shouldMutateAndReturnDto() throws Exception {
    EquipementUpdateRequest request = new EquipementUpdateRequest(id, dto);
    EquipementDto resultDto = new EquipementDto(id, "Climatiseur - renamed", List.of(), List.of());

    when(equipementService.update(id, dto)).thenReturn(resultDto);

    mockMvc
        .perform(
            put(BASE_PATH + "/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.nom").value("Climatiseur - renamed"));

    verify(equipementService).update(id, dto);
    verifyNoMoreInteractions(equipementService);
  }

  // ---------- delete ----------

  @Test
  void delete_shouldReturnOkAndInvokeService() throws Exception {
    mockMvc.perform(delete(BASE_PATH + "/{id}", id)).andExpect(status().isOk());

    verify(equipementService, times(1)).delete(id);
    verifyNoMoreInteractions(equipementService);
  }
}
