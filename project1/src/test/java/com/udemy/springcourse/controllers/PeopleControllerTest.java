package com.udemy.springcourse.controllers;

import com.udemy.springcourse.dao.BookDAO;
import com.udemy.springcourse.dao.PersonDAO;
import com.udemy.springcourse.pojo.Book;
import com.udemy.springcourse.pojo.Person;
import com.udemy.springcourse.validators.UniquePersonValidator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.Random.class)
class PeopleControllerTest {
    private MockMvc mockMvc;

    @Mock
    private PersonDAO personDAO;

    @Mock
    private BookDAO bookDAO;

    private Person testPerson;
    private List<Person> testPeople;
    private List<Book> testBooks;

    private static final Random RANDOM = new Random();

    @BeforeEach
    public void setUp() {
        testPerson = new Person();
        testPeople = new ArrayList<>();
        testBooks = new ArrayList<>();

        mockMvc = MockMvcBuilders.standaloneSetup(new PeopleController
                (personDAO, bookDAO, mock(UniquePersonValidator.class))).build();
    }

    @Test
    public void showPeopleTest() throws Exception {
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
    public void showPersonWithoutBooksTest() throws Exception {
        when(personDAO.showPerson(anyInt())).thenReturn(testPerson);

        mockMvc.perform(get("/people/{id}", anyInt()))
                .andExpectAll(
                        model().size(1),
                        model().attribute("person", testPerson),
                        model().attributeDoesNotExist("books"),
                        status().isOk(),
                        forwardedUrl("people/profile")
                );

        verify(personDAO, times(1)).showPerson(anyInt());
        verify(bookDAO, times(1)).showBooksByPerson(anyInt());
    }

    @Test
    public void showPersonWithBooksTest() throws Exception {
        when(personDAO.showPerson(anyInt())).thenReturn(testPerson);
        when(bookDAO.showBooksByPerson(anyInt())).thenReturn(testBooks);

        testBooks.add(new Book());
        mockMvc.perform(get("/people/{id}", anyInt()))
                .andExpectAll(
                        model().size(2),
                        model().attribute("person", testPerson),
                        model().attribute("books", testBooks),
                        status().isOk(),
                        forwardedUrl("people/profile")
                );

        verify(personDAO, times(1)).showPerson(anyInt());
        verify(bookDAO, times(1)).showBooksByPerson(anyInt());
    }

    @Test
    public void addPersonTest() throws Exception {
        mockMvc.perform(get("/people/new"))
                .andExpectAll(
                        model().size(1),
                        model().attributeExists("person"),
                        status().isOk(),
                        forwardedUrl("people/new")
                );
    }

    @Test
    public void createEmptyPersonTest() throws Exception {
        mockMvc.perform(post("/people")
                        .flashAttr("person", testPerson))
                .andExpectAll(
                        model().size(1),
                        model().attribute("person", testPerson),
                        model().attributeErrorCount("person", 2),
                        status().isOk(),
                        forwardedUrl("people/new")
                );
    }

    @Test
    public void createPersonWithNotValidNameTest() throws Exception {
        testPerson.setName("Name Surname Patronymic");
        testPerson.setYear(1989);

        mockMvc.perform(post("/people")
                        .flashAttr("person", testPerson))
                .andExpectAll(
                        model().size(1),
                        model().attribute("person", testPerson),
                        model().attributeErrorCount("person", 1),
                        status().isOk(),
                        forwardedUrl("people/new")
                );
    }

    @Test
    public void createPersonWithNotValidYearTest() throws Exception {
        testPerson.setName("Фамилия Имя Отчество");

        // test a person with year earlier than 1900
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
    }

    @Test
    public void createAValidPersonTest() throws Exception {
        testPerson.setName("Фамилия Имя Отчество");
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
    public void editTest() throws Exception {
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
    public void updatePersonWithEmptyFields() throws Exception {
        testPerson.setId(RANDOM.nextInt());

        mockMvc.perform(patch("/people/{id}", testPerson.getId())
                        .flashAttr("person", testPerson))
                .andExpectAll(
                        model().size(1),
                        model().attribute("person", testPerson),
                        model().attributeErrorCount("person", 2),
                        status().isOk(),
                        forwardedUrl("people/edit")
                );
    }

    @Test
    public void updatePersonWithWrongFieldsTest() throws Exception {
        testPerson.setId(RANDOM.nextInt());
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
    }

    @Test
    public void updatePersonWithValidFieldsTest() throws Exception {
        testPerson.setId(RANDOM.nextInt());
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
    public void deletePersonTest() throws Exception {
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
    public void tearDown() {
        verifyNoMoreInteractions(personDAO);
    }
}