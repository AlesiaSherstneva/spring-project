package com.udemy.springcourse.validators;

import com.udemy.springcourse.config.EmptyConfig;
import com.udemy.springcourse.config.TestConfig;
import com.udemy.springcourse.dao.PersonDAO;
import com.udemy.springcourse.pojo.Person;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import static org.junit.jupiter.api.Assertions.*;

@DataJdbcTest
@ContextConfiguration(classes = EmptyConfig.class)
@TestMethodOrder(MethodOrderer.Random.class)
class UniquePersonValidatorTest {
    private final PersonDAO personDAO;
    private final JdbcTemplate jdbcTemplate;

    private UniquePersonValidator validator;
    private Errors errors;

    private Person testPerson;

    @Autowired
    public UniquePersonValidatorTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        personDAO = new PersonDAO(jdbcTemplate);
    }

    @BeforeEach
    void setUp() {
        validator = new UniquePersonValidator(personDAO);

        jdbcTemplate.execute("CREATE TABLE Person (" +
                "id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY," +
                "name VARCHAR(250) UNIQUE," +
                "year INT NOT NULL CHECK ( year > 1900 AND year <= EXTRACT(year FROM now())))");
        jdbcTemplate.execute("INSERT INTO person(name, year) VALUES ('Test Person', 1982)");

        testPerson = new Person();
    }

    @Test
    public void supportsTest() {
        assertTrue(validator.supports(Person.class));
        assertFalse(validator.supports(Object.class));
    }

    @Test
    public void personWithNotUniqueNameTest() {
        testPerson.setName("Test Person");
        errors = new BeanPropertyBindingResult(testPerson, "person");

        validator.validate(testPerson, errors);
        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
    }

    @Test
    void samePersonTest() {
        testPerson.setName("Test Person");
        testPerson.setId(1);
        errors = new BeanPropertyBindingResult(testPerson, "person");

        validator.validate(testPerson, errors);
        assertFalse(errors.hasErrors());
    }

    @Test
    public void personWithUniqueNameTest() {
        testPerson.setName("Another Test Person");
        errors = new BeanPropertyBindingResult(testPerson, "person");

        validator.validate(testPerson, errors);
        assertFalse(errors.hasErrors());
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS Person");
    }
}