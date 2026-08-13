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
import org.aminesidki.resiaiac.dto.BatimentDto;
import org.aminesidki.resiaiac.dto.request.BatimentUpdateRequest;
import org.aminesidki.resiaiac.service.BatimentService;
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
 * Unit tests for {@link BatimentController}, exercised through {@link MockMvc} against a sliced web
 * context.
 *
 * <p>{@link BatimentService} is mocked; only request routing, (de)serialization, and status codes
 * are under test here — business logic is covered separately by {@code BatimentServiceTest}.
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
@WebMvcTest(BatimentController.class)
@AutoConfigureMockMvc(addFilters = false)
class BatimentControllerTest {

  @Autowired private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @MockitoBean private BatimentService batimentService;

  private UUID id;
  private BatimentDto dto;

  @BeforeEach
  void setUp() {
    id = UUID.randomUUID();
    dto = new BatimentDto(id, "Batiment A", List.of());
  }

  // ---------- getById ----------

  @Test
  void getById_shouldReturnDto() throws Exception {
    when(batimentService.getById(id)).thenReturn(dto);

    mockMvc
        .perform(get("/api/v1/batiment/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.nom").value("Batiment A"));

    verify(batimentService).getById(id);
    verifyNoMoreInteractions(batimentService);
  }

  // ---------- save ----------

  @Test
  void save_shouldPersistAndReturnDto() throws Exception {
    BatimentDto inputDto = new BatimentDto(null, "Nouveau Batiment", List.of());
    BatimentDto resultDto = new BatimentDto(id, "Nouveau Batiment", List.of());

    when(batimentService.save(inputDto)).thenReturn(resultDto);

    mockMvc
        .perform(
            post("/api/v1/batiment/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.nom").value("Nouveau Batiment"));

    verify(batimentService).save(inputDto);
    verifyNoMoreInteractions(batimentService);
  }

  // ---------- update ----------

  @Test
  void update_shouldMutateAndReturnDto() throws Exception {
    BatimentUpdateRequest request = new BatimentUpdateRequest(id, dto);
    BatimentDto resultDto = new BatimentDto(id, "Batiment A - renamed", List.of());

    when(batimentService.update(id, dto)).thenReturn(resultDto);

    mockMvc
        .perform(
            put("/api/v1/batiment/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.nom").value("Batiment A - renamed"));

    verify(batimentService).update(id, dto);
    verifyNoMoreInteractions(batimentService);
  }

  // ---------- delete ----------

  @Test
  void delete_shouldReturnOkAndInvokeService() throws Exception {
    mockMvc.perform(delete("/api/v1/batiment/{id}", id)).andExpect(status().isOk());

    verify(batimentService, times(1)).delete(id);
    verifyNoMoreInteractions(batimentService);
  }
}
