package com.udemy.springcourse.controllers;

import com.udemy.springcourse.config.SpringConfig;
import com.udemy.springcourse.dao.BookDAO;
import com.udemy.springcourse.dao.PersonDAO;
import com.udemy.springcourse.pojo.Book;
import com.udemy.springcourse.pojo.Person;
import com.udemy.springcourse.validators.UniquePersonValidator;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PeopleController.class)
@Import(UniquePersonValidator.class)
@ContextConfiguration(classes = SpringConfig.class)
@TestMethodOrder(MethodOrderer.Random.class)
class PeopleControllerTest {
    private MockMvc mockMvc;

    @Mock
    private PersonDAO personDAO;

    @Mock
    private BookDAO bookDAO;

    @InjectMocks
    private PeopleController peopleController;

    private Person testPerson;
    private List<Person> testPeople;
    private List<Book> testBooks;

    @BeforeEach
    void setUp() {
        testPerson = new Person();
        testPeople = new ArrayList<>();
        testBooks = new ArrayList<>();

        mockMvc = MockMvcBuilders.standaloneSetup(peopleController).build();
    }

    @Test
    void showPeopleTest() throws Exception {
        when(personDAO.showPeople()).thenReturn(testPeople);
        mockMvc.perform(get("/people"))
                .andExpectAll(
                        model().size(1),
                        model().attribute("people", testPeople),
                        status().isOk(),
                        forwardedUrl("people/show")
                );
        verify(personDAO, times(1)).showPeople();
    }

    @Test
    void showPersonTest() throws Exception {
        when(personDAO.showPerson(anyInt())).thenReturn(testPerson);
        when(bookDAO.showBooksByPerson(anyInt())).thenReturn(testBooks);

        //test with empty list of books
        mockMvc.perform(get("/people/{id}", anyInt()))
                .andExpectAll(
                        model().size(1),
                        model().attribute("person", testPerson),
                        model().attributeDoesNotExist("books"),
                        status().isOk(),
                        forwardedUrl("people/profile")
                );

        //test with not empty list of books
        testBooks.add(new Book());
        mockMvc.perform(get("/people/{id}", anyInt()))
                .andExpectAll(
                        model().size(2),
                        model().attribute("person", testPerson),
                        model().attribute("books", testBooks),
                        status().isOk(),
                        forwardedUrl("people/profile")
                );

        verify(personDAO, times(2)).showPerson(anyInt());
        verify(bookDAO, times(2)).showBooksByPerson(anyInt());
    }

    @Test
    void addPersonTest() throws Exception {
        mockMvc.perform(get("/people/new"))
                .andExpectAll(
                        model().size(1),
                        model().attributeExists("person"),
                        status().isOk(),
                        forwardedUrl("people/new")
                );
    }

    @Test
    void createPersonTest() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(new PeopleController(
                personDAO, bookDAO, mock(UniquePersonValidator.class))).build();

        // test empty new person
        mockMvc.perform(post("/people")
                        .flashAttr("person", testPerson))
                .andExpectAll(
                        model().size(1),
                        model().attribute("person", testPerson),
                        model().attributeErrorCount("person", 2),
                        status().isOk(),
                        forwardedUrl("people/new")
                );

        // test a person with not valid fields
        testPerson.setName("Name Surname Patronymic");
        testPerson.setYear(1800);
        mockMvc.perform(post("/people")
                        .flashAttr("person", testPerson))
                .andExpectAll(
                        model().size(1),
                        model().attribute("person", testPerson),
                        model().attributeErrorCount("person", 2),
                        status().isOk(),
                        forwardedUrl("people/new")
                );

        // test a person with year earlier than 1900
        testPerson.setName("Фамилия Имя Отчество");
        testPerson.setYear(1111);
        mockMvc.perform(post("/people")
                        .flashAttr("person", testPerson))
                .andExpectAll(
                        model().size(1),
                        model().attribute("person", testPerson),
                        model().attributeErrorCount("person", 1),
                        status().isOk(),
                        forwardedUrl("people/new")
                );

        // test a person with year later than current
        testPerson.setName("Фамилия Имя Отчество");
        testPerson.setYear(9999);
        mockMvc.perform(post("/people")
                        .flashAttr("person", testPerson))
                .andExpectAll(
                        model().size(1),
                        model().attribute("person", testPerson),
                        model().attributeErrorCount("person", 1),
                        status().isOk(),
                        forwardedUrl("people/new")
                );

        // test a person with valid fields
        testPerson.setYear(1956);
        mockMvc.perform(post("/people")
                        .flashAttr("person", testPerson))
                .andExpectAll(
                        model().size(1),
                        model().attribute("person", testPerson),
                        model().attributeErrorCount("person", 0),
                        status().is3xxRedirection(),
                        redirectedUrl("/people")
                );
        verify(personDAO, times(1)).save(any(Person.class));
    }

    @Test
    void editTest() throws Exception {
        when(personDAO.showPerson(anyInt())).thenReturn(testPerson);
        mockMvc.perform(get("/people/{id}/edit", anyInt()))
                .andExpectAll(
                        model().size(1),
                        model().attribute("person", testPerson),
                        status().isOk(),
                        forwardedUrl("people/edit")
                );
        verify(personDAO, times(1)).showPerson(anyInt());
    }

    @Test
    void updateTest() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(new PeopleController(
                personDAO, bookDAO, mock(UniquePersonValidator.class))).build();
        testPerson.setId(new Random().nextInt());

        // test empty new person
        mockMvc.perform(patch("/people/{id}", testPerson.getId())
                        .flashAttr("person", testPerson))
                .andExpectAll(
                        model().size(1),
                        model().attribute("person", testPerson),
                        model().attributeErrorCount("person", 2),
                        status().isOk(),
                        forwardedUrl("people/edit")
                );

        // test a person with not valid fields
        testPerson.setName("Name Surname Patronymic");
        testPerson.setYear(2345);
        mockMvc.perform(patch("/people/{id}", testPerson.getId())
                        .flashAttr("person", testPerson))
                .andExpectAll(
                        model().size(1),
                        model().attribute("person", testPerson),
                        model().attributeErrorCount("person", 2),
                        status().isOk(),
                        forwardedUrl("people/edit")
                );

        // test a person with valid fields
        testPerson.setName("Фамилия Имя Отчество");
        testPerson.setYear(2010);
        mockMvc.perform(patch("/people/{id}", testPerson.getId())
                        .flashAttr("person", testPerson))
                .andExpectAll(
                        model().size(1),
                        model().attribute("person", testPerson),
                        model().attributeErrorCount("person", 0),
                        status().is3xxRedirection(),
                        redirectedUrl("/people")
                );
        verify(personDAO, times(1)).update(anyInt(), any(Person.class));
    }

    @Test
    void deletePersonTest() throws Exception {
        for(int i = 0; i < 7; i++) {
            mockMvc.perform(delete("/people/{id}", anyInt()))
                    .andExpectAll(
                            status().is3xxRedirection(),
                            redirectedUrl("/people")
                    );
        }
        verify(personDAO, times(7)).delete(anyInt());
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(personDAO);
    }
}