package org.aminesidki.resiaiac.validator;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.List;
import org.aminesidki.resiaiac.dto.PromotionDto;
import org.aminesidki.resiaiac.validator.impl.PromotionYearValidator;
import org.junit.jupiter.api.Test;

class PromotionYearValidatorTest {
  private final PromotionYearValidator validator = new PromotionYearValidator();

  @Test
  void isValid_consecutiveYears_returnsTrue() {
    PromotionDto dto = new PromotionDto(null, 2025L, 2026L, 3, 1L, List.of());
    assertThat(validator.isValid(dto, null)).isTrue();
  }

  @Test
  void isValid_nonConsecutiveYears_returnsFalse() {
    PromotionDto dto = new PromotionDto(null, 2025L, 2030L, 3, 1L, List.of());
    assertThat(validator.isValid(dto, null)).isFalse();
  }

  @Test
  void isValid_anneeDeDepartNull_returnsTrue() {
    PromotionDto dto = new PromotionDto(null, null, 2026L, 3, 1L, List.of());
    assertThat(validator.isValid(dto, null)).isTrue();
  }

  @Test
  void isValid_anneeDeFinNull_returnsTrue() {
    PromotionDto dto = new PromotionDto(null, 2025L, null, 3, 1L, List.of());
    assertThat(validator.isValid(dto, null)).isTrue();
  }
}
