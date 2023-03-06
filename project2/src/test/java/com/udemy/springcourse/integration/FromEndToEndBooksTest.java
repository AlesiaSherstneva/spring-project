package com.udemy.springcourse.integration;

import com.udemy.springcourse.config.TestConfig;
import com.udemy.springcourse.controllers.BooksController;
import com.udemy.springcourse.pojo.Book;
import com.udemy.springcourse.pojo.Person;
import com.udemy.springcourse.repositories.BooksRepository;
import com.udemy.springcourse.repositories.PeopleRepository;
import com.udemy.springcourse.services.BookService;
import com.udemy.springcourse.services.PeopleService;
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
public class FromEndToEndBooksTest {
    @Autowired
    private PeopleRepository peopleRepository;

    @Autowired
    private BooksRepository booksRepository;

    private MockMvc mockMvc;
    private MvcResult mvcResult;
    private ModelAndView modelAndView;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new BooksController
                (new PeopleService(peopleRepository), new BookService(booksRepository))).build();
    }

    @Test
    public void showBooksWithoutSearchingAndPagingTest() throws Exception {
        mvcResult = mockMvc.perform(get("/books"))
                .andExpectAll(
                        model().size(1),
                        model().attributeExists("books"),
                        status().isOk(),
                        forwardedUrl("books/show"))
                .andReturn();

        modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);

        List<Book> receivedBooks = (List<Book>) modelAndView.getModel().get("books");
        assertEquals(2, receivedBooks.size());
    }

    @Test
    public void showBooksWithWrongPagingParametersTest() throws Exception {
        mvcResult = mockMvc.perform(get("/books")
                        .param("page", "1"))
                .andExpectAll(
                        model().size(1),
                        model().attributeExists("books"),
                        status().isOk(),
                        forwardedUrl("books/show"))
                .andReturn();

        modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);

        List<Book> booksWithPageParamOnly = (List<Book>) modelAndView.getModel().get("books");
        assertEquals(2, booksWithPageParamOnly.size());

        mvcResult = mockMvc.perform(get("/books")
                        .param("books_per_page", "3"))
                .andExpectAll(
                        model().size(1),
                        model().attributeExists("books"),
                        status().isOk(),
                        forwardedUrl("books/show"))
                .andReturn();

        modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);

        List<Book> booksWithBooksPerPageParamOnly = (List<Book>) modelAndView.getModel().get("books");
        assertEquals(2, booksWithBooksPerPageParamOnly.size());

        assertIterableEquals(booksWithPageParamOnly, booksWithBooksPerPageParamOnly);
    }

    @Test
    public void showBooksWithPagingOnlyTest() throws Exception {
        mvcResult = mockMvc.perform(get("/books")
                        .param("page", "0")
                        .param("books_per_page", "1"))
                .andExpectAll(
                        model().size(1),
                        model().attributeExists("books"),
                        status().isOk(),
                        forwardedUrl("books/show"))
                .andReturn();

        modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);

        List<Book> oneBookOnPage = (List<Book>) modelAndView.getModel().get("books");
        assertEquals(1, oneBookOnPage.size());
        assertEquals("Первое название", oneBookOnPage.get(0).getTitle());

        mvcResult = mockMvc.perform(get("/books")
                        .param("page", "1")
                        .param("books_per_page", "3"))
                .andExpectAll(
                        model().size(1),
                        model().attributeExists("books"),
                        status().isOk(),
                        forwardedUrl("books/show"))
                .andReturn();

        modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);

        List<Book> emptyPage = (List<Book>) modelAndView.getModel().get("books");
        assertTrue(emptyPage.isEmpty());
    }

    @Test
    public void showBooksWithSortingByYearOnlyTest() throws Exception {
        mvcResult = mockMvc.perform(get("/books")
                        .param("sort_by_year", "true"))
                .andExpectAll(
                        model().size(1),
                        model().attributeExists("books"),
                        status().isOk(),
                        forwardedUrl("books/show"))
                .andReturn();

        modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);

        List<Book> sortedBooks = (List<Book>) modelAndView.getModel().get("books");
        assertEquals(2, sortedBooks.size());
        assertEquals("Второй Автор", sortedBooks.get(0).getAuthor());
        assertEquals("Первый Автор", sortedBooks.get(1).getAuthor());
    }

    @Test
    public void showBooksWithPagingAndSortingByYearTest() throws Exception {
        mvcResult = mockMvc.perform(get("/books")
                        .param("page", "0")
                        .param("books_per_page", "1")
                        .param("sort_by_year", "true"))
                .andExpectAll(
                        model().size(1),
                        model().attributeExists("books"),
                        status().isOk(),
                        forwardedUrl("books/show"))
                .andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);
        assertEquals("books/show", modelAndView.getViewName());

        List<Book> sortedBooks = (List<Book>) modelAndView.getModel().get("books");
        assertEquals(1, sortedBooks.size());
        assertEquals("Второй Автор", sortedBooks.get(0).getAuthor());
    }

    @Test
    public void createNewBookTest() throws Exception {
        List<Book> booksInBase = booksRepository.findAll();
        assertEquals(2, booksInBase.size());

        Book bookToSave = new Book();
        bookToSave.setTitle("Новое название");
        bookToSave.setAuthor("Новый Автор");
        bookToSave.setYear(1979);

        mockMvc.perform(post("/books")
                        .flashAttr("book", bookToSave))
                .andExpectAll(
                        model().size(1),
                        model().attributeExists("book"),
                        model().attributeHasNoErrors("book"),
                        status().is3xxRedirection(),
                        redirectedUrl("/books")
                );

        booksInBase = booksRepository.findAll();
        assertEquals(3, booksInBase.size());
    }

    @Test
    public void updateBookTest() throws Exception {
        Book bookToUpdate = booksRepository.findByTitle("Первое название");
        assertEquals("Первый Автор", bookToUpdate.getAuthor());
        assertEquals(2011, bookToUpdate.getYear());

        bookToUpdate.setTitle("Новое название");
        bookToUpdate.setAuthor("Новый Автор");
        bookToUpdate.setYear(bookToUpdate.getYear() + 3);

        mockMvc.perform(patch("/books/{id}", bookToUpdate.getId())
                        .flashAttr("book", bookToUpdate))
                .andExpectAll(
                        model().size(1),
                        model().attributeExists("book"),
                        status().is3xxRedirection(),
                        redirectedUrl("/books")
                );

        bookToUpdate = booksRepository.findByTitle("Новое название");
        assertEquals("Новое название", bookToUpdate.getTitle());
        assertEquals("Новый Автор", bookToUpdate.getAuthor());
        assertEquals(2014, bookToUpdate.getYear());
    }

    @Test
    public void addBookToPersonTest() throws Exception {
        Book bookForAdding = booksRepository.findByTitle("Первое название");
        assertNull(bookForAdding.getReader());
        assertNull(bookForAdding.getTakenAt());

        Person personForAdding = peopleRepository.findByName("Третий Тестовый Читатель").orElse(null);

        if (personForAdding != null) {
            int personId = personForAdding.getId();

            mockMvc.perform(patch("/books/{id}/person", bookForAdding.getId())
                            .flashAttr("person", personForAdding))
                    .andExpectAll(
                            model().size(1),
                            model().attributeExists("person"),
                            status().is3xxRedirection(),
                            redirectedUrl("/books/" + bookForAdding.getId())
                    );

            personForAdding.setId(personId);
            peopleRepository.save(personForAdding);

            List<Book> receivedBooks = booksRepository.findAll();
            bookForAdding = receivedBooks.get(0);
            assertNotNull(bookForAdding.getReader());
            assertNotNull(bookForAdding.getTakenAt());
            assertFalse(bookForAdding.isExpired());
        } else {
            fail("Person should be found");
        }
    }

    @Test
    public void freeBookTest() throws Exception {
        Book bookForFreeing = booksRepository.findByTitle("Второе название");
        assertNotNull(bookForFreeing.getReader());
        assertNotNull(bookForFreeing.getTakenAt());
        assertTrue(bookForFreeing.isExpired());

        mockMvc.perform(patch("/books/{id}/free", bookForFreeing.getId()))
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/books/" + bookForFreeing.getId())
                );

        bookForFreeing = booksRepository.findByTitle("Первое название");
        assertNull(bookForFreeing.getReader());
        assertNull(bookForFreeing.getTakenAt());
    }

    @Test
    public void searchBooksExistsTest() throws Exception {
        mvcResult = mockMvc.perform(get("/books/search")
                        .param("startString", "втор"))
                .andExpectAll(
                        model().size(2),
                        model().attribute("startString", "втор"),
                        model().attributeExists("books"),
                        status().isOk(),
                        forwardedUrl("books/search"))
                .andReturn();

        modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);

        List<Book> foundBooks = (List<Book>) modelAndView.getModel().get("books");
        assertEquals(1, foundBooks.size());
        assertEquals("Второе название", foundBooks.get(0).getTitle());
    }

    @Test
    public void searchBooksDoesNotExistTest() throws Exception {
        mvcResult = mockMvc.perform(get("/books/search")
                        .param("startString", "строка"))
                .andExpectAll(
                        model().size(2),
                        model().attribute("startString", "строка"),
                        model().attributeExists("books"),
                        status().isOk(),
                        forwardedUrl("books/search"))
                .andReturn();

        modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);

        List<Book> foundBooks = (List<Book>) modelAndView.getModel().get("books");
        assertTrue(foundBooks.isEmpty());
    }

    @Test
    public void deleteBookTest() throws Exception {
        List<Book> booksInBase = booksRepository.findAll();
        assertEquals(2, booksInBase.size());

        Book bookForDeleting = booksInBase.get(1);
        assertEquals("Второе название", bookForDeleting.getTitle());

        mockMvc.perform(delete("/books/{id}", bookForDeleting.getId()))
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/books")
                );

        booksInBase = booksRepository.findAll();
        assertEquals(1, booksInBase.size());
        assertFalse(booksInBase.contains(bookForDeleting));
    }
}