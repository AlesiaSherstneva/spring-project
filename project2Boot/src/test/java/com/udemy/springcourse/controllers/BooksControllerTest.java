package com.udemy.springcourse.controllers;

import com.udemy.springcourse.pojo.Book;
import com.udemy.springcourse.pojo.Person;
import com.udemy.springcourse.services.BookService;
import com.udemy.springcourse.services.PeopleService;
import org.junit.jupiter.api.*;
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
        testBook.setId(new Random().nextInt(10000));
        testBooks = new ArrayList<>();

        mockMvc = MockMvcBuilders.standaloneSetup(booksController).build();
    }


    @Test
    void showBooksTest() throws Exception {
        // test without sorting and paging
        when(bookService.findAll()).thenReturn(testBooks);
        mockMvc.perform(get("/library/books"))
                .andExpectAll(
                        model().size(1),
                        model().attribute("books", testBooks),
                        status().isOk(),
                        forwardedUrl("books/show")
                );

        //test searching books with wrong parameters
        mockMvc.perform(get("/library/books")
                        .param("page", "1"))
                .andExpectAll(
                        model().size(1),
                        model().attribute("books", testBooks),
                        status().isOk(),
                        forwardedUrl("books/show")
                );
        mockMvc.perform(get("/library/books")
                        .param("books_per_page", "3"))
                .andExpectAll(
                        model().size(1),
                        model().attribute("books", testBooks),
                        status().isOk(),
                        forwardedUrl("books/show")
                );
        verify(bookService, times(3)).findAll();

        // test searching books with sorting only
        when(bookService.findAndSortByYear()).thenReturn(testBooks);
        mockMvc.perform(get("/library/books")
                        .param("sort_by_year", "true"))
                .andExpectAll(
                        model().size(1),
                        model().attribute("books", testBooks),
                        status().isOk(),
                        forwardedUrl("books/show")
                );
        verify(bookService, times(1)).findAndSortByYear();

        // test searching books with paging only
        when(bookService.findAndPage(anyInt(), anyInt())).thenReturn(testBooks);
        mockMvc.perform(get("/library/books")
                        .param("page", "1")
                        .param("books_per_page", "3"))
                .andExpectAll(
                        model().size(1),
                        model().attribute("books", testBooks),
                        status().isOk(),
                        forwardedUrl("books/show")
                );
        verify(bookService, times(1)).findAndPage(anyInt(), anyInt());

        // test searching books with sorting and paging
        when(bookService.findAndPageAndSortByYear(anyInt(), anyInt())).thenReturn(testBooks);
        mockMvc.perform(get("/library/books")
                        .param("page", "1")
                        .param("books_per_page", "3")
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

        //test a book without a reader
        mockMvc.perform(get("/library/books/{id}", anyInt()))
                .andExpectAll(
                        model().size(3),
                        model().attributeExists("person"),
                        model().attributeExists("book"),
                        model().attributeExists("people"),
                        model().attributeDoesNotExist("reader"),
                        status().isOk(),
                        forwardedUrl("books/profile")
                );

        //test a book with a reader
        testBook.setReader(testPerson);
        mockMvc.perform(get("/library/books/{id}", anyInt()))
                .andExpectAll(
                        model().size(3),
                        model().attributeExists("person"),
                        model().attributeExists("book"),
                        model().attributeDoesNotExist("people"),
                        model().attributeExists("reader"),
                        status().isOk(),
                        forwardedUrl("books/profile")
                );

        verify(bookService, times(2)).findOneById(anyInt());
        verify(peopleService, times(1)).findAll();
    }

    @Test
    void addBookTest() throws Exception {
        mockMvc.perform(get("/library/books/new"))
                .andExpectAll(
                        model().size(1),
                        model().attributeExists("book"),
                        status().isOk(),
                        forwardedUrl("books/new")
                );
    }

    @Test
    void createBookTest() throws Exception {
        // test with empty new book
        mockMvc.perform(post("/library/books")
                        .flashAttr("book", testBook))
                .andExpectAll(
                        model().attribute("book", testBook),
                        model().errorCount(3),
                        model().attributeHasFieldErrors("book", "title", "author", "year"),
                        status().isOk(),
                        forwardedUrl("books/new")
                );

        // test a new book with not valid title and author
        testBook.setYear(1991);
        testBook.setTitle("Some title");
        testBook.setAuthor("Some name and surname");
        mockMvc.perform(post("/library/books")
                        .flashAttr("book", testBook))
                .andExpectAll(
                        model().attribute("book", testBook),
                        model().errorCount(2),
                        model().attributeHasFieldErrors("book", "title", "author"),
                        status().isOk(),
                        forwardedUrl("books/new")
                );

        // test a new book with year earlier than 1445
        testBook.setYear(999);
        testBook.setTitle("Название");
        testBook.setAuthor("Фамилия Имя");
        mockMvc.perform(post("/library/books")
                        .flashAttr("book", testBook))
                .andExpectAll(
                        model().attribute("book", testBook),
                        model().errorCount(1),
                        model().attributeHasFieldErrors("book", "year"),
                        status().isOk(),
                        forwardedUrl("books/new")
                );

        // test a new book with year later than current
        testBook.setYear(3000);
        mockMvc.perform(post("/library/books")
                        .flashAttr("book", testBook))
                .andExpectAll(
                        model().attribute("book", testBook),
                        model().errorCount(1),
                        model().attributeHasFieldErrors("book", "year"),
                        status().isOk(),
                        forwardedUrl("books/new")
                );


        // test a valid new book
        testBook.setYear(1881);
        mockMvc.perform(post("/library/books")
                        .flashAttr("book", testBook))
                .andExpectAll(
                        model().size(1),
                        model().attribute("book", testBook),
                        model().attributeHasNoErrors("book"),
                        status().is3xxRedirection(),
                        redirectedUrl("/library/books")
                );
        verify(bookService, times(1)).save(any(Book.class));
    }

    @Test
    void editBookTest() throws Exception {
        when(bookService.findOneById(anyInt())).thenReturn(testBook);
        mockMvc.perform(get("/library/books/{id}/edit", anyInt()))
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
        // test with an empty book
        mockMvc.perform(patch("/library/books/{id}", testBook.getId())
                        .flashAttr("book", testBook))
                .andExpectAll(
                        model().size(1),
                        model().attribute("book", testBook),
                        status().isOk(),
                        forwardedUrl("books/edit")
                );

        // test a book with not valid fields
        testBook.setTitle("Any Title");
        testBook.setAuthor("Any Author");
        testBook.setYear(7777);
        mockMvc.perform(patch("/library/books/{id}", testBook.getId())
                        .flashAttr("book", testBook))
                .andExpectAll(
                        model().size(1),
                        model().attribute("book", testBook),
                        model().attributeErrorCount("book", 3),
                        status().isOk(),
                        forwardedUrl("books/edit")
                );

        //test a book with valid fields
        testBook.setTitle("Название");
        testBook.setAuthor("Фамилия Имя");
        testBook.setYear(1973);
        mockMvc.perform(patch("/library/books/{id}", testBook.getId())
                        .flashAttr("book", testBook))
                .andExpectAll(
                        model().size(1),
                        model().attribute("book", testBook),
                        status().is3xxRedirection(),
                        redirectedUrl("/library/books")
                );
        verify(bookService, times(1)).update(anyInt(), any(Book.class));
    }

    @Test
    void addBookToPersonTest() throws Exception {
        mockMvc.perform(patch("/library/books/{id}/person", testBook.getId())
                        .flashAttr("person", testPerson))
                .andExpectAll(
                        model().size(1),
                        model().attribute("person", testPerson),
                        status().is3xxRedirection(),
                        redirectedUrl("/library/books/" + testBook.getId())
                );
        verify(bookService, times(1)).addBookToPerson(any(Person.class), anyInt());
    }

    @Test
    void freeBookTest() throws Exception {
        mockMvc.perform(patch("/library/books/{id}/free", testBook.getId()))
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/library/books/" + testBook.getId())
                );
        verify(bookService, times(1)).freeBook(testBook.getId());
    }

    @Test
    void searchBookTest() throws Exception {
        // test with empty search string
        mockMvc.perform(get("/library/books/search")
                        .param("startString", ""))
                .andExpectAll(
                        model().size(1),
                        model().attribute("startString", ""),
                        model().attributeDoesNotExist("books"),
                        status().isOk(),
                        forwardedUrl("books/search")
                );

        // test with not empty search string
        when(bookService.searchBooks(anyString())).thenReturn(testBooks);
        mockMvc.perform(get("/library/books/search")
                        .param("startString", "any string"))
                .andExpectAll(
                        model().size(2),
                        model().attribute("startString", "any string"),
                        model().attribute("books", testBooks),
                        status().isOk(),
                        forwardedUrl("books/search")
                );
        verify(bookService, times(1)).searchBooks(anyString());
    }

    @Test
    void deleteBookTest() throws Exception {
        for (int i = 0; i < 9; i++) {
            mockMvc.perform(delete("/library/books/{id}", anyInt()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/library/books"));
        }
        verify(bookService, times(9)).delete(anyInt());
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(peopleService, bookService);
    }
}