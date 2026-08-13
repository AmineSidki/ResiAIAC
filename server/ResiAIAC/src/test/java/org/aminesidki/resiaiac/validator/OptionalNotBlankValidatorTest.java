package org.aminesidki.resiaiac.validator;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.aminesidki.resiaiac.validator.impl.OptionalNotBlankValidator;
import org.junit.jupiter.api.Test;

class OptionalNotBlankValidatorTest {
  private final OptionalNotBlankValidator validator = new OptionalNotBlankValidator();

  @Test
  void isValid_null_returnsTrue() {
    assertThat(validator.isValid(null, null)).isTrue();
  }

  @Test
  void isValid_blank_returnsFalse() {
    assertThat(validator.isValid("   ", null)).isFalse();
  }

  @Test
  void isValid_nonBlank_returnsTrue() {
    assertThat(validator.isValid("Casablanca", null)).isTrue();
  }
}
