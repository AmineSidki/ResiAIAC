package org.aminesidki.resiaiac.service;

import java.util.UUID;
import org.aminesidki.resiaiac.dto.PromotionDto;

public interface PromotionService {
  PromotionDto save(PromotionDto dto);

  PromotionDto getById(UUID id);

  PromotionDto update(UUID id, PromotionDto dto);

  void delete(UUID id);
}
