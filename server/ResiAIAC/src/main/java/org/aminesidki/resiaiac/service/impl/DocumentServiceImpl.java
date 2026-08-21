package org.aminesidki.resiaiac.service.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.DocumentDto;
import org.aminesidki.resiaiac.entity.Document;
import org.aminesidki.resiaiac.entity.Utilisateur;
import org.aminesidki.resiaiac.mapper.DocumentMapper;
import org.aminesidki.resiaiac.repository.DocumentRepository;
import org.aminesidki.resiaiac.service.DocumentService;
import org.aminesidki.resiaiac.service.UtilisateurService;
import org.aminesidki.resiaiac.util.ResourceFetcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class DocumentServiceImpl implements DocumentService {

  private final UtilisateurService utilisateurService;
  private final DocumentRepository documentRepository;
  private final DocumentMapper documentMapper;

  @Transactional(readOnly = true)
  @Override
  public Page<DocumentDto> getAllMy(Jwt jwt, Pageable pageable) {
    Utilisateur id = utilisateurService.getMyEntity(jwt);
    return documentRepository.findAllByProprietaire(id, pageable).map(documentMapper::toDto);
  }

  @Override
  public DocumentDto save(DocumentDto dto) {
    Document entity = documentMapper.toEntity(dto);
    entity = documentRepository.save(entity);
    return documentMapper.toDto(entity);
  }

  @Transactional(readOnly = true)
  @Override
  public DocumentDto getById(UUID id) {
    Document entity = ResourceFetcher.fetchResource(id, documentRepository, "Document");
    return documentMapper.toDto(entity);
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
    documentRepository.delete(ResourceFetcher.fetchResource(id, documentRepository, "Document"));
  }
}
