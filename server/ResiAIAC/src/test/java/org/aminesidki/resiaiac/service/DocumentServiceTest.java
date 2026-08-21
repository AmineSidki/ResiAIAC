package org.aminesidki.resiaiac.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.aminesidki.resiaiac.dto.DocumentDto;
import org.aminesidki.resiaiac.entity.Document;
import org.aminesidki.resiaiac.mapper.DocumentMapper;
import org.aminesidki.resiaiac.repository.DocumentRepository;
import org.aminesidki.resiaiac.service.impl.DocumentServiceImpl;
import org.aminesidki.resiaiac.util.ResourceFetcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link DocumentService}, exercised through its {@link DocumentServiceImpl}
 * implementation.
 *
 * <p>ResourceFetcher.fetchResource is a static method, mocked per-test with Mockito's mockStatic
 * (requires Mockito 5+ / mockito-inline).
 */
@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

  @Mock private DocumentRepository documentRepository;

  @Mock private DocumentMapper documentMapper;

  @Mock private UtilisateurService utilisateurService;

  private DocumentService documentService;

  private UUID id;
  private Document entity;
  private DocumentDto dto;

  @BeforeEach
  void setUp() {
    documentService =
        new DocumentServiceImpl(utilisateurService, documentRepository, documentMapper);

    id = UUID.randomUUID();
    entity = Document.builder().id(id).nomFichier("cin.pdf").build();
    dto = new DocumentDto(id, "cin.pdf", null, null, null, null, null);
  }

  // ---------- save ----------

  @Test
  void save_shouldMapPersistAndReturnDto() {
    DocumentDto inputDto = new DocumentDto(null, "cin.pdf", null, null, null, null, null);
    Document mappedEntity = Document.builder().nomFichier("cin.pdf").build();
    Document savedEntity = Document.builder().id(id).nomFichier("cin.pdf").build();
    DocumentDto resultDto = new DocumentDto(id, "cin.pdf", null, null, null, null, null);

    when(documentMapper.toEntity(inputDto)).thenReturn(mappedEntity);
    when(documentRepository.save(mappedEntity)).thenReturn(savedEntity);
    when(documentMapper.toDto(savedEntity)).thenReturn(resultDto);

    DocumentDto result = documentService.save(inputDto);

    assertThat(result).isEqualTo(resultDto);
    verify(documentMapper).toEntity(inputDto);
    verify(documentRepository).save(mappedEntity);
    verify(documentMapper).toDto(savedEntity);
    verifyNoMoreInteractions(documentRepository, documentMapper);
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

  // ---------- update ----------

  @Test
  void update_shouldFetchMutateSaveAndReturnDto() {
    Document savedEntity = Document.builder().id(id).nomFichier("cin-renamed.pdf").build();
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
      verifyNoMoreInteractions(documentRepository, documentMapper);
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
  void delete_shouldFetchAndDeleteEntity() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, documentRepository, "Document"))
          .thenReturn(entity);

      documentService.delete(id);

      fetcher.verify(() -> ResourceFetcher.fetchResource(id, documentRepository, "Document"));
      verify(documentRepository, times(1)).delete(entity);
      verifyNoMoreInteractions(documentRepository);
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
    }
  }
}
