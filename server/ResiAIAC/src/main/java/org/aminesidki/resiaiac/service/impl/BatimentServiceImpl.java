package org.aminesidki.resiaiac.service.impl;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.BatimentDto;
import org.aminesidki.resiaiac.entity.Batiment;
import org.aminesidki.resiaiac.mapper.BatimentMapper;
import org.aminesidki.resiaiac.repository.BatimentRepository;
import org.aminesidki.resiaiac.service.BatimentService;
import org.aminesidki.resiaiac.util.ResourceFetcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class BatimentServiceImpl implements BatimentService {

  private final BatimentRepository batimentRepository;
  private final BatimentMapper batimentMapper;

  @Override
  public BatimentDto save(BatimentDto dto) {
    Batiment entity = batimentMapper.toEntity(dto);
    entity = batimentRepository.save(entity);
    return batimentMapper.toDto(entity);
  }

  @Transactional(readOnly = true)
  @Override
  public BatimentDto getById(UUID id) {
    Batiment entity = ResourceFetcher.fetchResource(id, batimentRepository, "Batiment");
    return batimentMapper.toDto(entity);
  }

  @Override
  public BatimentDto update(UUID id, BatimentDto dto) {
    Batiment entity = ResourceFetcher.fetchResource(id, batimentRepository, "Batiment");
    batimentMapper.updateEntityFromDto(dto, entity);
    entity = batimentRepository.save(entity);
    return batimentMapper.toDto(entity);
  }

  @Override
  public void delete(UUID id) {
    batimentRepository.delete(ResourceFetcher.fetchResource(id, batimentRepository, "Batiment"));
  }
}
