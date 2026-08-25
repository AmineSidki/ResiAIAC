package org.aminesidki.resiaiac.service.impl;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.BatimentDto;
import org.aminesidki.resiaiac.entity.Batiment;
import org.aminesidki.resiaiac.mapper.BatimentMapper;
import org.aminesidki.resiaiac.repository.BatimentRepository;
import org.aminesidki.resiaiac.service.BatimentService;
import org.aminesidki.resiaiac.util.ResourceFetcher;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
@CacheConfig(cacheNames = "batiments")
public class BatimentServiceImpl implements BatimentService {

  private final BatimentRepository batimentRepository;
  private final BatimentMapper batimentMapper;

  @Override
  @Cacheable(key = "'all'")
  public List<BatimentDto> getAll() {
    return batimentRepository.findAll().stream().map(batimentMapper::toDto).toList();
  }

  @Override
  @CacheEvict(allEntries = true)
  public BatimentDto save(BatimentDto dto) {
    Batiment entity = batimentMapper.toEntity(dto);
    entity = batimentRepository.save(entity);
    return batimentMapper.toDto(entity);
  }

  @Transactional(readOnly = true)
  @Override
  @Cacheable(key = "#id")
  public BatimentDto getById(UUID id) {
    Batiment entity = ResourceFetcher.fetchResource(id, batimentRepository, "Batiment");
    return batimentMapper.toDto(entity);
  }

  @Override
  @CacheEvict(allEntries = true)
  /* OR --- @Caching(evict = {
             @CacheEvict(key = "#id"),
             @CacheEvict(key = "'all'")
  })
  */
  public BatimentDto update(UUID id, BatimentDto dto) {
    Batiment entity = ResourceFetcher.fetchResource(id, batimentRepository, "Batiment");
    batimentMapper.updateEntityFromDto(dto, entity);
    entity = batimentRepository.save(entity);
    return batimentMapper.toDto(entity);
  }

  @Override
  @CacheEvict(allEntries = true)
  public void delete(UUID id) {
    batimentRepository.delete(ResourceFetcher.fetchResource(id, batimentRepository, "Batiment"));
  }
}
