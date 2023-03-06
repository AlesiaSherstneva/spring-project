package com.udemy.springcourse.integration;

import com.udemy.springcourse.config.TestConfig;
import com.udemy.springcourse.controllers.PeopleController;
import com.udemy.springcourse.dao.BookDAO;
import com.udemy.springcourse.dao.PersonDAO;
import com.udemy.springcourse.pojo.Book;
import com.udemy.springcourse.pojo.Person;
import com.udemy.springcourse.validators.UniquePersonValidator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@Sql({"/jdbc/drop-book.sql", "/jdbc/drop-person.sql", "/jdbc/create-person.sql",
        "/jdbc/create-book.sql", "/jdbc/insert-people.sql", "/jdbc/insert-books.sql"})
@WebAppConfiguration
@ContextConfiguration(classes = TestConfig.class)
@TestMethodOrder(MethodOrderer.Random.class)
@SuppressWarnings("unchecked")
public class FromEndToEndPeopleTest {
    private final PersonDAO personDAO;

    private final MockMvc mockMvc;
    private MvcResult mvcResult;
    private ModelAndView modelAndView;

    @Autowired
    public FromEndToEndPeopleTest(JdbcTemplate jdbcTemplate) {
        personDAO = new PersonDAO(jdbcTemplate);
        mockMvc = MockMvcBuilders.standaloneSetup(new PeopleController
                (personDAO, new BookDAO(jdbcTemplate), new UniquePersonValidator(personDAO))).build();
    }

    @Test
    public void showPeopleTest() throws Exception {
        mvcResult = mockMvc.perform(get("/people"))
                .andExpectAll(
                        model().size(1),
                        model().attributeExists("people"),
                        status().isOk(),
                        forwardedUrl("people/show"))
                .andReturn();

        modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);

        List<Person> receivedPeople = (List<Person>) modelAndView.getModel().get("people");
        assertEquals(3, receivedPeople.size());
    }

    @Test
    public void showPersonWithBooksTest() throws Exception {
        mvcResult = mockMvc.perform(get("/people/{id}", 1))
                .andExpectAll(
                        model().size(2),
                        model().attributeExists("person"),
                        model().attributeExists("books"),
                        status().isOk(),
                        forwardedUrl("people/profile"))
                .andReturn();

        modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);

        Person receivedPerson = (Person) modelAndView.getModel().get("person");
        assertEquals("Первый Тестовый Читатель", receivedPerson.getName());
        assertEquals(1990, receivedPerson.getYear());

        List<Book> receivedBooks = (List<Book>) modelAndView.getModel().get("books");
        assertEquals(1, receivedBooks.size());
        assertEquals("Второе название", receivedBooks.get(0).getTitle());
    }

    @Test
    public void showPersonWithoutBooksTest() throws Exception {
        mvcResult = mockMvc.perform(get("/people/{id}", 2))
                .andExpectAll(
                        model().size(1),
                        model().attributeExists("person"),
                        model().attributeDoesNotExist("books"),
                        status().isOk(),
                        forwardedUrl("people/profile"))
                .andReturn();

        modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);

        Person receivedPerson = (Person) modelAndView.getModel().get("person");
        assertEquals("Второй Тестовый Читатель", receivedPerson.getName());
        assertEquals(1975, receivedPerson.getYear());

        List<Book> receivedBooks = (List<Book>) modelAndView.getModel().get("books");
        assertNull(receivedBooks);
    }

    @Test
    public void createPersonWithNotUniqueNameTest() throws Exception {
        List<Person> peopleInBase = personDAO.showPeople();
        assertEquals(3, peopleInBase.size());

        Person personToSave = new Person();
        personToSave.setName("Третий Тестовый Читатель");
        personToSave.setYear(1982);

        mockMvc.perform(post("/people")
                        .flashAttr("person", personToSave))
                .andExpectAll(
                        model().size(1),
                        model().attribute("person", personToSave),
                        model().attributeErrorCount("person", 1),
                        model().attributeHasFieldErrors("person", "name"),
                        status().isOk(),
                        forwardedUrl("people/new")
                );

        peopleInBase = personDAO.showPeople();
        assertEquals(3, peopleInBase.size());
        assertFalse(peopleInBase.contains(personToSave));
    }

    @Test
    public void createPersonWithUniqueNameTest() throws Exception {
        List<Person> peopleInBase = personDAO.showPeople();
        assertEquals(3, peopleInBase.size());

        Person personToSave = new Person();
        personToSave.setName("Новый Тестовый Читатель");
        personToSave.setYear(1982);

        mockMvc.perform(post("/people")
                        .flashAttr("person", personToSave))
                .andExpectAll(
                        model().size(1),
                        model().attribute("person", personToSave),
                        model().attributeHasNoErrors("person"),
                        status().is3xxRedirection(),
                        redirectedUrl("/people")
                );

        peopleInBase = personDAO.showPeople();
        assertEquals(4, peopleInBase.size());
        assertTrue(peopleInBase.contains(personToSave));
    }

    @Test
    public void updatePersonWithNotUniqueNewNameTest() throws Exception {
        Person personToUpdate = personDAO.showPerson("Первый Тестовый Читатель");
        personToUpdate.setName("Третий Тестовый Читатель");

        mockMvc.perform(patch("/people/{id}", personToUpdate.getId())
                        .flashAttr("person", personToUpdate))
                .andExpectAll(
                        model().size(1),
                        model().attribute("person", personToUpdate),
                        model().attributeErrorCount("person", 1),
                        model().attributeHasFieldErrors("person", "name"),
                        status().isOk(),
                        forwardedUrl("people/edit")
                );

        int personId = personToUpdate.getId();
        personToUpdate = personDAO.showPerson(personId);
        assertEquals("Первый Тестовый Читатель", personToUpdate.getName());
    }

    @Test
    public void updatePersonWithNoChangedNameTest() throws Exception {
        Person personToUpdate = personDAO.showPerson("Третий Тестовый Читатель");
        personToUpdate.setYear(2002);

        mockMvc.perform(patch("/people/{id}", personToUpdate.getId())
                        .flashAttr("person", personToUpdate))
                .andExpectAll(
                        model().size(1),
                        model().attribute("person", personToUpdate),
                        model().attributeHasNoErrors("person"),
                        status().is3xxRedirection(),
                        redirectedUrl("/people")
                );

        int personId = personToUpdate.getId();
        personToUpdate = personDAO.showPerson(personId);
        assertEquals("Третий Тестовый Читатель", personToUpdate.getName());
        assertEquals(2002, personToUpdate.getYear());
    }

    @Test
    public void updatePersonWithUniqueNewNameTest() throws Exception {
        Person personToUpdate = personDAO.showPerson("Второй Тестовый Читатель");
        personToUpdate.setName("Новый Тестовый Читатель");

        mockMvc.perform(patch("/people/{id}", personToUpdate.getId())
                        .flashAttr("person", personToUpdate))
                .andExpectAll(
                        model().size(1),
                        model().attribute("person", personToUpdate),
                        model().attributeHasNoErrors("person"),
                        status().is3xxRedirection(),
                        redirectedUrl("/people")
                );

        int personId = personToUpdate.getId();
        personToUpdate = personDAO.showPerson(personId);
        assertEquals("Новый Тестовый Читатель", personToUpdate.getName());
    }

    @Test
    public void deletePersonTest() throws Exception {
        Person personToDelete = personDAO.showPerson("Третий Тестовый Читатель");

        List<Person> peopleInBase = personDAO.showPeople();
        assertEquals(3, peopleInBase.size());
        assertTrue(peopleInBase.contains(personToDelete));

        mockMvc.perform(delete("/people/{id}", personToDelete.getId()))
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/people")
                );

        peopleInBase = personDAO.showPeople();
        assertEquals(2, peopleInBase.size());
        assertFalse(peopleInBase.contains(personToDelete));
    }
}