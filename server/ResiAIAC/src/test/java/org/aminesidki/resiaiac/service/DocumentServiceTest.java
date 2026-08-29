package org.aminesidki.resiaiac.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import org.aminesidki.resiaiac.dto.DocumentDto;
import org.aminesidki.resiaiac.entity.Document;
import org.aminesidki.resiaiac.entity.Utilisateur;
import org.aminesidki.resiaiac.enumeration.EtatDocument;
import org.aminesidki.resiaiac.enumeration.FileType;
import org.aminesidki.resiaiac.exception.ResourceOwnershipMismatchException;
import org.aminesidki.resiaiac.mapper.DocumentMapper;
import org.aminesidki.resiaiac.repository.DocumentRepository;
import org.aminesidki.resiaiac.service.impl.DocumentServiceImpl;
import org.aminesidki.resiaiac.util.ResourceFetcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Unit tests for {@link DocumentService}, exercised through its {@link DocumentServiceImpl}
 * implementation.
 *
 * <p>ResourceFetcher.fetchResource is a static method, mocked per-test with Mockito's mockStatic
 * (requires Mockito 5+ / mockito-inline).
 *
 * <p>SeaweedFsService is mocked entirely — no real storage backend involved, no Testcontainers.
 */
@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

  @Mock private SeaweedFsService seaweedFsService;

  @Mock private UtilisateurService utilisateurService;

  @Mock private DocumentRepository documentRepository;

  @Mock private DocumentMapper documentMapper;

  @Mock private EmailTemplateService emailTemplateService;

  private DocumentService documentService;

  private UUID id;
  private Document entity;
  private DocumentDto dto;

  @BeforeEach
  void setUp() {
    documentService =
        new DocumentServiceImpl(
            seaweedFsService,
            utilisateurService,
            documentRepository,
            documentMapper,
            emailTemplateService);

    id = UUID.randomUUID();
    entity = Document.builder().id(id).nomFichier("cin.pdf").nomSceau("cin").build();
    dto = new DocumentDto(id, "cin.pdf", "cin", null, null, null, null);
  }

  // ---------- getMyFileUrlById ----------

  @Test
  void getMyFileUrlById_shouldReturnUrlWhenOwnerMatches() {
    Jwt jwt = mock(Jwt.class);
    Utilisateur owner = Utilisateur.builder().id(UUID.randomUUID()).build();
    Document owned =
        Document.builder().id(id).nomFichier("cin.pdf").nomSceau("cin").proprietaire(owner).build();
    String url = "https://seaweed.local/cin/cin.pdf?signed=1";

    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      when(utilisateurService.getMyEntityByJwt(jwt)).thenReturn(owner);
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, documentRepository, "Document"))
          .thenReturn(owned);
      when(seaweedFsService.getFileUrl(eq("cin"), eq("cin.pdf"), anyInt())).thenReturn(url);

      String result = documentService.getMyFileUrlById(jwt, id);

      assertThat(result).isEqualTo(url);
      verify(seaweedFsService).getFileUrl(eq("cin"), eq("cin.pdf"), anyInt());
    }
  }

  @Test
  void getMyFileUrlById_shouldThrowWhenOwnerMismatches() {
    Jwt jwt = mock(Jwt.class);
    Utilisateur owner = Utilisateur.builder().id(UUID.randomUUID()).build();
    Utilisateur requester = Utilisateur.builder().id(UUID.randomUUID()).build();
    Document owned =
        Document.builder().id(id).nomFichier("cin.pdf").nomSceau("cin").proprietaire(owner).build();

    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      when(utilisateurService.getMyEntityByJwt(jwt)).thenReturn(requester);
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, documentRepository, "Document"))
          .thenReturn(owned);

      assertThatThrownBy(() -> documentService.getMyFileUrlById(jwt, id))
          .isInstanceOf(ResourceOwnershipMismatchException.class);

      verify(seaweedFsService, never()).getFileUrl(any(), any(), anyInt());
    }
  }

  // ---------- getMyById ----------

  @Test
  void getMyById_shouldReturnDtoWhenOwnerMatches() {
    Jwt jwt = mock(Jwt.class);
    Utilisateur owner = Utilisateur.builder().id(UUID.randomUUID()).build();
    Document owned =
        Document.builder().id(id).nomFichier("cin.pdf").nomSceau("cin").proprietaire(owner).build();

    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      when(utilisateurService.getMyEntityByJwt(jwt)).thenReturn(owner);
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, documentRepository, "Document"))
          .thenReturn(owned);
      when(documentMapper.toDto(owned)).thenReturn(dto);

      DocumentDto result = documentService.getMyById(jwt, id);

      assertThat(result).isEqualTo(dto);
      verify(documentMapper).toDto(owned);
    }
  }

  @Test
  void getMyById_shouldThrowOwnershipMismatchWhenOwnerDoesNotMatch() {
    Jwt jwt = mock(Jwt.class);
    Utilisateur owner = Utilisateur.builder().id(UUID.randomUUID()).build();
    Utilisateur requester = Utilisateur.builder().id(UUID.randomUUID()).build();
    Document owned =
        Document.builder().id(id).nomFichier("cin.pdf").nomSceau("cin").proprietaire(owner).build();

    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      when(utilisateurService.getMyEntityByJwt(jwt)).thenReturn(requester);
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, documentRepository, "Document"))
          .thenReturn(owned);

      assertThatThrownBy(() -> documentService.getMyById(jwt, id))
          .isInstanceOf(ResourceOwnershipMismatchException.class);

      verifyNoMoreInteractions(documentMapper);
    }
  }

  // ---------- uploadMyDocument ----------

  @Test
  void uploadMyDocument_shouldSaveEntityUploadFileAndReturnDto_whenNoExistingDocument()
      throws Exception {
    Jwt jwt = mock(Jwt.class);
    Utilisateur me = Utilisateur.builder().id(UUID.randomUUID()).build();
    MockMultipartFile file =
        new MockMultipartFile("file", "cin.pdf", "application/pdf", "content".getBytes());
    Document savedEntity =
        Document.builder()
            .id(id)
            .nomFichier("random-name")
            .nomSceau("cin")
            .proprietaire(me)
            .build();

    when(utilisateurService.getMyEntityByJwt(jwt)).thenReturn(me);
    when(documentRepository.findFirstByNomSceauAndProprietaire(FileType.CIN.getBucketName(), me))
        .thenReturn(null);
    when(documentRepository.save(any(Document.class))).thenReturn(savedEntity);
    when(documentMapper.toDto(any(Document.class))).thenReturn(dto);

    DocumentDto result = documentService.uploadMyDocument(jwt, FileType.CIN, file);

    assertThat(result).isEqualTo(dto);

    ArgumentCaptor<Document> captor = ArgumentCaptor.forClass(Document.class);
    verify(documentRepository).save(captor.capture());
    Document persisted = captor.getValue();
    assertThat(persisted.getEtat()).isEqualTo(EtatDocument.EN_ATTENTE);
    assertThat(persisted.getNomSceau()).isEqualTo(FileType.CIN.getBucketName());
    assertThat(persisted.getProprietaire()).isEqualTo(me);
    assertThat(persisted.getNomFichier()).isNotBlank();

    verify(seaweedFsService)
        .uploadFile(eq(FileType.CIN.getBucketName()), any(String.class), eq(file));
    verify(documentRepository, never()).delete(any());
    verify(seaweedFsService, never()).deleteFile(any(), any());
  }

  @Test
  void uploadMyDocument_shouldUploadNewThenDeleteOldDocument_whenSameTypeAlreadyExists()
      throws Exception {
    Jwt jwt = mock(Jwt.class);
    Utilisateur me = Utilisateur.builder().id(UUID.randomUUID()).build();
    MockMultipartFile file =
        new MockMultipartFile("file", "cin.pdf", "application/pdf", "content".getBytes());
    Document oldDocument =
        Document.builder()
            .id(UUID.randomUUID())
            .nomFichier("old-name")
            .nomSceau(FileType.CIN.getBucketName())
            .proprietaire(me)
            .build();
    Document savedEntity =
        Document.builder().id(id).nomFichier("new-name").nomSceau("cin").proprietaire(me).build();

    when(utilisateurService.getMyEntityByJwt(jwt)).thenReturn(me);
    when(documentRepository.findFirstByNomSceauAndProprietaire(FileType.CIN.getBucketName(), me))
        .thenReturn(oldDocument);
    when(documentRepository.save(any(Document.class))).thenReturn(savedEntity);

    documentService.uploadMyDocument(jwt, FileType.CIN, file);

    // new file must be uploaded before the old one is torn down
    InOrder inOrder = Mockito.inOrder(seaweedFsService, documentRepository);
    inOrder
        .verify(seaweedFsService)
        .uploadFile(eq(FileType.CIN.getBucketName()), any(String.class), eq(file));
    inOrder.verify(documentRepository).delete(oldDocument);
    inOrder
        .verify(seaweedFsService)
        .deleteFile(oldDocument.getNomSceau(), oldDocument.getNomFichier());
  }

  @Test
  void uploadMyDocument_shouldNotDeleteOldDocument_whenNewUploadFails() throws Exception {
    Jwt jwt = mock(Jwt.class);
    Utilisateur me = Utilisateur.builder().id(UUID.randomUUID()).build();
    MockMultipartFile file =
        new MockMultipartFile("file", "cin.pdf", "application/pdf", "content".getBytes());
    Document oldDocument =
        Document.builder()
            .id(UUID.randomUUID())
            .nomFichier("old-name")
            .nomSceau(FileType.CIN.getBucketName())
            .proprietaire(me)
            .build();

    when(utilisateurService.getMyEntityByJwt(jwt)).thenReturn(me);
    when(documentRepository.findFirstByNomSceauAndProprietaire(FileType.CIN.getBucketName(), me))
        .thenReturn(oldDocument);
    when(documentRepository.save(any(Document.class)))
        .thenReturn(Document.builder().id(id).nomFichier("new-name").build());
    org.mockito.Mockito.doThrow(new java.io.IOException("storage unavailable"))
        .when(seaweedFsService)
        .uploadFile(eq(FileType.CIN.getBucketName()), any(String.class), eq(file));

    assertThatThrownBy(() -> documentService.uploadMyDocument(jwt, FileType.CIN, file))
        .isInstanceOf(java.io.IOException.class);

    // old document must survive an upload failure — nothing was torn down
    verify(documentRepository, never()).delete(any());
    verify(seaweedFsService, never()).deleteFile(any(), any());
  }

  // ---------- getAll ----------

  @Test
  void getAll_shouldReturnAllMappedResults() {
    Pageable pageable = PageRequest.of(0, 20);
    var page = new PageImpl<>(List.of(entity));

    when(documentRepository.findAllBy(pageable)).thenReturn(page);
    when(documentMapper.toDto(entity)).thenReturn(dto);

    var result = documentService.getAll(pageable);

    assertThat(result.getContent()).containsExactly(dto);
    verify(documentRepository).findAllBy(pageable);
    verify(documentMapper).toDto(entity);
  }

  // ---------- getAllByStatus ----------

  @Test
  void getAllByStatus_shouldFilterByStatusAndMapResults() {
    Pageable pageable = PageRequest.of(0, 20);
    var page = new PageImpl<>(List.of(entity));

    when(documentRepository.findAllByEtat(EtatDocument.VALIDE, pageable)).thenReturn(page);
    when(documentMapper.toDto(entity)).thenReturn(dto);

    var result = documentService.getAllByStatus(EtatDocument.VALIDE, pageable);

    assertThat(result.getContent()).containsExactly(dto);
    verify(documentRepository).findAllByEtat(EtatDocument.VALIDE, pageable);
    verify(documentMapper).toDto(entity);
  }

  // ---------- getById ----------

  @Test
  void getById_shouldFetchAndReturnDto() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, documentRepository, "Document"))
          .thenReturn(entity);
      when(documentMapper.toDto(entity)).thenReturn(dto);

      DocumentDto result = documentService.getById(id);

      assertThat(result).isEqualTo(dto);
      fetcher.verify(() -> ResourceFetcher.fetchResource(id, documentRepository, "Document"));
      verify(documentMapper).toDto(entity);
      verifyNoMoreInteractions(documentMapper);
    }
  }

  @Test
  void getById_shouldPropagateExceptionWhenNotFound() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      RuntimeException notFound = new RuntimeException("Document not found");
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, documentRepository, "Document"))
          .thenThrow(notFound);

      assertThatThrownBy(() -> documentService.getById(id)).isSameAs(notFound);

      verifyNoMoreInteractions(documentMapper);
    }
  }

  // ---------- getFileUrlById ----------

  @Test
  void getFileUrlById_shouldFetchAndReturnUrl() {
    String url = "https://seaweed.local/cin/cin.pdf?signed=1";

    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, documentRepository, "Document"))
          .thenReturn(entity);
      when(seaweedFsService.getFileUrl(eq("cin"), eq("cin.pdf"), anyInt())).thenReturn(url);

      String result = documentService.getFileUrlById(id);

      assertThat(result).isEqualTo(url);
      verify(seaweedFsService).getFileUrl(eq("cin"), eq("cin.pdf"), anyInt());
    }
  }

  // ---------- update ----------

  @Test
  void update_shouldFetchMutateSaveAndReturnDto() {
    Utilisateur owner =
        Utilisateur.builder().id(UUID.randomUUID()).email("etudiant@example.com").build();
    Document savedEntity =
        Document.builder()
            .id(id)
            .nomFichier("cin-renamed.pdf")
            .etat(EtatDocument.VALIDE)
            .proprietaire(owner)
            .build();
    DocumentDto resultDto = new DocumentDto(id, "cin-renamed.pdf", null, null, null, null, null);

    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, documentRepository, "Document"))
          .thenReturn(entity);
      when(documentRepository.save(entity)).thenReturn(savedEntity);
      when(documentMapper.toDto(savedEntity)).thenReturn(resultDto);

      DocumentDto result = documentService.update(id, dto);

      assertThat(result).isEqualTo(resultDto);
      fetcher.verify(() -> ResourceFetcher.fetchResource(id, documentRepository, "Document"));
      verify(documentMapper).updateEntityFromDto(dto, entity);
      verify(documentRepository).save(entity);
      verify(documentMapper).toDto(savedEntity);
      verify(emailTemplateService).envoyerDocumentStatut(owner, savedEntity);
      verifyNoMoreInteractions(documentRepository, documentMapper, emailTemplateService);
    }
  }

  @Test
  void update_shouldNotSaveWhenResourceNotFound() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      RuntimeException notFound = new RuntimeException("Document not found");
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, documentRepository, "Document"))
          .thenThrow(notFound);

      assertThatThrownBy(() -> documentService.update(id, dto)).isSameAs(notFound);

      verify(documentRepository, never()).save(any());
      verifyNoMoreInteractions(documentMapper);
    }
  }

  // ---------- delete ----------

  @Test
  void delete_shouldFetchDeleteEntityAndFile() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, documentRepository, "Document"))
          .thenReturn(entity);

      documentService.delete(id);

      fetcher.verify(() -> ResourceFetcher.fetchResource(id, documentRepository, "Document"));
      verify(documentRepository, times(1)).delete(entity);
      verify(seaweedFsService, times(1)).deleteFile(entity.getNomSceau(), entity.getNomFichier());
      verifyNoMoreInteractions(documentRepository, seaweedFsService);
    }
  }

  @Test
  void delete_shouldNotDeleteWhenResourceNotFound() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      RuntimeException notFound = new RuntimeException("Document not found");
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, documentRepository, "Document"))
          .thenThrow(notFound);

      assertThatThrownBy(() -> documentService.delete(id)).isSameAs(notFound);

      verify(documentRepository, never()).delete(any());
      verify(seaweedFsService, never()).deleteFile(any(), any());
    }
  }

  // ---------- getAllMy ----------

  @Test
  void getAllMy_shouldResolveUserFromJwtAndMapEachResultToDto() {
    Jwt jwt = mock(Jwt.class);
    Utilisateur me = Utilisateur.builder().id(UUID.randomUUID()).build();
    Pageable pageable = PageRequest.of(0, 20);
    var page = new PageImpl<>(List.of(entity));

    when(utilisateurService.getMyEntityByJwt(jwt)).thenReturn(me);
    when(documentRepository.findAllByProprietaire(me, pageable)).thenReturn(page);
    when(documentMapper.toDto(entity)).thenReturn(dto);

    var result = documentService.getAllMy(jwt, pageable);

    assertThat(result.getContent()).containsExactly(dto);
    verify(utilisateurService).getMyEntityByJwt(jwt);
    verify(documentRepository).findAllByProprietaire(me, pageable);
    verify(documentMapper).toDto(entity);
  }

  // ---------- getAllMyByStatus ----------

  @Test
  void getAllMyByStatus_shouldResolveUserFromJwtFilterByStatusAndMapResults() {
    Jwt jwt = mock(Jwt.class);
    Utilisateur me = Utilisateur.builder().id(UUID.randomUUID()).build();
    Pageable pageable = PageRequest.of(0, 20);
    var page = new PageImpl<>(List.of(entity));

    when(utilisateurService.getMyEntityByJwt(jwt)).thenReturn(me);
    when(documentRepository.getAllByProprietaireAndEtat(me, EtatDocument.EN_ATTENTE, pageable))
        .thenReturn(page);
    when(documentMapper.toDto(entity)).thenReturn(dto);

    var result = documentService.getAllMyByStatus(jwt, EtatDocument.EN_ATTENTE, pageable);

    assertThat(result.getContent()).containsExactly(dto);
    verify(utilisateurService).getMyEntityByJwt(jwt);
    verify(documentRepository).getAllByProprietaireAndEtat(me, EtatDocument.EN_ATTENTE, pageable);
    verify(documentMapper).toDto(entity);
  }
}
