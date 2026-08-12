package org.aminesidki.resiaiac.service.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.EtageDto;
import org.aminesidki.resiaiac.entity.Etage;
import org.aminesidki.resiaiac.mapper.EtageMapper;
import org.aminesidki.resiaiac.repository.EtageRepository;
import org.aminesidki.resiaiac.service.EtageService;
import org.aminesidki.resiaiac.util.ResourceFetcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class EtageServiceImpl implements EtageService {
  private final EtageRepository etageRepository;
  private final EtageMapper etageMapper;

  @Override
  public EtageDto save(EtageDto dto) {
    Etage entity = etageMapper.toEntity(dto);
    entity = etageRepository.save(entity);
    return etageMapper.toDto(entity);
  }

  @Transactional(readOnly = true)
  @Override
  public EtageDto getById(UUID id) {
    Etage etage = ResourceFetcher.fetchResource(id, etageRepository, "Etage");
    return etageMapper.toDto(etage);
  }

  @Override
  public EtageDto update(UUID id, EtageDto dto) {
    Etage entity = ResourceFetcher.fetchResource(id, etageRepository, "Etage");
    etageMapper.updateEntityFromDto(dto, entity);
    entity = etageRepository.save(entity);
    return etageMapper.toDto(entity);
  }

  @Override
  public void delete(UUID id) {
    etageRepository.delete(ResourceFetcher.fetchResource(id, etageRepository, "Etage"));
  }
}
