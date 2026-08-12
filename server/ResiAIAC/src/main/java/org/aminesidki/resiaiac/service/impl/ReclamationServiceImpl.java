package org.aminesidki.resiaiac.service.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.ReclamationDto;
import org.aminesidki.resiaiac.entity.Reclamation;
import org.aminesidki.resiaiac.mapper.ReclamationMapper;
import org.aminesidki.resiaiac.repository.ReclamationRepository;
import org.aminesidki.resiaiac.service.ReclamationService;
import org.aminesidki.resiaiac.util.ResourceFetcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class ReclamationServiceImpl implements ReclamationService {

  private final ReclamationRepository reclamationRepository;
  private final ReclamationMapper reclamationMapper;

  @Override
  public ReclamationDto save(ReclamationDto dto) {
    Reclamation entity = reclamationMapper.toEntity(dto);
    entity = reclamationRepository.save(entity);
    return reclamationMapper.toDto(entity);
  }

  @Transactional(readOnly = true)
  @Override
  public ReclamationDto getById(UUID id) {
    Reclamation entity = ResourceFetcher.fetchResource(id, reclamationRepository, "Reclamation");
    return reclamationMapper.toDto(entity);
  }

  @Override
  public ReclamationDto update(UUID id, ReclamationDto dto) {
    Reclamation entity = ResourceFetcher.fetchResource(id, reclamationRepository, "Reclamation");
    reclamationMapper.updateEntityFromDto(dto, entity);
    entity = reclamationRepository.save(entity);
    return reclamationMapper.toDto(entity);
  }

  @Override
  public void delete(UUID id) {
    reclamationRepository.delete(
        ResourceFetcher.fetchResource(id, reclamationRepository, "Reclamation"));
  }
}
