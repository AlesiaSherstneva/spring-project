package com.udemy.springcourse.controllers;

import com.udemy.springcourse.config.SpringConfig;
import com.udemy.springcourse.dao.BookDAO;
import com.udemy.springcourse.dao.PersonDAO;
import com.udemy.springcourse.pojo.Book;
import com.udemy.springcourse.pojo.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BooksController.class)
@ContextConfiguration(classes = SpringConfig.class)
class BooksControllerTest {
    private MockMvc mockMvc;

    @Mock
    private BookDAO bookDAO;

    @Mock
    private PersonDAO personDAO;

    @InjectMocks
    private BooksController booksController;

    private Book testBook;
    private Person testPerson;
    private List<Book> testBooks;
    private List<Person> testPeople;

    @BeforeEach
    void setUp() {
        testBook = new Book();
        testPerson = new Person();
        testBooks = new ArrayList<>();
        testPeople = new ArrayList<>();

        mockMvc = MockMvcBuilders.standaloneSetup(booksController)
                .build();
    }

    @Test
    void showBooksTest() throws Exception {
        when(bookDAO.showBooks()).thenReturn(testBooks);
        mockMvc.perform(get("/books"))
                .andExpectAll(
                        model().size(1),
                        model().attribute("books", testBooks),
                        status().isOk(),
                        forwardedUrl("books/show")
                );
        verify(bookDAO, times(1)).showBooks();
    }

    @Test
    void showBookTest() throws Exception {
        when(bookDAO.showBook(anyInt())).thenReturn(testBook);
        when(personDAO.showPeople()).thenReturn(testPeople);
        when(personDAO.showPerson(anyInt())).thenReturn(testPerson);

        // test a book without a reader
        mockMvc.perform(get("/books/{id}", anyInt()))
                .andExpectAll(
                        model().size(3),
                        model().attribute("book", testBook),
                        model().attribute("people", testPeople),
                        model().attributeDoesNotExist("reader"),
                        status().isOk(),
                        forwardedUrl("books/profile")
                );

        // test a book with a reader
        testBook.setPerson_id(new Random().nextInt());
        mockMvc.perform(get("/books/{id}", anyInt()))
                .andExpectAll(
                        model().size(3),
                        model().attribute("book", testBook),
                        model().attributeDoesNotExist("people"),
                        model().attribute("reader", testPerson),
                        status().isOk(),
                        forwardedUrl("books/profile")
                );
    }

    @Test
    void addBookTest() throws Exception {
        mockMvc.perform(get("/books/new"))
                .andExpectAll(
                        model().size(1),
                        model().attributeExists("book"),
                        status().isOk(),
                        forwardedUrl("books/new")
                );
    }

    @Test
    void createBook() throws Exception {
        // test with empty book
        mockMvc.perform(post("/books")
                        .flashAttr("book", testBook))
                .andExpectAll(
                        model().size(1),
                        model().attribute("book", testBook),
                        model().attributeErrorCount("book", 3),
                        status().isOk(),
                        forwardedUrl("books/new")
                );

        // test a book with not valid title and author
        testBook.setTitle("Any Title");
        testBook.setAuthor("Any Author");
        testBook.setYear(2000);
        mockMvc.perform(post("/books")
                        .flashAttr("book", testBook))
                .andExpectAll(
                        model().size(1),
                        model().attribute("book", testBook),
                        model().attributeErrorCount("book", 2),
                        status().isOk(),
                        forwardedUrl("books/new")
                );

        // test a book with year earlier than 1445
        testBook.setTitle("Название");
        testBook.setAuthor("Фамилия Имя");
        testBook.setYear(945);
        mockMvc.perform(post("/books")
                        .flashAttr("book", testBook))
                .andExpectAll(
                        model().size(1),
                        model().attribute("book", testBook),
                        model().attributeErrorCount("book", 1),
                        status().isOk(),
                        forwardedUrl("books/new")
                );

        // test a book with year later than current
        testBook.setYear(4321);
        mockMvc.perform(post("/books")
                        .flashAttr("book", testBook))
                .andExpectAll(
                        model().size(1),
                        model().attribute("book", testBook),
                        model().attributeErrorCount("book", 1),
                        status().isOk(),
                        forwardedUrl("books/new")
                );

        // test a book with valid fields
        testBook.setYear(2000);
        mockMvc.perform(post("/books")
                        .flashAttr("book", testBook))
                .andExpectAll(
                        model().size(1),
                        model().attribute("book", testBook),
                        model().attributeErrorCount("book", 0),
                        status().is3xxRedirection(),
                        redirectedUrl("/books")
                );
        verify(bookDAO, times(1)).save(any(Book.class));
    }

    @Test
    void editBookTest() throws Exception {
        when(bookDAO.showBook(anyInt())).thenReturn(testBook);
        mockMvc.perform(get("/books/{id}/edit", anyInt()))
                .andExpectAll(
                        model().size(1),
                        model().attribute("book", testBook),
                        status().isOk(),
                        forwardedUrl("books/edit")
                );
        verify(bookDAO, times(1)).showBook(anyInt());
    }

    @Test
    void updateBookTest() throws Exception {
        testBook.setId(new Random().nextInt());

        // test with empty book
        mockMvc.perform(patch("/books/{id}", testBook.getId())
                        .flashAttr("book", testBook))
                .andExpectAll(
                        model().size(1),
                        model().attribute("book", testBook),
                        model().attributeErrorCount("book", 3),
                        status().isOk(),
                        forwardedUrl("books/edit")
                );


        // test a book with not valid fields
        testBook.setTitle("Any Title");
        testBook.setAuthor("Any Author");
        testBook.setYear(2900);
        mockMvc.perform(patch("/books/{id}", testBook.getId())
                        .flashAttr("book", testBook))
                .andExpectAll(
                        model().size(1),
                        model().attribute("book", testBook),
                        model().attributeErrorCount("book", 3),
                        status().isOk(),
                        forwardedUrl("books/edit")
                );

        // test a book with valid fields
        testBook.setTitle("Название");
        testBook.setAuthor("Фамилия Имя");
        testBook.setYear(1500);
        mockMvc.perform(patch("/books/{id}", testBook.getId())
                        .flashAttr("book", testBook))
                .andExpectAll(
                        model().size(1),
                        model().attribute("book", testBook),
                        model().attributeErrorCount("book", 0),
                        status().is3xxRedirection(),
                        redirectedUrl("/books")
                );
        verify(bookDAO, times(1)).update(anyInt(), any(Book.class));
    }

    @Test
    void addBookToPersonTest() throws Exception {
        when(bookDAO.showBook(anyInt())).thenReturn(testBook);
        mockMvc.perform(patch("/books/{id}/person", testBook.getId())
                        .flashAttr("person", testPerson))
                .andExpectAll(
                        model().size(1),
                        model().attribute("person", testPerson),
                        status().is3xxRedirection(),
                        redirectedUrl("/books/" + testBook.getId())
                );
        verify(bookDAO, times(1)).showBook(anyInt());
        verify(bookDAO, times(1)).update(anyInt(), any(Book.class));
    }

    @Test
    void freeBookTest() throws Exception {
        mockMvc.perform(patch("/books/{id}/free", testBook.getId()))
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/books/" + testBook.getId())
                );
        verify(bookDAO, times(1)).free(anyInt());
    }

    @Test
    void deleteBook() throws Exception {
        for (int i = 0; i < 6; i++) {
            mockMvc.perform(delete("/books/{id}", anyInt()))
                    .andExpectAll(
                            status().is3xxRedirection(),
                            redirectedUrl("/books")
                    );
        }
        verify(bookDAO, times(6)).delete(anyInt());
    }
}