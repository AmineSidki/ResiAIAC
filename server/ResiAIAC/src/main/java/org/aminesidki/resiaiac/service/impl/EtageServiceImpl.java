package org.aminesidki.resiaiac.service.impl;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.EtageDto;
import org.aminesidki.resiaiac.entity.Etage;
import org.aminesidki.resiaiac.mapper.EtageMapper;
import org.aminesidki.resiaiac.repository.EtageRepository;
import org.aminesidki.resiaiac.service.EtageService;
import org.aminesidki.resiaiac.util.ResourceFetcher;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
@CacheConfig(cacheNames = "etages")
public class EtageServiceImpl implements EtageService {
  private final EtageRepository etageRepository;
  private final EtageMapper etageMapper;

  @Override
  @Cacheable(key = "'all'")
  public List<EtageDto> getAll() {
    return etageRepository.findAll().stream().map(etageMapper::toDto).toList();
  }

  @Override
  @Caching(evict = {
          @CacheEvict(key = "'all'")
  })
  public EtageDto save(EtageDto dto) {
    Etage entity = etageMapper.toEntity(dto);
    entity = etageRepository.save(entity);
    return etageMapper.toDto(entity);
  }

  @Transactional(readOnly = true)
  @Override
  @Cacheable(key = "#id")
  public EtageDto getById(UUID id) {
    Etage etage = ResourceFetcher.fetchResource(id, etageRepository, "Etage");
    return etageMapper.toDto(etage);
  }

  @Override
  @Caching(evict = {
          @CacheEvict(key = "#id"),
          @CacheEvict(key = "'all'")
  })
  public EtageDto update(UUID id, EtageDto dto) {
    Etage entity = ResourceFetcher.fetchResource(id, etageRepository, "Etage");
    etageMapper.updateEntityFromDto(dto, entity);
    entity = etageRepository.save(entity);
    return etageMapper.toDto(entity);
  }

  @Override
  @Caching(evict = {
          @CacheEvict(key = "#id"),
          @CacheEvict(key = "'all'")
  })
  public void delete(UUID id) {
    etageRepository.delete(ResourceFetcher.fetchResource(id, etageRepository, "Etage"));
  }
}
