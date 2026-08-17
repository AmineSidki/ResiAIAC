package org.aminesidki.resiaiac.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.aminesidki.resiaiac.validator.impl.PromotionYearValidator;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PromotionYearValidator.class)
public @interface ValidPromotionYears {
  String message() default "AnneeDeFin must be one year after AnneeDeDepart";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
