package com.udemy.springcourse.validators;

import com.udemy.springcourse.pojo.Person;
import com.udemy.springcourse.repositories.PeopleRepository;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UniquePersonValidator implements Validator {
    private final PeopleRepository peopleRepository;

    public UniquePersonValidator(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }

    @Override
    public void validate(Object object, Errors errors) {
        Person person = (Person) object;
        if (peopleRepository.findByName(person.getName()) != null) {
            errors.rejectValue("name", "", "Читатель с таким ФИО уже существует");
        }
    }
}
