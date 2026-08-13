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
import org.aminesidki.resiaiac.dto.EquipementUpcDto;
import org.aminesidki.resiaiac.dto.request.EquipementUpcUpdateRequest;
import org.aminesidki.resiaiac.entity.id.EquipementUpcId;
import org.aminesidki.resiaiac.entity.id.UtilisateurPromotionChambreId;
import org.aminesidki.resiaiac.service.EquipementUpcService;
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
 * Unit tests for {@link EquipementUpcController}, exercised through {@link MockMvc} against a
 * sliced web context.
 *
 * <p>{@link EquipementUpcService} is mocked; only request routing, (de)serialization, and status
 * codes are under test here — business logic is covered separately by {@code
 * EquipementUpcServiceTest}.
 *
 * <p>{@code getById}/{@code delete} take the composite key's parts as query params ({@code
 * equipementId}, {@code utilisateurId}, {@code promotionId}, {@code chambreId}) rather than a path
 * variable, since {@link EquipementUpcId} — itself nesting a {@link UtilisateurPromotionChambreId}
 * — has no single-value representation.
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
@WebMvcTest(EquipementUpcController.class)
@AutoConfigureMockMvc(addFilters = false)
class EquipementUpcControllerTest {

  private static final String BASE_PATH = "/api/v1/equipement-upc";

  @Autowired private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @MockitoBean private EquipementUpcService equipementUpcService;

  private Long equipementId;
  private UUID utilisateurId;
  private UUID promotionId;
  private UUID chambreId;
  private UtilisateurPromotionChambreId upcId;
  private EquipementUpcId id;
  private EquipementUpcDto dto;

  @BeforeEach
  void setUp() {
    equipementId = 1L;
    utilisateurId = UUID.randomUUID();
    promotionId = UUID.randomUUID();
    chambreId = UUID.randomUUID();
    upcId = new UtilisateurPromotionChambreId(utilisateurId, promotionId, chambreId);
    id = new EquipementUpcId(equipementId, upcId);
    dto = new EquipementUpcDto(id, 5L, equipementId, upcId);
  }

  // ---------- getById ----------

  @Test
  void getById_shouldReturnDto() throws Exception {
    when(equipementUpcService.getById(equipementId, utilisateurId, promotionId, chambreId))
        .thenReturn(dto);

    mockMvc
        .perform(
            get(BASE_PATH + "/")
                .param("equipementId", equipementId.toString())
                .param("utilisateurId", utilisateurId.toString())
                .param("promotionId", promotionId.toString())
                .param("chambreId", chambreId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.quantite").value(5))
        .andExpect(jsonPath("$.equipement").value(equipementId))
        .andExpect(jsonPath("$.upc.utilisateur_id").value(utilisateurId.toString()));

    verify(equipementUpcService).getById(equipementId, utilisateurId, promotionId, chambreId);
    verifyNoMoreInteractions(equipementUpcService);
  }

  // ---------- save ----------

  @Test
  void save_shouldPersistAndReturnDto() throws Exception {
    UtilisateurPromotionChambreId otherUpcId =
        new UtilisateurPromotionChambreId(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
    EquipementUpcDto inputDto = new EquipementUpcDto(null, 10L, 2L, otherUpcId);
    EquipementUpcDto resultDto = new EquipementUpcDto(id, 10L, equipementId, upcId);

    when(equipementUpcService.save(inputDto)).thenReturn(resultDto);

    mockMvc
        .perform(
            post(BASE_PATH + "/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.quantite").value(10))
        .andExpect(jsonPath("$.equipement").value(equipementId));

    verify(equipementUpcService).save(inputDto);
    verifyNoMoreInteractions(equipementUpcService);
  }

  // ---------- update ----------

  @Test
  void update_shouldMutateAndReturnDto() throws Exception {
    EquipementUpcUpdateRequest request = new EquipementUpcUpdateRequest(id, dto);
    EquipementUpcDto resultDto = new EquipementUpcDto(id, 7L, equipementId, upcId);

    when(equipementUpcService.update(id, dto)).thenReturn(resultDto);

    mockMvc
        .perform(
            put(BASE_PATH + "/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.quantite").value(7));

    verify(equipementUpcService).update(id, dto);
    verifyNoMoreInteractions(equipementUpcService);
  }

  // ---------- delete ----------

  @Test
  void delete_shouldReturnOkAndInvokeService() throws Exception {
    mockMvc
        .perform(
            delete(BASE_PATH + "/")
                .param("equipementId", equipementId.toString())
                .param("utilisateurId", utilisateurId.toString())
                .param("promotionId", promotionId.toString())
                .param("chambreId", chambreId.toString()))
        .andExpect(status().isOk());

    verify(equipementUpcService, times(1))
        .delete(equipementId, utilisateurId, promotionId, chambreId);
    verifyNoMoreInteractions(equipementUpcService);
  }
}
