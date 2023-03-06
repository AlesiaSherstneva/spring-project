package com.udemy.springcourse.validators;

import com.udemy.springcourse.config.TestConfig;
import com.udemy.springcourse.pojo.Person;
import com.udemy.springcourse.repositories.PeopleRepository;
import com.udemy.springcourse.services.PeopleService;
import com.udemy.springcourse.util.TestDataInit;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = TestConfig.class)
@Transactional
@TestMethodOrder(MethodOrderer.Random.class)
class UniquePersonValidatorTest {
    private final Person testPerson;
    private final TestDataInit dataInit;

    private final PeopleService peopleService;
    private final UniquePersonValidator validator;
    private final Errors errors;

    @Autowired
    public UniquePersonValidatorTest(PeopleRepository peopleRepository) {
        testPerson = new Person();
        dataInit = new TestDataInit();
        peopleService = new PeopleService(peopleRepository);
        validator = new UniquePersonValidator(peopleService);
        errors = new BeanPropertyBindingResult(testPerson, "person");
    }

    @BeforeEach
    void setUp() {
        for (Person person: dataInit.getTestPeople()) peopleService.save(person);
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