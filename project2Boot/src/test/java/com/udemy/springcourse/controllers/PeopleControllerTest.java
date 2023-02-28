package com.udemy.springcourse.controllers;

import com.udemy.springcourse.pojo.Book;
import com.udemy.springcourse.pojo.Person;
import com.udemy.springcourse.services.BookService;
import com.udemy.springcourse.services.PeopleService;
import com.udemy.springcourse.validators.UniquePersonValidator;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Import(UniquePersonValidator.class)
@TestMethodOrder(MethodOrderer.Random.class)
class PeopleControllerTest {
    private MockMvc mockMvc;

    @MockBean
    private PeopleService peopleService;

    @MockBean
    private BookService bookService;

    @Autowired
    private PeopleController peopleController;

    private Person testPerson;
    private List<Person> testPeople;
    private List<Book> testBooks;

    @BeforeEach
    void setUp() {
        testPerson = new Person();
        testPerson.setId(new Random().nextInt(200));
        testPeople = new ArrayList<>();
        testBooks = new ArrayList<>();

        mockMvc = MockMvcBuilders.standaloneSetup(peopleController).build();
    }

    @Test
    void showPeopleTest() throws Exception {
        when(peopleService.findAll()).thenReturn(testPeople);
        mockMvc.perform(get("/library/people"))
                .andExpectAll(
                        model().size(1),
                        model().attribute("people", testPeople),
                        status().isOk(),
                        forwardedUrl("people/show")
                );
        verify(peopleService, times(1)).findAll();
    }

    @Test
    void showPersonTest() throws Exception {
        when(peopleService.findOneById(anyInt())).thenReturn(testPerson);
        when(bookService.findByReader(any(Person.class))).thenReturn(testBooks);

        //test with empty list of books
        mockMvc.perform(get("/library/people/{id}", anyInt()))
                .andExpectAll(
                        model().size(1),
                        model().attribute("person", testPerson),
                        model().attributeDoesNotExist("books"),
                        status().isOk(),
                        forwardedUrl("people/profile")
                );

        //test with not empty list of books
        testBooks.add(new Book());
        mockMvc.perform(get("/library/people/{id}", anyInt()))
                .andExpectAll(
                        model().size(2),
                        model().attribute("person", testPerson),
                        model().attribute("books", testBooks),
                        status().isOk(),
                        forwardedUrl("people/profile")
                );

        verify(peopleService, times(2)).findOneById(anyInt());
        verify(bookService, times(2)).findByReader(any(Person.class));
    }

    @Test
    void addPersonTest() throws Exception {
        mockMvc.perform(get("/library/people/new"))
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
                peopleService, bookService, mock(UniquePersonValidator.class))).build();

        // test an empty new person
        mockMvc.perform(post("/library/people")
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
        testPerson.setYear(1550);
        mockMvc.perform(post("/library/people")
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
        mockMvc.perform(post("/library/people")
                        .flashAttr("person", testPerson))
                .andExpectAll(
                        model().size(1),
                        model().attribute("person", testPerson),
                        model().attributeErrorCount("person", 1),
                        status().isOk(),
                        forwardedUrl("people/new")
                );

        // test a person with year later than current
        testPerson.setYear(2210);
        mockMvc.perform(post("/library/people")
                        .flashAttr("person", testPerson))
                .andExpectAll(
                        model().size(1),
                        model().attribute("person", testPerson),
                        model().attributeErrorCount("person", 1),
                        status().isOk(),
                        forwardedUrl("people/new")
                );

        // test a person with valid fields
        testPerson.setYear(2023);
        mockMvc.perform(post("/library/people")
                        .flashAttr("person", testPerson))
                .andExpectAll(
                        model().size(1),
                        model().attribute("person", testPerson),
                        model().attributeHasNoErrors("person"),
                        status().is3xxRedirection(),
                        redirectedUrl("/library/people")
                );
        verify(peopleService, times(1)).save(any(Person.class));
    }

    @Test
    void editTest() throws Exception {
        when(peopleService.findOneById(anyInt())).thenReturn(testPerson);
        mockMvc.perform(get("/library/people/{id}/edit", anyInt()))
                .andExpectAll(
                        model().size(1),
                        model().attribute("person", testPerson),
                        status().isOk(),
                        forwardedUrl("people/edit")
                );
        verify(peopleService, times(1)).findOneById(anyInt());
    }

    @Test
    void updateTest() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(new PeopleController(
                peopleService, bookService, mock(UniquePersonValidator.class))).build();
        testPerson.setId(new Random().nextInt(100));

        // test an empty new person
        mockMvc.perform(patch("/library/people/{id}", testPerson.getId())
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
        testPerson.setYear(5005);
        mockMvc.perform(patch("/library/people/{id}", testPerson.getId())
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
        testPerson.setYear(1999);
        mockMvc.perform(patch("/library/people/{id}", testPerson.getId())
                        .flashAttr("person", testPerson))
                .andExpectAll(
                        model().size(1),
                        model().attribute("person", testPerson),
                        model().attributeErrorCount("person", 0),
                        status().is3xxRedirection(),
                        redirectedUrl("/library/people")
                );
        verify(peopleService, times(1)).update(anyInt(), any(Person.class));
    }

    @Test
    void deletePersonTest() throws Exception {
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(delete("/library/people/{id}", anyInt()))
                    .andExpectAll(
                            status().is3xxRedirection(),
                            redirectedUrl("/library/people")
                    );
        }
        verify(peopleService, times(3)).delete(anyInt());
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(peopleService, bookService);
    }
}