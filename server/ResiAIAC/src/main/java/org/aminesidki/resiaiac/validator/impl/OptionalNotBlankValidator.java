package org.aminesidki.resiaiac.validator.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.aminesidki.resiaiac.validator.OptionalNotBlank;

public class OptionalNotBlankValidator implements ConstraintValidator<OptionalNotBlank, String> {
  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    return value == null || !value.isBlank();
  }
}
