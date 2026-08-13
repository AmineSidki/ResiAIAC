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
import org.aminesidki.resiaiac.dto.PromotionDto;
import org.aminesidki.resiaiac.dto.request.PromotionUpdateRequest;
import org.aminesidki.resiaiac.service.PromotionService;
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
 * Unit tests for {@link PromotionController}, exercised through {@link MockMvc} against a sliced
 * web context.
 *
 * <p>{@link PromotionService} is mocked; only request routing, (de)serialization, and status codes
 * are under test here — business logic is covered separately by {@code PromotionServiceTest}.
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
 * <p>{@code PromotionDto}'s constructor order is {@code (id, anneeDeDepart, anneeDeFin, niveau,
 * filiere, combinaisonsUpc)} — {@code niveau} is an {@code Integer}, {@code filiere} is a {@code
 * Long}.
 */
@WebMvcTest(PromotionController.class)
@AutoConfigureMockMvc(addFilters = false)
class PromotionControllerTest {

  @Autowired private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @MockitoBean private PromotionService promotionService;

  private UUID id;
  private PromotionDto dto;

  @BeforeEach
  void setUp() {
    id = UUID.randomUUID();
    dto = new PromotionDto(id, 2025L, 2026L, 3, 1L, List.of());
  }

  // ---------- getById ----------

  @Test
  void getById_shouldReturnDto() throws Exception {
    when(promotionService.getById(id)).thenReturn(dto);

    mockMvc
        .perform(get("/api/v1/promotion/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.anneeDeDepart").value(2025))
        .andExpect(jsonPath("$.anneeDeFin").value(2026));

    verify(promotionService).getById(id);
    verifyNoMoreInteractions(promotionService);
  }

  // ---------- save ----------

  @Test
  void save_shouldPersistAndReturnDto() throws Exception {
    PromotionDto inputDto = new PromotionDto(null, 2025L, 2026L, 3, 1L, List.of());
    PromotionDto resultDto = new PromotionDto(id, 2025L, 2026L, 3, 1L, List.of());

    when(promotionService.save(inputDto)).thenReturn(resultDto);

    mockMvc
        .perform(
            post("/api/v1/promotion/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.niveau").value(3));

    verify(promotionService).save(inputDto);
    verifyNoMoreInteractions(promotionService);
  }

  // ---------- update ----------

  @Test
  void update_shouldMutateAndReturnDto() throws Exception {
    PromotionUpdateRequest request = new PromotionUpdateRequest(id, dto);
    PromotionDto resultDto = new PromotionDto(id, 2025L, 2026L, 4, 1L, List.of());

    when(promotionService.update(id, dto)).thenReturn(resultDto);

    mockMvc
        .perform(
            put("/api/v1/promotion/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.niveau").value(4));

    verify(promotionService).update(id, dto);
    verifyNoMoreInteractions(promotionService);
  }

  // ---------- delete ----------

  @Test
  void delete_shouldReturnOkAndInvokeService() throws Exception {
    mockMvc.perform(delete("/api/v1/promotion/{id}", id)).andExpect(status().isOk());

    verify(promotionService, times(1)).delete(id);
    verifyNoMoreInteractions(promotionService);
  }
}
