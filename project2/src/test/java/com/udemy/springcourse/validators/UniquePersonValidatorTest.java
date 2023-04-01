package com.udemy.springcourse.validators;

import com.udemy.springcourse.config.TestConfig;
import com.udemy.springcourse.pojo.Person;
import com.udemy.springcourse.services.PeopleService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@Sql({"/jdbc/drop-book.sql", "/jdbc/drop-person.sql", "/jdbc/create-person.sql",
        "/jdbc/create-book.sql", "/jdbc/insert-people.sql", "/jdbc/insert-books.sql"})
@WebAppConfiguration
@ContextConfiguration(classes = TestConfig.class)
@TestMethodOrder(MethodOrderer.Random.class)
class UniquePersonValidatorTest {
    private final Person testPerson;
    private final UniquePersonValidator validator;
    private final Errors errors;

    @Autowired
    public UniquePersonValidatorTest(PeopleService peopleService) {
        testPerson = new Person();

        validator = new UniquePersonValidator(peopleService);
        errors = new BeanPropertyBindingResult(testPerson, "person");
    }

    @Test
    void supportsTest() {
        assertTrue(validator.supports(Person.class));
        assertFalse(validator.supports(Object.class));
    }

    @Test
    void personWithNotUniqueNameTest() {
        // given
/*        testPerson.setId(0);*/
        testPerson.setName("Второй Тестовый Читатель");
        // when
        validator.validate(testPerson, errors);
        // then
        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
    }

    @Test
    void samePersonTest() {
        // given
        testPerson.setId(3);
        testPerson.setName("Третий Тестовый Читатель");
        // when
        validator.validate(testPerson, errors);
        // then
        assertFalse(errors.hasErrors());
    }

    @Test
    public void personWithUniqueNameTest() {
        // given
/*        testPerson.setId(0);*/
        testPerson.setName("Новый Тестовый Читатель");
        // when
        validator.validate(testPerson, errors);
        // then
        assertFalse(errors.hasErrors());
    }
}