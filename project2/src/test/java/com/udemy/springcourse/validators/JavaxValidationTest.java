package com.udemy.springcourse.validators;

import com.udemy.springcourse.pojo.Book;
import com.udemy.springcourse.pojo.Person;
import org.junit.jupiter.api.BeforeEach;
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
public class JavaxValidationTest {
    private Validator validator;

    @BeforeEach
    public void setUp() {
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
        testPerson.setName("Any Wrong Name");
        violations = validator.validate(testPerson);
        assertEquals(2, violations.size());

        // test a person with valid name
        testPerson.setName("Фамилия Имя Отчество");
        violations = validator.validate(testPerson);
        assertEquals(1, violations.size());

        // test a person with year earlier than 1900
        testPerson.setYear(1414);
        violations = validator.validate(testPerson);
        assertEquals(1, violations.size());

        // test a person with year later than current
        testPerson.setYear(4444);
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
        testBook.setTitle("Any Wrong Title");
        testBook.setAuthor("Wrong Author");
        violations = validator.validate(testBook);
        assertEquals(3, violations.size());

        // test a book with valid title and author
        testBook.setTitle("Название книги");
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