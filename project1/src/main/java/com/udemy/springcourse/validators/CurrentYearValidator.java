package com.udemy.springcourse.validators;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.YearMonth;

public class CurrentYearValidator implements ConstraintValidator<CurrentYear, Integer> {
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        int year = YearMonth.now().getYear();
        return value <= year;
    }
}
