package org.aminesidki.resiaiac.service;

import org.aminesidki.resiaiac.dto.PromotionDto;

import java.util.UUID;

public interface PromotionService {
    PromotionDto save(PromotionDto dto);
    PromotionDto getById(UUID id);
    PromotionDto update(UUID id, PromotionDto dto);
    void delete(UUID id);

}
