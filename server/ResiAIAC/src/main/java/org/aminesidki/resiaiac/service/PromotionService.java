package org.aminesidki.resiaiac.service;

import java.util.UUID;
import org.aminesidki.resiaiac.dto.PromotionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PromotionService {
  Page<PromotionDto> getAll(Pageable pageable);

  Page<PromotionDto> getAllByFiliere(Long id, Pageable pageable);

  PromotionDto save(PromotionDto dto);

  PromotionDto getById(UUID id);

  PromotionDto update(UUID id, PromotionDto dto);

  void delete(UUID id);
}
