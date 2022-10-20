package com.udemy.springcourse.project2Boot.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(FIELD)
@Retention(RUNTIME)
@Constraint(validatedBy = CurrentYearValidator.class)
@Documented
public @interface CurrentYear {
    String message() default "Год не может быть больше текущего";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
