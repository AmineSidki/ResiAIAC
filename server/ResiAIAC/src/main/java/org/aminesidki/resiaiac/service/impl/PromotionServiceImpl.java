package org.aminesidki.resiaiac.service.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.PromotionDto;
import org.aminesidki.resiaiac.mapper.PromotionMapper;
import org.aminesidki.resiaiac.repository.PromotionRepository;
import org.aminesidki.resiaiac.service.PromotionService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PromotionServiceImpl implements PromotionService {

  private final PromotionRepository promotionRepository;
  private final PromotionMapper promotionMapper;

  @Override
  public PromotionDto save(PromotionDto dto) {
    return null;
  }

  @Override
  public PromotionDto getById(UUID id) {
    return null;
  }

  @Override
  public PromotionDto update(UUID id, PromotionDto dto) {
    return null;
  }

  @Override
  public void delete(UUID id) {}
}
