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
import org.aminesidki.resiaiac.dto.DocumentDto;
import org.aminesidki.resiaiac.dto.request.DocumentUpdateRequest;
import org.aminesidki.resiaiac.service.DocumentService;
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
 * Unit tests for {@link DocumentController}, exercised through {@link MockMvc} against a sliced web
 * context.
 *
 * <p>{@link DocumentService} is mocked; only request routing, (de)serialization, and status codes
 * are under test here — business logic is covered separately by {@code DocumentServiceTest}.
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
@WebMvcTest(DocumentController.class)
@AutoConfigureMockMvc(addFilters = false)
class DocumentControllerTest {

  private static final String BASE_PATH = "/api/v1/document";

  @Autowired private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @MockitoBean private DocumentService documentService;

  private UUID id;
  private DocumentDto dto;

  @BeforeEach
  void setUp() {
    id = UUID.randomUUID();
    dto = new DocumentDto(id, "cin.pdf", "seal.png", false, null, UUID.randomUUID(), null);
  }

  // ---------- getById ----------

  @Test
  void getById_shouldReturnDto() throws Exception {
    when(documentService.getById(id)).thenReturn(dto);

    mockMvc
        .perform(get(BASE_PATH + "/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.nomFichier").value("cin.pdf"));

    verify(documentService).getById(id);
    verifyNoMoreInteractions(documentService);
  }

  // ---------- save ----------

  @Test
  void save_shouldPersistAndReturnDto() throws Exception {
    DocumentDto inputDto = new DocumentDto(null, "cin.pdf", null, false, null, null, null);
    DocumentDto resultDto = new DocumentDto(id, "cin.pdf", null, false, null, null, null);

    when(documentService.save(inputDto)).thenReturn(resultDto);

    mockMvc
        .perform(
            post(BASE_PATH + "/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.nomFichier").value("cin.pdf"));

    verify(documentService).save(inputDto);
    verifyNoMoreInteractions(documentService);
  }

  // ---------- update ----------

  @Test
  void update_shouldMutateAndReturnDto() throws Exception {
    DocumentUpdateRequest request = new DocumentUpdateRequest(id, dto);
    DocumentDto resultDto =
        new DocumentDto(id, "cin-renamed.pdf", "seal.png", true, null, dto.proprietaire(), null);

    when(documentService.update(id, dto)).thenReturn(resultDto);

    mockMvc
        .perform(
            put(BASE_PATH + "/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.nomFichier").value("cin-renamed.pdf"))
        .andExpect(jsonPath("$.valide").value(true));

    verify(documentService).update(id, dto);
    verifyNoMoreInteractions(documentService);
  }

  // ---------- delete ----------

  @Test
  void delete_shouldReturnOkAndInvokeService() throws Exception {
    mockMvc.perform(delete(BASE_PATH + "/{id}", id)).andExpect(status().isOk());

    verify(documentService, times(1)).delete(id);
    verifyNoMoreInteractions(documentService);
  }
}
