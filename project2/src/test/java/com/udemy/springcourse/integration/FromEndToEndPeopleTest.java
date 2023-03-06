package com.udemy.springcourse.integration;

import com.udemy.springcourse.config.TestConfig;
import com.udemy.springcourse.controllers.PeopleController;
import com.udemy.springcourse.pojo.Book;
import com.udemy.springcourse.pojo.Person;
import com.udemy.springcourse.repositories.BooksRepository;
import com.udemy.springcourse.repositories.PeopleRepository;
import com.udemy.springcourse.services.BookService;
import com.udemy.springcourse.services.PeopleService;
import com.udemy.springcourse.validators.UniquePersonValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private PeopleRepository peopleRepository;

    @Autowired
    private BooksRepository booksRepository;

    private MockMvc mockMvc;
    private MvcResult mvcResult;
    private ModelAndView modelAndView;

    @BeforeEach
    void setUp() {
        PeopleService peopleService = new PeopleService(peopleRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(new PeopleController(peopleService,
                new BookService(booksRepository), new UniquePersonValidator(peopleService))).build();
    }

    @Test
    public void showPeopleTest() throws Exception {
        mvcResult = mockMvc.perform(get("/people"))
                .andExpectAll(
                        model().size(1),
                        model().attributeExists("people"),
                        status().isOk())
                .andReturn();

        modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);
        assertEquals("people/show", modelAndView.getViewName());

        List<Person> receivedPeople = (List<Person>) modelAndView.getModel().get("people");
        assertEquals(3, receivedPeople.size());
    }

    @Test
    public void showPersonWithBooksTest() throws Exception {
        Person personWithBooks = peopleRepository.findByName("Первый Тестовый Читатель").orElse(null);

        if (personWithBooks != null) {
            mvcResult = mockMvc.perform(get("/people/{id}", personWithBooks.getId()))
                    .andExpectAll(
                            model().size(2),
                            model().attributeExists("person"),
                            model().attributeExists("books"),
                            status().isOk())
                    .andReturn();

            modelAndView = mvcResult.getModelAndView();
            assertNotNull(modelAndView);
            assertEquals("people/profile", modelAndView.getViewName());

            Person receivedPerson = (Person) modelAndView.getModel().get("person");
            assertEquals("Первый Тестовый Читатель", receivedPerson.getName());
            assertEquals(1990, receivedPerson.getYear());

            List<Book> receivedBooks = (List<Book>) modelAndView.getModel().get("books");
            assertEquals(1, receivedBooks.size());
            assertEquals("Второе название", receivedBooks.get(0).getTitle());
        } else {
            fail("Person should be received");
        }
    }

    @Test
    public void showPersonWithoutBooksTest() throws Exception {
        Person personWithoutBooks = peopleRepository.findByName("Второй Тестовый Читатель").orElse(null);

        if (personWithoutBooks != null) {
            mvcResult = mockMvc.perform(get("/people/{id}", personWithoutBooks.getId()))
                    .andExpectAll(
                            model().size(1),
                            model().attributeExists("person"),
                            model().attributeDoesNotExist("books"),
                            status().isOk())
                    .andReturn();

            ModelAndView modelAndView = mvcResult.getModelAndView();
            assertNotNull(modelAndView);
            assertEquals("people/profile", modelAndView.getViewName());

            Person receivedPerson = (Person) modelAndView.getModel().get("person");
            assertEquals("Второй Тестовый Читатель", receivedPerson.getName());
            assertEquals(1975, receivedPerson.getYear());

            List<Book> receivedBooks = (List<Book>) modelAndView.getModel().get("books");
            assertNull(receivedBooks);
        } else {
            fail("Person should be received");
        }
    }

    @Test
    public void createPersonWithNotUniqueNameTest() throws Exception {
        List<Person> peopleInBase = peopleRepository.findAll();
        assertEquals(3, peopleInBase.size());

        Person personToSave = new Person();
        personToSave.setName("Третий Тестовый Читатель");
        personToSave.setYear(1982);

        mvcResult = mockMvc.perform(post("/people")
                        .flashAttr("person", personToSave))
                .andExpectAll(
                        model().size(1),
                        model().attribute("person", personToSave),
                        model().attributeErrorCount("person", 1),
                        model().attributeHasFieldErrors("person", "name"),
                        status().isOk())
                .andReturn();

        modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);
        assertEquals("people/new", modelAndView.getViewName());

        peopleInBase = peopleRepository.findAll();
        assertEquals(3, peopleInBase.size());
    }

    @Test
    public void createPersonWithUniqueNameTest() throws Exception {
        List<Person> peopleInBase = peopleRepository.findAll();
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

        peopleInBase = peopleRepository.findAll();
        assertEquals(4, peopleInBase.size());
        assertEquals("Новый Тестовый Читатель", peopleInBase.get(3).getName());
        assertEquals(1982, peopleInBase.get(3).getYear());
    }

    @Test
    public void updatePersonWithNotUniqueNewNameTest() throws Exception {
        Person personToUpdate = peopleRepository.findByName("Первый Тестовый Читатель").orElse(null);

        if (personToUpdate != null) {
            personToUpdate.setName("Третий Тестовый Читатель");
            mvcResult = mockMvc.perform(patch("/people/{id}", personToUpdate.getId())
                            .flashAttr("person", personToUpdate))
                    .andExpectAll(
                            model().size(1),
                            model().attribute("person", personToUpdate),
                            model().attributeErrorCount("person", 1),
                            model().attributeHasFieldErrors("person", "name"),
                            status().isOk())
                    .andReturn();

            modelAndView = mvcResult.getModelAndView();
            assertNotNull(modelAndView);
            assertEquals("people/edit", modelAndView.getViewName());

            personToUpdate = peopleRepository.findById(personToUpdate.getId()).orElse(null);
            assertNotNull(personToUpdate);
            assertEquals("Первый Тестовый Читатель", personToUpdate.getName());
        } else {
            fail("Person should be received");
        }
    }

    @Test
    public void updatePersonWithNoChangedNameTest() throws Exception {
        Person personToUpdate = peopleRepository.findByName("Третий Тестовый Читатель").orElse(null);

        if (personToUpdate != null) {
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

            personToUpdate = peopleRepository.findById(personToUpdate.getId()).orElse(null);
            assertNotNull(personToUpdate);
            assertEquals(2002, personToUpdate.getYear());
        } else {
            fail("Person should be received");
        }
    }

    @Test
    public void updatePersonWithUniqueNewNameTest() throws Exception {
        Person personToUpdate = peopleRepository.findByName("Второй Тестовый Читатель").orElse(null);
        if (personToUpdate != null) {
            assertEquals("Второй Тестовый Читатель", personToUpdate.getName());

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

            personToUpdate = peopleRepository.findById(personToUpdate.getId()).orElse(null);
            assertNotNull(personToUpdate);
            assertEquals("Новый Тестовый Читатель", personToUpdate.getName());
        } else {
            fail("Person should be received");
        }
    }

    @Test
    public void deletePersonTest() throws Exception {
        Person personToDelete = peopleRepository.findByName("Третий Тестовый Читатель").orElse(null);
        if (personToDelete != null) {
            List<Person> peopleInBase = peopleRepository.findAll();
            assertEquals(3, peopleInBase.size());
            assertTrue(peopleInBase.contains(personToDelete));

            mockMvc.perform(delete("/people/{id}", personToDelete.getId()))
                    .andExpectAll(
                            status().is3xxRedirection(),
                            redirectedUrl("/people")
                    );

            peopleInBase = peopleRepository.findAll();
            assertEquals(2, peopleInBase.size());
            assertFalse(peopleInBase.contains(personToDelete));
        } else {
            fail("Person should be received");
        }
    }
}