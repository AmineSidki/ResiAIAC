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
import org.aminesidki.resiaiac.dto.ServiceDto;
import org.aminesidki.resiaiac.dto.request.ServiceUpdateRequest;
import org.aminesidki.resiaiac.service.ServiceService;
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
 * Unit tests for {@link ServiceController}, exercised through {@link MockMvc} against a sliced web
 * context.
 *
 * <p>{@link ServiceService} is mocked; only request routing, (de)serialization, and status codes
 * are under test here — business logic is covered separately by {@code ServiceServiceTest}.
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
 * <p>Unlike the other entities in this package, {@code Service} uses a {@link Long} identifier
 * rather than a {@link java.util.UUID}.
 */
@WebMvcTest(ServiceController.class)
@AutoConfigureMockMvc(addFilters = false)
class ServiceControllerTest {

  @Autowired private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @MockitoBean private ServiceService serviceService;

  private Long id;
  private ServiceDto dto;

  @BeforeEach
  void setUp() {
    id = 1L;
    dto = new ServiceDto(id, "Maintenance", List.of());
  }

  // ---------- getById ----------

  @Test
  void getById_shouldReturnDto() throws Exception {
    when(serviceService.getById(id)).thenReturn(dto);

    mockMvc
        .perform(get("/api/v1/service/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.nom").value("Maintenance"));

    verify(serviceService).getById(id);
    verifyNoMoreInteractions(serviceService);
  }

  // ---------- save ----------

  @Test
  void save_shouldPersistAndReturnDto() throws Exception {
    ServiceDto inputDto = new ServiceDto(null, "Nouveau Service", List.of());
    ServiceDto resultDto = new ServiceDto(id, "Nouveau Service", List.of());

    when(serviceService.save(inputDto)).thenReturn(resultDto);

    mockMvc
        .perform(
            post("/api/v1/service/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.nom").value("Nouveau Service"));

    verify(serviceService).save(inputDto);
    verifyNoMoreInteractions(serviceService);
  }

  // ---------- update ----------

  @Test
  void update_shouldMutateAndReturnDto() throws Exception {
    ServiceUpdateRequest request = new ServiceUpdateRequest(id, dto);
    ServiceDto resultDto = new ServiceDto(id, "Maintenance - renamed", List.of());

    when(serviceService.update(id, dto)).thenReturn(resultDto);

    mockMvc
        .perform(
            put("/api/v1/service/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.nom").value("Maintenance - renamed"));

    verify(serviceService).update(id, dto);
    verifyNoMoreInteractions(serviceService);
  }

  // ---------- delete ----------

  @Test
  void delete_shouldReturnOkAndInvokeService() throws Exception {
    mockMvc.perform(delete("/api/v1/service/{id}", id)).andExpect(status().isOk());

    verify(serviceService, times(1)).delete(id);
    verifyNoMoreInteractions(serviceService);
  }
}
