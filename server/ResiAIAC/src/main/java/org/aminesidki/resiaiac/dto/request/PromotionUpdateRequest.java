package org.aminesidki.resiaiac.dto.request;

import java.util.UUID;
import org.aminesidki.resiaiac.dto.PromotionDto;

public record PromotionUpdateRequest(UUID id, PromotionDto dto) {}
