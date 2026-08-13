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
import org.aminesidki.resiaiac.dto.UtilisateurPromotionChambreDto;
import org.aminesidki.resiaiac.dto.request.UtilisateurPromotionChambreUpdateRequest;
import org.aminesidki.resiaiac.entity.id.UtilisateurPromotionChambreId;
import org.aminesidki.resiaiac.service.UtilisateurPromotionChambreService;
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
 * Unit tests for {@link UtilisateurPromotionChambreController}, exercised through {@link MockMvc}
 * against a sliced web context.
 *
 * <p>{@link UtilisateurPromotionChambreService} is mocked; only request routing, (de)serialization,
 * and status codes are under test here — business logic is covered separately by {@code
 * UtilisateurPromotionChambreServiceTest}.
 *
 * <p>getById/delete use {@code @RequestParam} (not {@code @PathVariable}) since the composite key
 * has three raw UUID components carried as query params rather than a single path segment.
 *
 * <p>Security filters are disabled ({@code addFilters = false}) since Keycloak-backed
 * authentication is out of scope for these tests.
 */
@WebMvcTest(UtilisateurPromotionChambreController.class)
@AutoConfigureMockMvc(addFilters = false)
class UtilisateurPromotionChambreControllerTest {

  @Autowired private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @MockitoBean private UtilisateurPromotionChambreService utilisateurPromotionChambreService;

  private UUID utilisateurId;
  private UUID promotionId;
  private UUID chambreId;
  private UtilisateurPromotionChambreId id;
  private UtilisateurPromotionChambreDto dto;

  @BeforeEach
  void setUp() {
    utilisateurId = UUID.randomUUID();
    promotionId = UUID.randomUUID();
    chambreId = UUID.randomUUID();
    id = new UtilisateurPromotionChambreId(utilisateurId, promotionId, chambreId);
    dto =
        new UtilisateurPromotionChambreDto(
            id, false, "RAS", utilisateurId, promotionId, chambreId, List.of());
  }

  // ---------- getById ----------

  @Test
  void getById_shouldReturnDto() throws Exception {
    when(utilisateurPromotionChambreService.getById(utilisateurId, promotionId, chambreId))
        .thenReturn(dto);

    mockMvc
        .perform(
            get("/api/v1/upc/")
                .param("utilisateurId", utilisateurId.toString())
                .param("promotionId", promotionId.toString())
                .param("chambreId", chambreId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.retard").value(false))
        .andExpect(jsonPath("$.note").value("RAS"));

    verify(utilisateurPromotionChambreService).getById(utilisateurId, promotionId, chambreId);
    verifyNoMoreInteractions(utilisateurPromotionChambreService);
  }

  // ---------- save ----------

  @Test
  void save_shouldPersistAndReturnDto() throws Exception {
    UtilisateurPromotionChambreDto inputDto =
        new UtilisateurPromotionChambreDto(
            id, true, "En retard", utilisateurId, promotionId, chambreId, List.of());
    UtilisateurPromotionChambreDto resultDto =
        new UtilisateurPromotionChambreDto(
            id, true, "En retard", utilisateurId, promotionId, chambreId, List.of());

    when(utilisateurPromotionChambreService.save(inputDto)).thenReturn(resultDto);

    mockMvc
        .perform(
            post("/api/v1/upc/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.retard").value(true))
        .andExpect(jsonPath("$.note").value("En retard"));

    verify(utilisateurPromotionChambreService).save(inputDto);
    verifyNoMoreInteractions(utilisateurPromotionChambreService);
  }

  // ---------- update ----------

  @Test
  void update_shouldMutateAndReturnDto() throws Exception {
    UtilisateurPromotionChambreUpdateRequest request =
        new UtilisateurPromotionChambreUpdateRequest(id, dto);
    UtilisateurPromotionChambreDto resultDto =
        new UtilisateurPromotionChambreDto(
            id, true, "Mis a jour", utilisateurId, promotionId, chambreId, List.of());

    when(utilisateurPromotionChambreService.update(id, dto)).thenReturn(resultDto);

    mockMvc
        .perform(
            put("/api/v1/upc/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.note").value("Mis a jour"));

    verify(utilisateurPromotionChambreService).update(id, dto);
    verifyNoMoreInteractions(utilisateurPromotionChambreService);
  }

  // ---------- delete ----------

  @Test
  void delete_shouldReturnOkAndInvokeService() throws Exception {
    mockMvc
        .perform(
            delete("/api/v1/upc/")
                .param("utilisateurId", utilisateurId.toString())
                .param("promotionId", promotionId.toString())
                .param("chambreId", chambreId.toString()))
        .andExpect(status().isOk());

    verify(utilisateurPromotionChambreService, times(1))
        .delete(utilisateurId, promotionId, chambreId);
    verifyNoMoreInteractions(utilisateurPromotionChambreService);
  }
}
