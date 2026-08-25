package org.aminesidki.resiaiac.service;

import java.util.List;
import java.util.UUID;
import org.aminesidki.resiaiac.dto.PromotionDto;

public interface PromotionService {
  List<PromotionDto> getAll();

  PromotionDto save(PromotionDto dto);

  PromotionDto getById(UUID id);

  PromotionDto update(UUID id, PromotionDto dto);

  void delete(UUID id);
}
