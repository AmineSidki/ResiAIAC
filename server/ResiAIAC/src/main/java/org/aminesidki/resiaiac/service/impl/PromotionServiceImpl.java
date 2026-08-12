package org.aminesidki.resiaiac.service.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.PromotionDto;
import org.aminesidki.resiaiac.entity.Promotion;
import org.aminesidki.resiaiac.mapper.PromotionMapper;
import org.aminesidki.resiaiac.repository.PromotionRepository;
import org.aminesidki.resiaiac.service.PromotionService;
import org.aminesidki.resiaiac.util.ResourceFetcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class PromotionServiceImpl implements PromotionService {

  private final PromotionRepository promotionRepository;
  private final PromotionMapper promotionMapper;

  @Override
  public PromotionDto save(PromotionDto dto) {
    Promotion entity = promotionMapper.toEntity(dto);
    entity = promotionRepository.save(entity);
    return promotionMapper.toDto(entity);
  }

  @Transactional(readOnly = true)
  @Override
  public PromotionDto getById(UUID id) {
    Promotion entity = ResourceFetcher.fetchResource(id, promotionRepository, "Promotion");
    return promotionMapper.toDto(entity);
  }

  @Override
  public PromotionDto update(UUID id, PromotionDto dto) {
    Promotion entity = ResourceFetcher.fetchResource(id, promotionRepository, "Promotion");
    promotionMapper.updateEntityFromDto(dto, entity);
    entity = promotionRepository.save(entity);
    return promotionMapper.toDto(entity);
  }

  @Override
  public void delete(UUID id) {
    promotionRepository.delete(ResourceFetcher.fetchResource(id, promotionRepository, "Promotion"));
  }
}
