package org.aminesidki.resiaiac.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.FiliereDto;
import org.aminesidki.resiaiac.entity.Filiere;
import org.aminesidki.resiaiac.mapper.FiliereMapper;
import org.aminesidki.resiaiac.repository.FiliereRepository;
import org.aminesidki.resiaiac.service.FiliereService;
import org.aminesidki.resiaiac.util.ResourceFetcher;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
@CacheConfig(cacheNames = "filieres")
public class FiliereServiceImpl implements FiliereService {
  private final FiliereRepository filiereRepository;
  private final FiliereMapper filiereMapper;

  @Override
  public Filiere getEntity(Long id) {
    return ResourceFetcher.fetchResource(id, filiereRepository, "Filiere");
  }

  @Override
  @Cacheable(key = "'all'")
  public List<FiliereDto> getAll() {
    return filiereRepository.findAll().stream().map(filiereMapper::toDto).toList();
  }

  @Override
  @Caching(evict = {@CacheEvict(key = "'all'")})
  public FiliereDto save(FiliereDto dto) {
    Filiere entity = filiereMapper.toEntity(dto);
    entity = filiereRepository.save(entity);
    return filiereMapper.toDto(entity);
  }

  @Transactional(readOnly = true)
  @Override
  @Cacheable(key = "#id")
  public FiliereDto getById(Long id) {
    Filiere entity = ResourceFetcher.fetchResource(id, filiereRepository, "Filiere");
    return filiereMapper.toDto(entity);
  }

  @Override
  @Caching(evict = {@CacheEvict(key = "#id"), @CacheEvict(key = "'all'")})
  public FiliereDto update(Long id, FiliereDto dto) {
    Filiere entity = ResourceFetcher.fetchResource(id, filiereRepository, "Filiere");
    filiereMapper.updateEntityFromDto(dto, entity);
    entity = filiereRepository.save(entity);
    return filiereMapper.toDto(entity);
  }

  @Override
  @Caching(evict = {@CacheEvict(key = "#id"), @CacheEvict(key = "'all'")})
  public void delete(Long id) {
    filiereRepository.delete(ResourceFetcher.fetchResource(id, filiereRepository, "Filiere"));
  }
}
