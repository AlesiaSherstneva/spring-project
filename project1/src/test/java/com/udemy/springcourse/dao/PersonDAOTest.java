package com.udemy.springcourse.dao;

import com.udemy.springcourse.config.TestConfig;
import com.udemy.springcourse.pojo.Person;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@Sql({"/jdbc/drop-book.sql", "/jdbc/drop-person.sql",
        "/jdbc/create-person.sql", "/jdbc/insert-people.sql"})
@ContextConfiguration(classes = TestConfig.class)
@TestMethodOrder(MethodOrderer.Random.class)
class PersonDAOTest {
    private final PersonDAO personDAO;

    @Autowired
    public PersonDAOTest(JdbcTemplate jdbcTemplate) {
        personDAO = new PersonDAO(jdbcTemplate);
    }

    @Test
    void showPeopleTest() {
        // given, when
        List<Person> peopleInBase = personDAO.showPeople();
        // then
        assertEquals(3, peopleInBase.size());
    }

    @Test
    void showPersonByIdTest() {
        // given, when
        Person firstPerson = personDAO.showPerson(1);
        Person secondPerson = personDAO.showPerson(2);
        Person thirdPerson = personDAO.showPerson(3);
        Person missingPerson = personDAO.showPerson(4);
        // then
        assertEquals(1, firstPerson.getId());
        assertEquals("Второй Тестовый Читатель", secondPerson.getName());
        assertEquals(2004, thirdPerson.getYear());
        assertNull(missingPerson);
    }

    @Test
    void showPersonByNameTest() {
        // given, when
        Person firstPerson = personDAO.showPerson("Первый Тестовый Читатель");
        Person secondPerson = personDAO.showPerson("Второй Тестовый Читатель");
        Person thirdPerson = personDAO.showPerson("Третий Тестовый Читатель");
        Person missingPerson = personDAO.showPerson("Незарегистрированный Тестовый Читатель");
        // then
        assertEquals(1, firstPerson.getId());
        assertEquals("Второй Тестовый Читатель", secondPerson.getName());
        assertEquals(2004, thirdPerson.getYear());
        assertNull(missingPerson);
    }

    @Test
    void savePersonWithUniqueNameTest() {
        // given
        List<Person> peopleInBase = personDAO.showPeople();
        assertEquals(3, peopleInBase.size());

        Person testPerson = new Person();
        testPerson.setName("Новый Тестовый Читатель");
        testPerson.setYear(1982);
        // when
        personDAO.save(testPerson);
        // then
        peopleInBase = personDAO.showPeople();
        assertEquals(4, peopleInBase.size());

        Person receivedPerson = personDAO.showPerson(4);
        assertEquals(testPerson, receivedPerson);
    }

    @Test
    void savePersonWithNotUniqueNameTest() {
        // given
        List<Person> peopleInBase = personDAO.showPeople();
        assertEquals(3, peopleInBase.size());

        Person wrongPerson = new Person();
        wrongPerson.setName("Первый Тестовый Читатель");
        wrongPerson.setYear(1920);
        // when
        try {
            personDAO.save(wrongPerson);
            fail("There should be violation here");
        } catch (DuplicateKeyException exception) {
            String message = exception.getMessage();
            assertNotNull(message);
            assertTrue(message.contains("Unique index or primary key violation"));
        }
        // then
        peopleInBase = personDAO.showPeople();
        assertEquals(3, peopleInBase.size());

        Person gottenPerson = personDAO.showPerson(4);
        assertNull(gottenPerson);
    }

    @Test
    void updatePersonTest() {
        // given
        Person updatingPerson = personDAO.showPerson(2);
        assertEquals("Второй Тестовый Читатель", updatingPerson.getName());
        assertEquals(1975, updatingPerson.getYear());
        // when
        updatingPerson.setName("Новый Тестовый Читатель");
        updatingPerson.setYear(2010);
        personDAO.update(updatingPerson.getId(), updatingPerson);
        // then
        updatingPerson = personDAO.showPerson(2);
        assertEquals("Новый Тестовый Читатель", updatingPerson.getName());
        assertEquals(2010, updatingPerson.getYear());
    }

    @Test
    void deletePersonTest() {
        // given
        List<Person> peopleInBase = personDAO.showPeople();
        assertEquals(3, peopleInBase.size());
        // when
        personDAO.delete(1);
        // then
        peopleInBase = personDAO.showPeople();
        assertEquals(2, peopleInBase.size());

        Person deletedPerson = personDAO.showPerson(1);
        assertNull(deletedPerson);
    }
}