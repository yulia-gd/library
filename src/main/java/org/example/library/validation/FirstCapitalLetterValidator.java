package org.example.library.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FirstCapitalLetterValidator implements ConstraintValidator<FirstCapitalLetter, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null || s.isEmpty()) {
            return false;
        }
        return Character.isUpperCase(s.charAt(0));
    }
}
