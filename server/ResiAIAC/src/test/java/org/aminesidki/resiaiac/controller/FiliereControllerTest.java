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
import org.aminesidki.resiaiac.dto.FiliereDto;
import org.aminesidki.resiaiac.dto.request.FiliereUpdateRequest;
import org.aminesidki.resiaiac.service.FiliereService;
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
 * Unit tests for {@link FiliereController}, exercised through {@link MockMvc} against a sliced web
 * context.
 *
 * <p>{@link FiliereService} is mocked; only request routing, (de)serialization, and status codes
 * are under test here — business logic is covered separately by {@code FiliereServiceTest}.
 *
 * <p>Unlike most other controllers in this module, Filiere uses a {@code Long} id rather than
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
@WebMvcTest(FiliereController.class)
@AutoConfigureMockMvc(addFilters = false)
class FiliereControllerTest {

  private static final String BASE_PATH = "/api/v1/filiere";

  @Autowired private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @MockitoBean private FiliereService filiereService;

  private Long id;
  private FiliereDto dto;

  @BeforeEach
  void setUp() {
    id = 1L;
    dto = new FiliereDto(id, "Genie Informatique", 5, List.of());
  }

  // ---------- getById ----------

  @Test
  void getById_shouldReturnDto() throws Exception {
    when(filiereService.getById(id)).thenReturn(dto);

    mockMvc
        .perform(get(BASE_PATH + "/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.nom").value("Genie Informatique"));

    verify(filiereService).getById(id);
    verifyNoMoreInteractions(filiereService);
  }

  // ---------- save ----------

  @Test
  void save_shouldPersistAndReturnDto() throws Exception {
    FiliereDto inputDto = new FiliereDto(null, "Genie Informatique", 5, List.of());
    FiliereDto resultDto = new FiliereDto(id, "Genie Informatique", 5, List.of());

    when(filiereService.save(inputDto)).thenReturn(resultDto);

    mockMvc
        .perform(
            post(BASE_PATH + "/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.nom").value("Genie Informatique"));

    verify(filiereService).save(inputDto);
    verifyNoMoreInteractions(filiereService);
  }

  // ---------- update ----------

  @Test
  void update_shouldMutateAndReturnDto() throws Exception {
    FiliereUpdateRequest request = new FiliereUpdateRequest(id, dto);
    FiliereDto resultDto = new FiliereDto(id, "Genie Informatique - renamed", 5, List.of());

    when(filiereService.update(id, dto)).thenReturn(resultDto);

    mockMvc
        .perform(
            put(BASE_PATH + "/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.nom").value("Genie Informatique - renamed"));

    verify(filiereService).update(id, dto);
    verifyNoMoreInteractions(filiereService);
  }

  // ---------- delete ----------

  @Test
  void delete_shouldReturnOkAndInvokeService() throws Exception {
    mockMvc.perform(delete(BASE_PATH + "/{id}", id)).andExpect(status().isOk());

    verify(filiereService, times(1)).delete(id);
    verifyNoMoreInteractions(filiereService);
  }
}
