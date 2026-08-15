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
import org.aminesidki.resiaiac.dto.ReservationDto;
import org.aminesidki.resiaiac.dto.request.ReservationUpdateRequest;
import org.aminesidki.resiaiac.service.ReservationService;
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
 * Unit tests for {@link ReservationController}, exercised through {@link MockMvc} against a sliced
 * web context.
 *
 * <p>{@link ReservationService} is mocked; only request routing, (de)serialization, and status
 * codes are under test here — business logic is covered separately by {@code
 * ReservationServiceTest}.
 *
 * <p>{@code etat} is left {@code null} in the fixture DTOs here since the {@code EtatReservation}
 * enum's constants weren't available when writing this test — swap in a real constant if you'd like
 * that field exercised. {@code createdAt}/{@code updatedAt} are likewise left {@code null}.
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
@WebMvcTest(ReservationController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReservationControllerTest {

  private static final String BASE_PATH = "/api/v1/reservation";

  @Autowired private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @MockitoBean private ReservationService reservationService;

  private UUID id;
  private UUID utilisateurId;
  private UUID chambreId;
  private ReservationDto dto;

  @BeforeEach
  void setUp() {
    id = UUID.randomUUID();
    utilisateurId = UUID.randomUUID();
    chambreId = UUID.randomUUID();
    dto = new ReservationDto(id, null, utilisateurId, chambreId, null, null);
  }

  // ---------- getById ----------

  @Test
  void getById_shouldReturnDto() throws Exception {
    when(reservationService.getById(id)).thenReturn(dto);

    mockMvc
        .perform(get(BASE_PATH + "/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.utilisateur").value(utilisateurId.toString()))
        .andExpect(jsonPath("$.chambre").value(chambreId.toString()));

    verify(reservationService).getById(id);
    verifyNoMoreInteractions(reservationService);
  }

  // ---------- save ----------

  @Test
  void save_shouldPersistAndReturnDto() throws Exception {
    ReservationDto inputDto = new ReservationDto(null, null, utilisateurId, chambreId, null, null);
    ReservationDto resultDto = new ReservationDto(id, null, utilisateurId, chambreId, null, null);

    when(reservationService.save(inputDto)).thenReturn(resultDto);

    mockMvc
        .perform(
            post(BASE_PATH + "/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.utilisateur").value(utilisateurId.toString()));

    verify(reservationService).save(inputDto);
    verifyNoMoreInteractions(reservationService);
  }

  // ---------- update ----------

  @Test
  void update_shouldMutateAndReturnDto() throws Exception {
    ReservationUpdateRequest request = new ReservationUpdateRequest(id, dto);
    UUID newChambreId = UUID.randomUUID();
    ReservationDto resultDto =
        new ReservationDto(id, null, utilisateurId, newChambreId, null, null);

    when(reservationService.update(id, dto)).thenReturn(resultDto);

    mockMvc
        .perform(
            put(BASE_PATH + "/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.chambre").value(newChambreId.toString()));

    verify(reservationService).update(id, dto);
    verifyNoMoreInteractions(reservationService);
  }

  // ---------- delete ----------

  @Test
  void delete_shouldReturnOkAndInvokeService() throws Exception {
    mockMvc.perform(delete(BASE_PATH + "/{id}", id)).andExpect(status().isOk());

    verify(reservationService, times(1)).delete(id);
    verifyNoMoreInteractions(reservationService);
  }
}
