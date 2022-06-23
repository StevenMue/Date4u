package com.tutego.date4u.service.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target( { ElementType.FIELD, ElementType.TYPE,
        ElementType.ANNOTATION_TYPE } )
@Retention(RUNTIME)
@Constraint(validatedBy = BirthdateValidator.class)
@Documented
public @interface ValidBirthdate {
    String message() default "Invalid Birthdate";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
