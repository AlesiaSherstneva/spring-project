package com.udemy.springcourse.project2Boot.validators;

import com.udemy.springcourse.project2Boot.pojo.Person;
import com.udemy.springcourse.project2Boot.services.PeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UniquePersonValidator implements Validator {
    private final PeopleService peopleService;

    @Autowired
    public UniquePersonValidator(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }

    @Override
    public void validate(Object object, Errors errors) {
        Person person = (Person) object;
        Person personInBase = peopleService.findOneByName(person.getName());
        if (personInBase != null && person.getId() != personInBase.getId()) {
            errors.rejectValue("name", "", "Читатель с таким ФИО уже существует");
        }
    }
}
