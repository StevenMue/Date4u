package com.tutego.date4u.service.validation;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Period;

public class BirthdateValidator implements ConstraintValidator<ValidBirthdate, LocalDate> {

    @Override
    public void initialize(ValidBirthdate constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(LocalDate dateTime, ConstraintValidatorContext constraintValidatorContext) {
        return Period.between(dateTime, LocalDate.now()).getYears() >= 18;
    }
}
