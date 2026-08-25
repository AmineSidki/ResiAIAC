package org.aminesidki.resiaiac.service.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.PromotionDto;
import org.aminesidki.resiaiac.entity.Filiere;
import org.aminesidki.resiaiac.entity.Promotion;
import org.aminesidki.resiaiac.mapper.PromotionMapper;
import org.aminesidki.resiaiac.repository.PromotionRepository;
import org.aminesidki.resiaiac.service.FiliereService;
import org.aminesidki.resiaiac.service.PromotionService;
import org.aminesidki.resiaiac.util.ResourceFetcher;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
@CacheConfig(cacheNames = "promotions")
public class PromotionServiceImpl implements PromotionService {
  private final FiliereService filiereService;
  private final PromotionRepository promotionRepository;
  private final PromotionMapper promotionMapper;

  @Override
  @Cacheable(key = "'page-' + #pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort")
  public Page<PromotionDto> getAll(Pageable pageable) {
    return promotionRepository.findAllBy(pageable).map(promotionMapper::toDto);
  }

  @Override
  @Cacheable(key = "'filiere-' + #id + '-' + #pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort")
  public Page<PromotionDto> getAllByFiliere(Long id, Pageable pageable) {
    Filiere filiere = filiereService.getEntity(id);
    return promotionRepository.findAllByFiliere(filiere, pageable).map(promotionMapper::toDto);
  }

  @Override
  @CacheEvict(allEntries = true)
  public PromotionDto save(PromotionDto dto) {
    Promotion entity = promotionMapper.toEntity(dto);
    entity = promotionRepository.save(entity);
    return promotionMapper.toDto(entity);
  }

  @Transactional(readOnly = true)
  @Override
  @Cacheable(key = "#id")
  public PromotionDto getById(UUID id) {
    Promotion entity = ResourceFetcher.fetchResource(id, promotionRepository, "Promotion");
    return promotionMapper.toDto(entity);
  }

  @Override
  @CacheEvict(allEntries = true)
  public PromotionDto update(UUID id, PromotionDto dto) {
    Promotion entity = ResourceFetcher.fetchResource(id, promotionRepository, "Promotion");
    promotionMapper.updateEntityFromDto(dto, entity);
    entity = promotionRepository.save(entity);
    return promotionMapper.toDto(entity);
  }

  @Override
  @CacheEvict(allEntries = true)
  public void delete(UUID id) {
    promotionRepository.delete(ResourceFetcher.fetchResource(id, promotionRepository, "Promotion"));
  }
}
