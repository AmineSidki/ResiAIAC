package org.aminesidki.resiaiac.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.UUID;
import org.aminesidki.resiaiac.dto.DocumentDto;
import org.aminesidki.resiaiac.dto.request.DocumentUpdateRequest;
import org.aminesidki.resiaiac.enumeration.EtatDocument;
import org.aminesidki.resiaiac.enumeration.FileType;
import org.aminesidki.resiaiac.service.DocumentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
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
 * coverage too. {@code .with(jwt())} is still applied on self-service routes so
 * {@code @AuthenticationPrincipal Jwt} has something to resolve.
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
    dto = new DocumentDto(id, "cin.pdf", "seal.png", null, null, UUID.randomUUID(), null);
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

  // ---------- getAll ----------

  @Test
  void getAll_shouldReturnPagedResults() throws Exception {
    when(documentService.getAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(dto)));

    mockMvc
        .perform(get(BASE_PATH + "/"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(id.toString()));

    verify(documentService).getAll(any(Pageable.class));
    verifyNoMoreInteractions(documentService);
  }

  // ---------- getAllByStatus ----------

  @Test
  void getAllByStatus_shouldReturnPagedResultsFilteredByStatus() throws Exception {
    when(documentService.getAllByStatus(eq(EtatDocument.VALIDE), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(dto)));

    mockMvc
        .perform(get(BASE_PATH + "/by-etat/{etat}", EtatDocument.VALIDE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(id.toString()));

    verify(documentService).getAllByStatus(eq(EtatDocument.VALIDE), any(Pageable.class));
    verifyNoMoreInteractions(documentService);
  }

  // ---------- getDocumentUrlById ----------

  @Test
  void getDocumentUrlById_shouldReturnUrl() throws Exception {
    String url = "https://seaweed.local/cin/cin.pdf?signed=1";
    when(documentService.getFileUrlById(id)).thenReturn(url);

    mockMvc
        .perform(get(BASE_PATH + "/{id}/url", id))
        .andExpect(status().isOk())
        .andExpect(content().string(url));

    verify(documentService).getFileUrlById(id);
    verifyNoMoreInteractions(documentService);
  }

  // ---------- update ----------

  @Test
  void update_shouldMutateAndReturnDto() throws Exception {
    DocumentUpdateRequest request = new DocumentUpdateRequest(id, dto);
    DocumentDto resultDto =
        new DocumentDto(id, "cin-renamed.pdf", "seal.png", null, null, dto.proprietaire(), null);

    when(documentService.update(id, dto)).thenReturn(resultDto);

    mockMvc
        .perform(
            put(BASE_PATH + "/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.nomFichier").value("cin-renamed.pdf"));

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

  // ---------- myDocuments ----------

  @Test
  void myDocuments_shouldReturnPagedResults() throws Exception {
    when(documentService.getAllMy(any(), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(dto)));

    mockMvc
        .perform(get(BASE_PATH + "/me").with(jwt()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(id.toString()));

    verify(documentService).getAllMy(any(), any(Pageable.class));
    verifyNoMoreInteractions(documentService);
  }

  // ---------- getAllMyDocumentsByStatus ----------

  @Test
  void getAllMyDocumentsByStatus_shouldReturnPagedResults() throws Exception {
    when(documentService.getAllMyByStatus(any(), eq(EtatDocument.EN_ATTENTE), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(dto)));

    mockMvc
        .perform(get(BASE_PATH + "/me/by-etat/{etat}", EtatDocument.EN_ATTENTE).with(jwt()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(id.toString()));

    verify(documentService)
        .getAllMyByStatus(any(), eq(EtatDocument.EN_ATTENTE), any(Pageable.class));
    verifyNoMoreInteractions(documentService);
  }

  // ---------- myDocument (details) ----------

  @Test
  void myDocument_shouldReturnDto() throws Exception {
    when(documentService.getMyById(any(), eq(id))).thenReturn(dto);

    mockMvc
        .perform(get(BASE_PATH + "/me/{id}", id).with(jwt()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.nomFichier").value("cin.pdf"));

    verify(documentService).getMyById(any(), eq(id));
    verifyNoMoreInteractions(documentService);
  }

  // ---------- myDocument (url) ----------

  @Test
  void myDocument_shouldReturnUrl() throws Exception {
    String url = "https://seaweed.local/cin/cin.pdf?signed=1";
    when(documentService.getMyFileUrlById(any(), eq(id))).thenReturn(url);

    mockMvc
        .perform(get(BASE_PATH + "/me/{id}/url", id).with(jwt()))
        .andExpect(status().isOk())
        .andExpect(content().string(url));

    verify(documentService).getMyFileUrlById(any(), eq(id));
    verifyNoMoreInteractions(documentService);
  }

  // ---------- uploadMyDocument routes ----------

  @Test
  void uploadProfileImage_shouldUploadAsImageType() throws Exception {
    MockMultipartFile file =
        new MockMultipartFile("file", "pfp.png", MediaType.IMAGE_PNG_VALUE, "content".getBytes());
    when(documentService.uploadMyDocument(any(), eq(FileType.IMAGE), any())).thenReturn(dto);

    mockMvc
        .perform(multipart(BASE_PATH + "/me/upload/pfp").file(file).with(jwt()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()));

    verify(documentService).uploadMyDocument(any(), eq(FileType.IMAGE), any());
    verifyNoMoreInteractions(documentService);
  }

  @Test
  void uploadCin_shouldUploadAsCinType() throws Exception {
    MockMultipartFile file =
        new MockMultipartFile(
            "file", "cin.pdf", MediaType.APPLICATION_PDF_VALUE, "content".getBytes());
    when(documentService.uploadMyDocument(any(), eq(FileType.CIN), any())).thenReturn(dto);

    mockMvc
        .perform(multipart(BASE_PATH + "/me/upload/cin").file(file).with(jwt()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()));

    verify(documentService).uploadMyDocument(any(), eq(FileType.CIN), any());
    verifyNoMoreInteractions(documentService);
  }

  @Test
  void uploadDiploma_shouldUploadAsDiplomaType() throws Exception {
    MockMultipartFile file =
        new MockMultipartFile(
            "file", "dip.pdf", MediaType.APPLICATION_PDF_VALUE, "content".getBytes());
    when(documentService.uploadMyDocument(any(), eq(FileType.DIPLOMA), any())).thenReturn(dto);

    mockMvc
        .perform(multipart(BASE_PATH + "/me/upload/dip").file(file).with(jwt()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()));

    verify(documentService).uploadMyDocument(any(), eq(FileType.DIPLOMA), any());
    verifyNoMoreInteractions(documentService);
  }
}
