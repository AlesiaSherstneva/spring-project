package com.udemy.springcourse.validators;

import com.udemy.springcourse.pojo.Person;
import com.udemy.springcourse.repositories.PeopleRepository;
import com.udemy.springcourse.services.PeopleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestMethodOrder(MethodOrderer.Random.class)
class UniquePersonValidatorTest {
    @Autowired
    private TestEntityManager entityManager;

    private final PeopleService peopleService;
    private final UniquePersonValidator validator;
    private Errors errors;

    private final Person testPerson;

    @Autowired
    public UniquePersonValidatorTest(PeopleRepository peopleRepository) {
        peopleService = new PeopleService(peopleRepository);
        validator = new UniquePersonValidator(peopleService);
        testPerson = new Person();
    }

    @BeforeEach
    void setUp() {
        Person personInBase = new Person();
        personInBase.setName("Фамилия Имя Отчество");
        personInBase.setYear(1992);
        entityManager.persist(personInBase);
    }

    @Test
    void supportsTest() {
        assertTrue(validator.supports(Person.class));
        assertFalse(validator.supports(Object.class));
    }

    @Test
    void personWithNotUniqueNameTest() {
        // given
        testPerson.setName("Фамилия Имя Отчество");
        errors = new BeanPropertyBindingResult(testPerson, "person");
        // when
        validator.validate(testPerson, errors);
        // then
        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
    }

    @Test
    void samePersonTest() {
        // given
        int sameId = peopleService.findOneByName("Фамилия Имя Отчество").getId();
        testPerson.setId(sameId);
        testPerson.setName("Фамилия Имя Отчество");
        errors = new BeanPropertyBindingResult(testPerson, "person");
        // when
        validator.validate(testPerson, errors);
        // then
        assertFalse(errors.hasErrors());
    }

    @Test
    public void personWithUniqueNameTest() {
        // given
        testPerson.setName("Тест Тест Тест");
        errors = new BeanPropertyBindingResult(testPerson, "person");
        // when
        validator.validate(testPerson, errors);
        // then
        assertFalse(errors.hasErrors());
    }
}