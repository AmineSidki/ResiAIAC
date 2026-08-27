package org.aminesidki.resiaiac.service.impl;

import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.DocumentDto;
import org.aminesidki.resiaiac.entity.Document;
import org.aminesidki.resiaiac.entity.Utilisateur;
import org.aminesidki.resiaiac.enumeration.EtatDocument;
import org.aminesidki.resiaiac.enumeration.FileType;
import org.aminesidki.resiaiac.exception.ResourceOwnershipMismatchException;
import org.aminesidki.resiaiac.mapper.DocumentMapper;
import org.aminesidki.resiaiac.repository.DocumentRepository;
import org.aminesidki.resiaiac.service.DocumentService;
import org.aminesidki.resiaiac.service.SeaweedFsService;
import org.aminesidki.resiaiac.service.UtilisateurService;
import org.aminesidki.resiaiac.util.ResourceFetcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Transactional
@Service
public class DocumentServiceImpl implements DocumentService {
  private static final int FILE_URL_EXPIRY_DURATION_IN_SECONDS = 300; // 5 minutes
  private final SeaweedFsService seaweedFsService;
  private final UtilisateurService utilisateurService;
  private final DocumentRepository documentRepository;
  private final DocumentMapper documentMapper;

  private Document userHasSameTypeDocumentUploaded(Utilisateur user, FileType fileType) {
    return documentRepository.findFirstByNomSceauAndProprietaire(fileType.getBucketName(), user);
  }

  private void deleteAlreadyFetched(Document entity) {
    documentRepository.delete(entity);
    seaweedFsService.deleteFile(entity.getNomSceau(), entity.getNomFichier());
  }

  @Transactional(readOnly = true)
  @Override
  public String getMyFileUrlById(Jwt jwt, UUID id) {
    Utilisateur utilisateur = utilisateurService.getMyEntityByJwt(jwt);
    Document entity = ResourceFetcher.fetchResource(id, documentRepository, "Document");
    if (entity.getProprietaire().equals(utilisateur))
      return seaweedFsService.getFileUrl(
          entity.getNomSceau(), entity.getNomFichier(), FILE_URL_EXPIRY_DURATION_IN_SECONDS);
    throw new ResourceOwnershipMismatchException(
        "Queried resource does not belong to querying user !");
  }

  @Transactional(readOnly = true)
  @Override
  public DocumentDto getMyById(Jwt jwt, UUID id) {
    Utilisateur utilisateur = utilisateurService.getMyEntityByJwt(jwt);
    Document entity = ResourceFetcher.fetchResource(id, documentRepository, "Document");
    if (entity.getProprietaire().equals(utilisateur)) return documentMapper.toDto(entity);
    throw new ResourceOwnershipMismatchException(
        "Queried resource does not belong to querying user !");
  }

  @Override
  public Page<DocumentDto> getAllMyByStatus(Jwt jwt, EtatDocument etat, Pageable pageable) {
    Utilisateur id = utilisateurService.getMyEntityByJwt(jwt);
    return documentRepository
        .getAllByProprietaireAndEtat(id, etat, pageable)
        .map(documentMapper::toDto);
  }

  @Transactional(readOnly = true)
  @Override
  public Page<DocumentDto> getAllMy(Jwt jwt, Pageable pageable) {
    Utilisateur id = utilisateurService.getMyEntityByJwt(jwt);
    return documentRepository.findAllByProprietaire(id, pageable).map(documentMapper::toDto);
  }

  @Transactional(rollbackFor = IOException.class)
  public DocumentDto uploadMyDocument(Jwt jwt, FileType fileType, MultipartFile file)
      throws IOException {
    Utilisateur id = utilisateurService.getMyEntityByJwt(jwt);
    Document sameTypeUploadedDocument = userHasSameTypeDocumentUploaded(id, fileType);
    String randomizedName = UUID.randomUUID().toString();
    Document entity =
        Document.builder()
            .etat(fileType.equals(FileType.IMAGE) ? EtatDocument.AUCUN : EtatDocument.EN_ATTENTE)
            .nomFichier(randomizedName)
            .nomSceau(fileType.getBucketName())
            .proprietaire(id)
            .build();
    documentRepository.save(entity);
    seaweedFsService.uploadFile(fileType.getBucketName(), randomizedName, file);
    if (sameTypeUploadedDocument != null) deleteAlreadyFetched(sameTypeUploadedDocument);
    return documentMapper.toDto(entity);
  }

  @Override
  public Page<DocumentDto> getAll(Pageable pageable) {
    return documentRepository.findAllBy(pageable).map(documentMapper::toDto);
  }

  @Override
  public Page<DocumentDto> getAllByStatus(EtatDocument etatDocument, Pageable pageable) {
    return documentRepository.findAllByEtat(etatDocument, pageable).map(documentMapper::toDto);
  }

  @Transactional(readOnly = true)
  @Override
  public DocumentDto getById(UUID id) {
    Document entity = ResourceFetcher.fetchResource(id, documentRepository, "Document");
    return documentMapper.toDto(entity);
  }

  @Transactional(readOnly = true)
  @Override
  public String getFileUrlById(UUID id) {
    Document entity = ResourceFetcher.fetchResource(id, documentRepository, "Document");
    return seaweedFsService.getFileUrl(
        entity.getNomSceau(), entity.getNomFichier(), FILE_URL_EXPIRY_DURATION_IN_SECONDS);
  }

  @Override
  public DocumentDto update(UUID id, DocumentDto dto) {
    Document entity = ResourceFetcher.fetchResource(id, documentRepository, "Document");
    documentMapper.updateEntityFromDto(dto, entity);
    entity = documentRepository.save(entity);
    return documentMapper.toDto(entity);
  }

  @Override
  public void delete(UUID id) {
    Document entity = ResourceFetcher.fetchResource(id, documentRepository, "Document");
    documentRepository.delete(entity);
    seaweedFsService.deleteFile(entity.getNomSceau(), entity.getNomFichier());
  }
}
