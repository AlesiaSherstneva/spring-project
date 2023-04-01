package com.udemy.springcourse.controllers;

import com.udemy.springcourse.pojo.Book;
import com.udemy.springcourse.pojo.Person;
import com.udemy.springcourse.services.BookService;
import com.udemy.springcourse.services.PeopleService;
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
    private PeopleService peopleService;

    @Mock
    private BookService bookService;

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
                (peopleService, bookService, mock(UniquePersonValidator.class))).build();
    }

    @Test
    public void showPeopleTest() throws Exception {
        when(peopleService.findAll()).thenReturn(testPeople);

        mockMvc.perform(get("/people"))
                .andExpectAll(
                        model().size(1),
                        model().attribute("people", testPeople),
                        status().isOk(),
                        forwardedUrl("people/show")
                );

        verify(peopleService, times(1)).findAll();
    }

    @Test
    public void showPersonWithoutBooksTest() throws Exception {
        when(peopleService.findOneById(anyInt())).thenReturn(testPerson);
        when(bookService.findByReader(any(Person.class))).thenReturn(testBooks);

        mockMvc.perform(get("/people/{id}", anyInt()))
                .andExpectAll(
                        model().size(1),
                        model().attribute("person", testPerson),
                        model().attributeDoesNotExist("books"),
                        status().isOk(),
                        forwardedUrl("people/profile")
                );

        verify(peopleService, times(1)).findOneById(anyInt());
        verify(bookService, times(1)).findByReader(any(Person.class));
    }

    @Test
    public void showPersonWithABookTest() throws Exception {
        when(peopleService.findOneById(anyInt())).thenReturn(testPerson);
        when(bookService.findByReader(any(Person.class))).thenReturn(testBooks);

        testBooks.add(new Book());
        mockMvc.perform(get("/people/{id}", anyInt()))
                .andExpectAll(
                        model().size(2),
                        model().attribute("person", testPerson),
                        model().attribute("books", testBooks),
                        status().isOk(),
                        forwardedUrl("people/profile")
                );

        verify(peopleService, times(1)).findOneById(anyInt());
        verify(bookService, times(1)).findByReader(any(Person.class));
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
    }

    @Test
    public void createPersonWithNotValidYearTest() throws Exception {
        testPerson.setName("Фамилия Имя Отчество");

        // test a person with year earlier than 1900
        testPerson.setYear(1000);
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
        testPerson.setYear(9000);
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
    public void createPersonWithValidFieldsTest() throws Exception {
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

        verify(peopleService, times(1)).save(any(Person.class));
    }

    @Test
    public void editTest() throws Exception {
        when(peopleService.findOneById(anyInt())).thenReturn(testPerson);

        mockMvc.perform(get("/people/{id}/edit", anyInt()))
                .andExpectAll(
                        model().size(1),
                        model().attribute("person", testPerson),
                        status().isOk(),
                        forwardedUrl("people/edit")
                );

        verify(peopleService, times(1)).findOneById(anyInt());
    }

    @Test
    public void updatePersonWithEmptyFieldsTest() throws Exception {
        testPerson.setId(RANDOM.nextInt(100));

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
    public void updatePersonWithNotValidFieldsTest() throws Exception {
        testPerson.setId(RANDOM.nextInt(100));

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
        testPerson.setId(RANDOM.nextInt(100));
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

        verify(peopleService, times(1)).update(anyInt(), any(Person.class));
    }

    @Test
    public void deletePersonTest() throws Exception {
        mockMvc.perform(delete("/people/{id}", anyInt()))
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/people")
                );

        verify(peopleService, times(1)).delete(anyInt());
    }

    @AfterEach
    public void tearDown() {
        verifyNoMoreInteractions(peopleService, bookService);
    }
}