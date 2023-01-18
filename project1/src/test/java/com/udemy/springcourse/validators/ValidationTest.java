package com.udemy.springcourse.validators;

import com.udemy.springcourse.pojo.Book;
import com.udemy.springcourse.pojo.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
public class ValidationTest {
    private Validator validator;

    @BeforeEach
    public void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void personValidationTest() {
        Person testPerson = new Person();

        // test with empty person
        Set<ConstraintViolation<Person>> violations = validator.validate(testPerson);
        assertEquals(2, violations.size());

        // test a person with not valid name
        testPerson.setName("Any Wrong Name");
        violations = validator.validate(testPerson);
        assertEquals(2, violations.size());

        // test a person with valid name
        testPerson.setName("Фамилия Имя Отчество");
        violations = validator.validate(testPerson);
        assertEquals(1, violations.size());

        // test a person with not valid year
        testPerson.setYear(1785);
        violations = validator.validate(testPerson);
        assertEquals(1, violations.size());

        // test a valid person
        testPerson.setYear(1910);
        violations = validator.validate(testPerson);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void bookValidationTest() {
        Book testBook = new Book();

        // test with empty book
        Set<ConstraintViolation<Book>> violations = validator.validate(testBook);
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
        testBook.setYear(3000);
        violations = validator.validate(testBook);
        assertEquals(1, violations.size());

        // test a valid book
        testBook.setYear(1950);
        violations = validator.validate(testBook);
        assertTrue(violations.isEmpty());
    }
}