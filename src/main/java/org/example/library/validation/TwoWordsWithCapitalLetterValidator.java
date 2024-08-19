package org.example.library.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TwoWordsWithCapitalLetterValidator implements ConstraintValidator<TwoWordsWithCapitalLetter, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null || s.isEmpty()) {
            return false;
        }

        String[] words = s.split(" ");

        if (words.length != 2) {
            return false;
        }

        return Character.isUpperCase(words[0].charAt(0)) && Character.isUpperCase(words[1].charAt(0));
    }

}
