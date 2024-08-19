package org.example.library.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FirstCapitalLetterValidator.class)
public @interface FirstCapitalLetter {
    String message() default "The book name must start with a capital letter";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
