package com.udemy.springcourse.controllers;

import com.udemy.springcourse.pojo.Book;
import com.udemy.springcourse.pojo.Person;
import com.udemy.springcourse.services.BookService;
import com.udemy.springcourse.services.PeopleService;
import com.udemy.springcourse.validators.UniquePersonValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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
    private UniquePersonValidator validator;

    private Person testPerson;
    private List<Person> testPeople;
    private List<Book> testBooks;

    @BeforeEach
    void setUp() {
        testPerson = new Person();
        testPerson.setId(new Random().nextInt());
        testPeople = new ArrayList<>();
        testBooks = new ArrayList<>();

        mockMvc = MockMvcBuilders.standaloneSetup
                (new PeopleController(peopleService, bookService, validator)).build();
    }

    @Test
    void showPeopleTest() throws Exception {
        when(peopleService.findAll()).thenReturn(testPeople);
        mockMvc.perform(get("/library/people"))
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("people"))
                .andExpect(model().attribute("people", testPeople))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("people/show"));
        verify(peopleService, times(1)).findAll();
    }

    @Test
    void showPersonTest() throws Exception {
        when(peopleService.findOneById(anyInt())).thenReturn(testPerson);
        when(bookService.findByReader(any(Person.class))).thenReturn(testBooks);

        //test with empty list of books
        mockMvc.perform(get("/library/people/{id}", anyInt()))
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("person"))
                .andExpect(model().attributeDoesNotExist("books"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("people/profile"));

        //test with not empty list of books
        testBooks.add(new Book());
        mockMvc.perform(get("/library/people/{id}", anyInt()))
                .andExpect(model().size(2))
                .andExpect(model().attributeExists("person"))
                .andExpect(model().attributeExists("books"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("people/profile"));

        verify(peopleService, times(2)).findOneById(anyInt());
        verify(bookService, times(2)).findByReader(any(Person.class));
    }

    @Test
    void addPersonTest() throws Exception {
        mockMvc.perform(get("/library/people/new"))
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("person"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("people/new"));
    }

    @Test
    void createPersonTest() throws Exception {
        // test with not valid new person
        mockMvc.perform(post("/library/people")
                        .flashAttr("person", testPerson))
                .andExpect(model().size(1))
                .andExpect(model().attribute("person", testPerson))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("people/new"));

        // test with valid new person
        testPerson.setName("Фамилия Имя Отчество");
        testPerson.setYear(2000);
        mockMvc.perform(post("/library/people")
                        .flashAttr("person", testPerson))
                .andExpect(model().size(1))
                .andExpect(model().attribute("person", testPerson))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/library/people"));

        verify(peopleService, times(1)).save(any(Person.class));
    }

    @Test
    void editTest() throws Exception {
        when(peopleService.findOneById(anyInt())).thenReturn(testPerson);
        mockMvc.perform(get("/library/people/{id}/edit", anyInt()))
                .andExpect(model().size(1))
                .andExpect(model().attribute("person", testPerson))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("people/edit"));
        verify(peopleService, times(1)).findOneById(anyInt());
    }

    @Test
    void updateTest() throws Exception {
        // test with not valid edit person
        mockMvc.perform(patch("/library/people/{id}", testPerson.getId())
                        .flashAttr("person", testPerson))
                .andExpect(model().size(1))
                .andExpect(model().attribute("person", testPerson))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("people/edit"));

        // test with valid edit person
        testPerson.setName("Фамилия Имя Отчество");
        testPerson.setYear(2000);
        mockMvc.perform(patch("/library/people/{id}", testPerson.getId())
                        .flashAttr("person", testPerson))
                .andExpect(model().size(1))
                .andExpect(model().attribute("person", testPerson))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/library/people"));

        verify(peopleService, times(1)).update(anyInt(), any(Person.class));
    }

    @Test
    void deletePerson() throws Exception {
        for(int i = 0; i < 5; i++) {
            mockMvc.perform(delete("/library/people/{id}", anyInt()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/library/people"));
        }
        verify(peopleService, times(5)).delete(anyInt());
    }
}