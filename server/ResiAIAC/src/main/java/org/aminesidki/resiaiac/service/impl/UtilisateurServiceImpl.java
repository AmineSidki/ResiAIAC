package org.aminesidki.resiaiac.service.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.UtilisateurDto;
import org.aminesidki.resiaiac.entity.Utilisateur;
import org.aminesidki.resiaiac.mapper.UtilisateurMapper;
import org.aminesidki.resiaiac.repository.UtilisateurRepository;
import org.aminesidki.resiaiac.service.UtilisateurService;
import org.aminesidki.resiaiac.util.ResourceFetcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class UtilisateurServiceImpl implements UtilisateurService {

  private final UtilisateurRepository utilisateurRepository;
  private final UtilisateurMapper utilisateurMapper;

  @Override
  public Page<UtilisateurDto> getAll(Pageable pageable) {
    return utilisateurRepository.findAll(pageable).map(utilisateurMapper::toDto);
  }

  @Override
  public UtilisateurDto save(UtilisateurDto dto) {
    Utilisateur entity = utilisateurMapper.toEntity(dto);
    entity = utilisateurRepository.save(entity);
    return utilisateurMapper.toDto(entity);
  }

  @Transactional(readOnly = true)
  @Override
  public UtilisateurDto getById(UUID id) {
    Utilisateur entity = ResourceFetcher.fetchResource(id, utilisateurRepository, "Utilisateur");
    return utilisateurMapper.toDto(entity);
  }

  @Override
  public UtilisateurDto update(UUID id, UtilisateurDto dto) {
    Utilisateur entity = ResourceFetcher.fetchResource(id, utilisateurRepository, "Utilisateur");
    utilisateurMapper.updateEntityFromDto(dto, entity);
    entity = utilisateurRepository.save(entity);
    return utilisateurMapper.toDto(entity);
  }

  @Override
  public void delete(UUID id) {
    utilisateurRepository.delete(
        ResourceFetcher.fetchResource(id, utilisateurRepository, "Utilisateur"));
  }
}
