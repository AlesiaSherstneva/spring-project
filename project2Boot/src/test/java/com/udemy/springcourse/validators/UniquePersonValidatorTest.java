package com.udemy.springcourse.validators;

import com.udemy.springcourse.pojo.Person;
import com.udemy.springcourse.repositories.PeopleRepository;
import com.udemy.springcourse.services.PeopleService;
import com.udemy.springcourse.util.H2databaseInitTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.Random.class)
class UniquePersonValidatorTest extends H2databaseInitTest {
    private final Person testPerson;

    private final PeopleService peopleService;
    private final UniquePersonValidator validator;
    private final Errors errors;

    @Autowired
    public UniquePersonValidatorTest(PeopleRepository peopleRepository) {
        testPerson = new Person();
        peopleService = new PeopleService(peopleRepository);
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
        int sameId = peopleService.findOneByName("Третий Тестовый Читатель").getId();
        testPerson.setId(sameId);
        testPerson.setName("Третий Тестовый Читатель");
        // when
        validator.validate(testPerson, errors);
        // then
        assertFalse(errors.hasErrors());
    }

    @Test
    public void personWithUniqueNameTest() {
        // given
        testPerson.setName("Новый Тестовый Читатель");
        // when
        validator.validate(testPerson, errors);
        // then
        assertFalse(errors.hasErrors());
    }
}