package com.udemy.springcourse.validators;

import com.udemy.springcourse.dao.PersonDAO;
import com.udemy.springcourse.pojo.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UniquePersonValidator implements Validator {
    private final PersonDAO personDAO;

    @Autowired
    public UniquePersonValidator(PersonDAO personDAO) {
        this.personDAO = personDAO;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }

    @Override
    public void validate(Object object, Errors errors) {
        Person person = (Person) object;
        if(personDAO.showPerson(person.getName()) != null
                && person.getId() != personDAO.showPerson(person.getName()).getId()) {
            errors.rejectValue("name", "", "Читатель с таким ФИО уже существует");
        }
    }
}
