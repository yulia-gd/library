package org.example.library.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TwoWordsWithCapitalLetterValidator.class)
public @interface TwoWordsWithCapitalLetter {
    String message() default "The author should contain two capital words with name and\n" +
            "surname and space between.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
