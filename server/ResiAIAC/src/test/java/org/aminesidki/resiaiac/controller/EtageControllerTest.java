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
import org.aminesidki.resiaiac.dto.EtageDto;
import org.aminesidki.resiaiac.dto.request.EtageUpdateRequest;
import org.aminesidki.resiaiac.service.EtageService;
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
 * Unit tests for {@link EtageController}, exercised through {@link MockMvc} against a sliced web
 * context.
 *
 * <p>{@link EtageService} is mocked; only request routing, (de)serialization, and status codes are
 * under test here — business logic is covered separately by {@code EtageServiceTest}.
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
@WebMvcTest(EtageController.class)
@AutoConfigureMockMvc(addFilters = false)
class EtageControllerTest {

  @Autowired private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @MockitoBean private EtageService etageService;

  private UUID id;
  private UUID batimentId;
  private EtageDto dto;

  @BeforeEach
  void setUp() {
    id = UUID.randomUUID();
    batimentId = UUID.randomUUID();
    dto = new EtageDto(id, "1", batimentId, List.of());
  }

  // ---------- getById ----------

  @Test
  void getById_shouldReturnDto() throws Exception {
    when(etageService.getById(id)).thenReturn(dto);

    mockMvc
        .perform(get("/api/v1/etage/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.numero").value("1"));

    verify(etageService).getById(id);
    verifyNoMoreInteractions(etageService);
  }

  // ---------- save ----------

  @Test
  void save_shouldPersistAndReturnDto() throws Exception {
    EtageDto inputDto = new EtageDto(null, "2", batimentId, List.of());
    EtageDto resultDto = new EtageDto(id, "2", batimentId, List.of());

    when(etageService.save(inputDto)).thenReturn(resultDto);

    mockMvc
        .perform(
            post("/api/v1/etage/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.numero").value("2"));

    verify(etageService).save(inputDto);
    verifyNoMoreInteractions(etageService);
  }

  // ---------- update ----------

  @Test
  void update_shouldMutateAndReturnDto() throws Exception {
    EtageUpdateRequest request = new EtageUpdateRequest(id, dto);
    EtageDto resultDto = new EtageDto(id, "1 - renamed", batimentId, List.of());

    when(etageService.update(id, dto)).thenReturn(resultDto);

    mockMvc
        .perform(
            put("/api/v1/etage/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.numero").value("1 - renamed"));

    verify(etageService).update(id, dto);
    verifyNoMoreInteractions(etageService);
  }

  // ---------- delete ----------

  @Test
  void delete_shouldReturnOkAndInvokeService() throws Exception {
    mockMvc.perform(delete("/api/v1/etage/{id}", id)).andExpect(status().isOk());

    verify(etageService, times(1)).delete(id);
    verifyNoMoreInteractions(etageService);
  }
}
