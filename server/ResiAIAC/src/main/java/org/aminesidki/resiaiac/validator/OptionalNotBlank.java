package org.aminesidki.resiaiac.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.aminesidki.resiaiac.validator.impl.OptionalNotBlankValidator;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = OptionalNotBlankValidator.class)
public @interface OptionalNotBlank {
  String message() default "ne doit pas etre vide si fourni !";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
