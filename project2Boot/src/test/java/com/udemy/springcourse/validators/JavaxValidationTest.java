package com.udemy.springcourse.validators;

import com.udemy.springcourse.pojo.Book;
import com.udemy.springcourse.pojo.Person;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.Random.class)
class JavaxValidationTest {
    private final Validator validator;

    public JavaxValidationTest() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void personValidationTest() {
        Person testPerson = new Person();
        Set<ConstraintViolation<Person>> violations;

        // test with empty person
        violations = validator.validate(testPerson);
        assertEquals(2, violations.size());

        // test a person with not valid name
        testPerson.setName("Name Surname Patronymic");
        violations = validator.validate(testPerson);
        assertEquals(2, violations.size());

        // test a person with valid name
        testPerson.setName("Фамилия Имя Отчество");
        violations = validator.validate(testPerson);
        assertEquals(1, violations.size());

        // test a person with year earlier than 1900
        testPerson.setYear(666);
        violations = validator.validate(testPerson);
        assertEquals(1, violations.size());

        // test a person with year later than current
        testPerson.setYear(6666);
        violations = validator.validate(testPerson);
        assertEquals(1, violations.size());

        // test a valid person
        testPerson.setYear(1975);
        violations = validator.validate(testPerson);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void bookValidationTest() {
        Book testBook = new Book();
        Set<ConstraintViolation<Book>> violations;

        // test with empty book
        violations = validator.validate(testBook);
        assertEquals(3, violations.size());

        // test a book with not valid title and author
        testBook.setTitle("Some title");
        testBook.setAuthor("Some Author");
        violations = validator.validate(testBook);
        assertEquals(3, violations.size());

        // test a book with valid title and author
        testBook.setTitle("Название");
        testBook.setAuthor("Фамилия Имя");
        violations = validator.validate(testBook);
        assertEquals(1, violations.size());

        // test a book with year earlier than 1445
        testBook.setYear(1);
        violations = validator.validate(testBook);
        assertEquals(1, violations.size());

        // test a book with year later than current
        testBook.setYear(3113);
        violations = validator.validate(testBook);
        assertEquals(1, violations.size());

        // test a valid book
        testBook.setYear(1891);
        violations = validator.validate(testBook);
        assertTrue(violations.isEmpty());
    }
}