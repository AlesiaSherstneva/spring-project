package com.udemy.springcourse.controllers;

import com.udemy.springcourse.config.SpringConfig;
import com.udemy.springcourse.pojo.Book;
import com.udemy.springcourse.pojo.Person;
import com.udemy.springcourse.services.BookService;
import com.udemy.springcourse.services.PeopleService;
import org.junit.jupiter.api.*;
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
@TestMethodOrder(MethodOrderer.Random.class)
class BooksControllerTest {
    private MockMvc mockMvc;

    @Mock
    private BookService bookService;

    @Mock
    private PeopleService peopleService;

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

        mockMvc = MockMvcBuilders.standaloneSetup(booksController).build();
    }

    @Test
    void showBooksTest() throws Exception {
        // test without sorting and paging
        when(bookService.findAll()).thenReturn(testBooks);
        mockMvc.perform(get("/books"))
                .andExpectAll(
                        model().size(1),
                        model().attribute("books", testBooks),
                        status().isOk(),
                        forwardedUrl("books/show")
                );
        verify(bookService, times(1)).findAll();

        // test with paging only
        when(bookService.findAndPage(anyInt(), anyInt())).thenReturn(testBooks);
        mockMvc.perform(get("/books")
                        .param("page", "1")
                        .param("books_per_page", "2"))
                .andExpectAll(
                        model().size(1),
                        model().attribute("books", testBooks),
                        status().isOk(),
                        forwardedUrl("books/show")
                );
        verify(bookService, times(1)).findAndPage(anyInt(), anyInt());

        // test with sorting only
        when(bookService.findAndSortByYear()).thenReturn(testBooks);
        mockMvc.perform(get("/books")
                        .param("sort_by_year", "true"))
                .andExpectAll(
                        model().size(1),
                        model().attribute("books", testBooks),
                        status().isOk(),
                        forwardedUrl("books/show")
                );
        verify(bookService, times(1)).findAndSortByYear();

        // test with sorting and paging
        when(bookService.findAndPageAndSortByYear(anyInt(), anyInt())).thenReturn(testBooks);
        mockMvc.perform(get("/books")
                        .param("page", "1")
                        .param("books_per_page", "2")
                        .param("sort_by_year", "true"))
                .andExpectAll(
                        model().size(1),
                        model().attribute("books", testBooks),
                        status().isOk(),
                        forwardedUrl("books/show")
                );
        verify(bookService, times(1)).findAndPageAndSortByYear(anyInt(), anyInt());
    }

    @Test
    void showBookTest() throws Exception {
        when(bookService.findOneById(anyInt())).thenReturn(testBook);
        when(peopleService.findAll()).thenReturn(testPeople);
        when(peopleService.findOneById(anyInt())).thenReturn(testPerson);

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
        testBook.setReader(testPerson);
        mockMvc.perform(get("/books/{id}", anyInt()))
                .andExpectAll(
                        model().size(3),
                        model().attribute("book", testBook),
                        model().attributeDoesNotExist("people"),
                        model().attribute("reader", testPerson),
                        status().isOk(),
                        forwardedUrl("books/profile")
                );
        verify(bookService, times(2)).findOneById(anyInt());
        verify(peopleService, times(1)).findAll();
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
    void createBookTest() throws Exception {
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
        verify(bookService, times(1)).save(any(Book.class));
    }

    @Test
    void editBookTest() throws Exception {
        when(bookService.findOneById(anyInt())).thenReturn(testBook);
        mockMvc.perform(get("/books/{id}/edit", anyInt()))
                .andExpectAll(
                        model().size(1),
                        model().attribute("book", testBook),
                        status().isOk(),
                        forwardedUrl("books/edit")
                );
        verify(bookService, times(1)).findOneById(anyInt());
    }

    @Test
    void updateBookTest() throws Exception {
        testBook.setId(new Random().nextInt(100));

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
        verify(bookService, times(1)).update(anyInt(), any(Book.class));
    }

    @Test
    void addBookToPersonTest() throws Exception {
        mockMvc.perform(patch("/books/{id}/person", testBook.getId())
                        .flashAttr("person", testPerson))
                .andExpectAll(
                        model().size(1),
                        model().attribute("person", testPerson),
                        status().is3xxRedirection(),
                        redirectedUrl("/books/" + testBook.getId())
                );
        verify(bookService, times(1)).addBookToPerson(any(Person.class), anyInt());
    }

    @Test
    void freeBookTest() throws Exception {
        mockMvc.perform(patch("/books/{id}/free", testBook.getId()))
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/books/" + testBook.getId())
                );
        verify(bookService, times(1)).freeBook(anyInt());
    }


    @Test
    void searchBookTest() throws Exception {
        // test with an empty search string
        mockMvc.perform(get("/books/search")
                        .param("startString", ""))
                .andExpectAll(
                        model().size(1),
                        model().attribute("startString", ""),
                        model().attributeDoesNotExist("books"),
                        status().isOk(),
                        forwardedUrl("books/search")
                );

        // test with not empty search string
        mockMvc.perform(get("/books/search")
                        .param("startString", "some string"))
                .andExpectAll(
                        model().size(2),
                        model().attribute("startString", "some string"),
                        model().attributeExists("books"),
                        status().isOk(),
                        forwardedUrl("books/search")
                );
        verify(bookService, times(1)).searchBooks(anyString());
    }

    @Test
    void deleteBookTest() throws Exception {
        mockMvc.perform(delete("/books/{id}", anyInt()))
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/books")
                );
        verify(bookService, times(1)).delete(anyInt());
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(bookService);
        verifyNoMoreInteractions(peopleService);
    }
}