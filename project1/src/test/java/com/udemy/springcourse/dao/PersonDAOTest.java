package com.udemy.springcourse.dao;

import com.udemy.springcourse.config.TestConfig;
import com.udemy.springcourse.pojo.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@DataJdbcTest
@ContextConfiguration(classes = TestConfig.class)
class PersonDAOTest {
    private final PersonDAO personDAO;
    private final JdbcTemplate jdbcTemplate;

    private Person testPerson;

    @Autowired
    public PersonDAOTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        personDAO = new PersonDAO(jdbcTemplate);
    }

    @BeforeEach
    void setUp() {
        testPerson = new Person();
        testPerson.setName("Test Person");
        testPerson.setYear(1982);

        jdbcTemplate.execute("CREATE TABLE Person (" +
                "id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY," +
                "name VARCHAR(250) UNIQUE," +
                "year INT NOT NULL CHECK ( year > 1900 AND year <= EXTRACT(year FROM now())))");
        jdbcTemplate.execute("INSERT INTO person(name, year) " +
                "VALUES ('Test Person1', 1990), ('Test Person2', 1975), ('Test Person3', 2004)");
    }

    @Test
    void showPeopleTest() {
        assertEquals(3, personDAO.showPeople().size());
    }

    @Test
    void showPersonByIdTest() {
        assertEquals(1, personDAO.showPerson(1).getId());
        assertEquals("Test Person2", personDAO.showPerson(2).getName());
        assertEquals(2004, personDAO.showPerson(3).getYear());
        assertNull(personDAO.showPerson(4));
    }

    @Test
    void showPersonByNameTest() {
        assertEquals(1, personDAO.showPerson("Test Person1").getId());
        assertEquals("Test Person2", personDAO.showPerson("Test Person2").getName());
        assertEquals(2004, personDAO.showPerson("Test Person3").getYear());
        assertNull(personDAO.showPerson("Some name"));
    }

    @Test
    void saveTest() {
        personDAO.save(testPerson);
        assertEquals(4, personDAO.showPeople().size());

        Person gottenPerson = personDAO.showPerson("Test Person");
        assertEquals(testPerson.getName(), gottenPerson.getName());
        assertEquals(testPerson.getYear(), gottenPerson.getYear());
    }

    @Test
    void updateTest() {
        Person updatedPerson = personDAO.showPerson(2);
        updatedPerson.setName("New Name");
        updatedPerson.setYear(2010);

        personDAO.update(updatedPerson.getId(), updatedPerson);
        updatedPerson = personDAO.showPerson(2);

        assertEquals("New Name", updatedPerson.getName());
        assertEquals(2010, updatedPerson.getYear());
    }

    @Test
    void deleteTest() {
        personDAO.delete(1);
        assertEquals(2, personDAO.showPeople().size());
        assertNull(personDAO.showPerson(1));
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS Person");
    }
}