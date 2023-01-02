package com.udemy.springcourse.controllers;

import com.udemy.springcourse.pojo.Book;
import com.udemy.springcourse.pojo.Person;
import com.udemy.springcourse.services.BookService;
import com.udemy.springcourse.services.PeopleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.Random.class)
class BooksControllerTest {
    private MockMvc mockMvc;

    @MockBean
    private PeopleService peopleService;

    @MockBean
    private BookService bookService;

    @Autowired
    private BooksController booksController;

    private Person testPerson;
    private List<Person> testPeople;
    private Book testBook;
    private List<Book> testBooks;

    @BeforeEach
    void setUp() {
        testPerson = new Person();
        testPeople = new ArrayList<>();
        testBook = new Book();
        testBook.setId(new Random().nextInt());
        testBooks = new ArrayList<>();

        mockMvc = MockMvcBuilders.standaloneSetup(booksController).build();
    }


    @Test
    void showBooksTest() throws Exception {
        // test searching books with sorting only
        when(bookService.findAndSortByYear()).thenReturn(testBooks);
        mockMvc.perform(get("/library/books")
                        .param("sort_by_year", "true"))
                .andExpect(model().size(1))
                .andExpect(model().attribute("books", testBooks))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("books/show"));
        verify(bookService, times(1)).findAndSortByYear();

        // test searching books with paging only
        when(bookService.findAndPage(anyInt(), anyInt())).thenReturn(testBooks);
        mockMvc.perform(get("/library/books")
                        .param("page", "1")
                        .param("books_per_page", "3"))
                .andExpect(model().size(1))
                .andExpect(model().attribute("books", testBooks))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("books/show"));
        verify(bookService, times(1)).findAndPage(anyInt(), anyInt());

        // test searching books with sorting and paging
        when(bookService.findAndPageAndSortByYear(anyInt(), anyInt())).thenReturn(testBooks);
        mockMvc.perform(get("/library/books")
                        .param("page", "1")
                        .param("books_per_page", "3")
                        .param("sort_by_year", "true"))
                .andExpect(model().size(1))
                .andExpect(model().attribute("books", testBooks))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("books/show"));
        verify(bookService, times(1)).findAndPageAndSortByYear(anyInt(), anyInt());

        // test searching books without sorting and paging
        when(bookService.findAll()).thenReturn(testBooks);
        mockMvc.perform(get("/library/books"))
                .andExpect(model().size(1))
                .andExpect(model().attribute("books", testBooks))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("books/show"));

        //test searching books with wrong parameters
        mockMvc.perform(get("/library/books")
                        .param("page", "1"))
                .andExpect(model().size(1))
                .andExpect(model().attribute("books", testBooks))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("books/show"));
        mockMvc.perform(get("/library/books")
                        .param("books_per_page", "3"))
                .andExpect(model().size(1))
                .andExpect(model().attribute("books", testBooks))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("books/show"));

        verify(bookService, times(3)).findAll();
    }

    @Test
    void showBookTest() throws Exception {
        when(bookService.findOneById(anyInt())).thenReturn(testBook);
        when(peopleService.findAll()).thenReturn(testPeople);

        //test a book without a reader
        mockMvc.perform(get("/library/books/{id}", anyInt()))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("books/profile"))
                .andExpect(model().size(3))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attributeExists("people"))
                .andExpect(model().attributeDoesNotExist("reader"));

        //test a book with a reader
        testBook.setReader(testPerson);
        mockMvc.perform(get("/library/books/{id}", anyInt()))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("books/profile"))
                .andExpect(model().size(3))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attributeDoesNotExist("people"))
                .andExpect(model().attributeExists("reader"));

        verify(bookService, times(2)).findOneById(anyInt());
        verify(peopleService, times(1)).findAll();
    }

    @Test
    void addBookTest() throws Exception {
        mockMvc.perform(get("/library/books/new"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("books/new"))
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("book"));
    }

    @Test
    void createBookTest() throws Exception {
        // test with empty new book
        mockMvc.perform(post("/library/books")
                        .flashAttr("book", testBook))
                .andExpect(model().attribute("book", testBook))
                .andExpect(model().errorCount(3))
                .andExpect(model().attributeHasFieldErrors("book", "title", "author", "year"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("books/new"));

        // test a new book with year earlier than 1445
        testBook.setYear(1000);
        mockMvc.perform(post("/library/books")
                        .flashAttr("book", testBook))
                .andExpect(model().attribute("book", testBook))
                .andExpect(model().errorCount(3))
                .andExpect(model().attributeHasFieldErrors("book", "title", "author", "year"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("books/new"));

        // test a new book with year later than current
        testBook.setYear(3000);
        mockMvc.perform(post("/library/books")
                        .flashAttr("book", testBook))
                .andExpect(model().attribute("book", testBook))
                .andExpect(model().errorCount(3))
                .andExpect(model().attributeHasFieldErrors("book", "title", "author", "year"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("books/new"));

        // test a new book with not valid names
        testBook.setYear(1991);
        testBook.setTitle("Some title");
        testBook.setAuthor("Some name and surname");
        mockMvc.perform(post("/library/books")
                        .flashAttr("book", testBook))
                .andExpect(model().attribute("book", testBook))
                .andExpect(model().errorCount(2))
                .andExpect(model().attributeHasFieldErrors("book", "title", "author"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("books/new"));

        // test a valid new book
        testBook.setTitle("Название");
        testBook.setAuthor("Фамилия Имя");
        mockMvc.perform(post("/library/books")
                        .flashAttr("book", testBook))
                .andExpect(model().size(1))
                .andExpect(model().attribute("book", testBook))
                .andExpect(model().attributeHasNoErrors("book"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/library/books"));

        verify(bookService, times(1)).save(any(Book.class));
    }

    @Test
    void editBookTest() throws Exception {
        when(bookService.findOneById(anyInt())).thenReturn(testBook);
        mockMvc.perform(get("/library/books/{id}/edit", anyInt()))
                .andExpect(model().size(1))
                .andExpect(model().attribute("book", testBook))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("books/edit"));
        verify(bookService, times(1)).findOneById(anyInt());
    }

    @Test
    void updateBookTest() throws Exception {
        // test with not valid edit book
        mockMvc.perform(patch("/library/books/{id}", testBook.getId())
                        .flashAttr("book", testBook))
                .andExpect(model().size(1))
                .andExpect(model().attribute("book", testBook))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("books/edit"));

        //test with valid edit book
        testBook.setTitle("Название");
        testBook.setAuthor("Фамилия Имя");
        testBook.setYear(1991);
        mockMvc.perform(patch("/library/books/{id}", testBook.getId())
                        .flashAttr("book", testBook))
                .andExpect(model().size(1))
                .andExpect(model().attribute("book", testBook))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/library/books"));

        verify(bookService, times(1)).update(anyInt(), any(Book.class));
    }

    @Test
    void addBookToPersonTest() throws Exception {
        mockMvc.perform(patch("/library/books/{id}/person", testBook.getId())
                        .flashAttr("person", testPerson))
                .andExpect(model().size(1))
                .andExpect(model().attribute("person", testPerson))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/library/books/" + testBook.getId()));
        verify(bookService, times(1)).addBookToPerson(any(Person.class), anyInt());
    }

    @Test
    void freeBookTest() throws Exception {
        mockMvc.perform(patch("/library/books/{id}/free", testBook.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/library/books/" + testBook.getId()));
        verify(bookService, times(1)).freeBook(anyInt());
    }

    @Test
    void searchBookTest() throws Exception {
        // test with empty search string
        mockMvc.perform(get("/library/books/search")
                        .param("startString", ""))
                .andExpect(model().size(1))
                .andExpect(model().attribute("startString", ""))
                .andExpect(model().attributeDoesNotExist("books"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("books/search"));

        // test with not empty search string
        when(bookService.searchBooks(anyString())).thenReturn(testBooks);
        mockMvc.perform(get("/library/books/search")
                        .param("startString", "any string"))
                .andExpect(model().size(2))
                .andExpect(model().attribute("startString", "any string"))
                .andExpect(model().attribute("books", testBooks))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("books/search"));

        verify(bookService, times(1)).searchBooks(anyString());
    }

    @Test
    void deleteBookTest() throws Exception {
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(delete("/library/books/{id}", anyInt()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/library/books"));
        }
        verify(bookService, times(3)).delete(anyInt());
    }
}