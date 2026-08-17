package org.aminesidki.resiaiac.validator.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.aminesidki.resiaiac.dto.PromotionDto;
import org.aminesidki.resiaiac.validator.ValidPromotionYears;

public class PromotionYearValidator
    implements ConstraintValidator<ValidPromotionYears, PromotionDto> {
  @Override
  public boolean isValid(PromotionDto value, ConstraintValidatorContext context) {
    if (value.anneeDeDepart() == null || value.anneeDeFin() == null) {
      return true;
    }

    return value.anneeDeDepart().equals(value.anneeDeFin() - 1);
  }
}
