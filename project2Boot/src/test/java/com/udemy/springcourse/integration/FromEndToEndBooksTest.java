package com.udemy.springcourse.integration;

import com.udemy.springcourse.pojo.Book;
import com.udemy.springcourse.pojo.Person;
import com.udemy.springcourse.services.BookService;
import com.udemy.springcourse.services.PeopleService;
import com.udemy.springcourse.util.H2databaseInitTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.Random.class)
@SuppressWarnings("unchecked")
public class FromEndToEndBooksTest extends H2databaseInitTest {
    private final MockMvc mockMvc;
    private MvcResult mvcResult;
    private ModelAndView modelAndView;

    @Autowired
    private PeopleService peopleService;

    @Autowired
    private BookService bookService;

    @Autowired
    public FromEndToEndBooksTest(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void showBooksWithoutSearchingAndPagingTest() throws Exception {
        mvcResult = mockMvc.perform(get("/library/books"))
                .andExpectAll(
                        model().size(1),
                        model().attributeExists("books"),
                        status().isOk())
                .andReturn();

        modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);
        assertEquals("books/show", modelAndView.getViewName());

        List<Book> receivedBooks = (List<Book>) modelAndView.getModel().get("books");
        assertEquals(2, receivedBooks.size());
    }

    @Test
    public void showBooksWithWrongPagingParametersTest() throws Exception {
        mvcResult = mockMvc.perform(get("/library/books")
                        .param("page", "1"))
                .andExpectAll(
                        model().size(1),
                        model().attributeExists("books"),
                        status().isOk())
                .andReturn();

        modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);
        assertEquals("books/show", modelAndView.getViewName());

        List<Book> booksWithPageParamOnly = (List<Book>) modelAndView.getModel().get("books");
        assertEquals(2, booksWithPageParamOnly.size());

        mvcResult = mockMvc.perform(get("/library/books")
                        .param("books_per_page", "3"))
                .andExpectAll(
                        model().size(1),
                        model().attributeExists("books"),
                        status().isOk())
                .andReturn();

        modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);
        assertEquals("books/show", modelAndView.getViewName());

        List<Book> booksWithBooksPerPageParamOnly = (List<Book>) modelAndView.getModel().get("books");
        assertEquals(2, booksWithBooksPerPageParamOnly.size());

        assertIterableEquals(booksWithPageParamOnly, booksWithBooksPerPageParamOnly);
    }

    @Test
    public void showBooksWithPagingOnlyTest() throws Exception {
        mvcResult = mockMvc.perform(get("/library/books")
                        .param("page", "0")
                        .param("books_per_page", "1"))
                .andExpectAll(
                        model().size(1),
                        model().attributeExists("books"),
                        status().isOk())
                .andReturn();

        modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);
        assertEquals("books/show", modelAndView.getViewName());

        List<Book> oneBookOnPage = (List<Book>) modelAndView.getModel().get("books");
        assertEquals(1, oneBookOnPage.size());
        assertEquals("Первое название", oneBookOnPage.get(0).getTitle());

        mvcResult = mockMvc.perform(get("/library/books")
                        .param("page", "1")
                        .param("books_per_page", "3"))
                .andExpectAll(
                        model().size(1),
                        model().attributeExists("books"),
                        status().isOk())
                .andReturn();

        modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);
        assertEquals("books/show", modelAndView.getViewName());

        List<Book> emptyPage = (List<Book>) modelAndView.getModel().get("books");
        assertTrue(emptyPage.isEmpty());
    }

    @Test
    public void showBooksWithSortingByYearOnlyTest() throws Exception {
        mvcResult = mockMvc.perform(get("/library/books")
                        .param("sort_by_year", "true"))
                .andExpectAll(
                        model().size(1),
                        model().attributeExists("books"),
                        status().isOk())
                .andReturn();

        modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);
        assertEquals("books/show", modelAndView.getViewName());

        List<Book> sortedBooks = (List<Book>) modelAndView.getModel().get("books");
        assertEquals(2, sortedBooks.size());
        assertEquals("Второй Автор", sortedBooks.get(0).getAuthor());
        assertEquals("Первый Автор", sortedBooks.get(1).getAuthor());
    }

    @Test
    public void showBooksWithPagingAndSortingByYearTest() throws Exception {
        mvcResult = mockMvc.perform(get("/library/books")
                        .param("page", "0")
                        .param("books_per_page", "1")
                        .param("sort_by_year", "true"))
                .andExpectAll(
                        model().size(1),
                        model().attributeExists("books"),
                        status().isOk())
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
        List<Book> booksInBase = bookService.findAll();
        assertEquals(2, booksInBase.size());

        Book bookToSave = new Book();
        bookToSave.setTitle("Новое название");
        bookToSave.setAuthor("Новый Автор");
        bookToSave.setYear(1979);

        mockMvc.perform(post("/library/books")
                        .flashAttr("book", bookToSave))
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/library/books"));

        booksInBase = bookService.findAll();
        assertEquals(3, booksInBase.size());
    }

    @Test
    public void updateBookTest() throws Exception {
        Book bookToUpdate = bookService.findOneById(1);
        assertEquals("Первое название", bookToUpdate.getTitle());
        assertEquals("Первый Автор", bookToUpdate.getAuthor());
        assertEquals(2011, bookToUpdate.getYear());

        bookToUpdate.setTitle("Новое название");
        bookToUpdate.setAuthor("Новый Автор");
        bookToUpdate.setYear(bookToUpdate.getYear() + 3);

        mockMvc.perform(patch("/library/books/{id}", bookToUpdate.getId())
                        .flashAttr("book", bookToUpdate))
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/library/books"));

        bookToUpdate = bookService.findOneById(1);
        assertEquals("Новое название", bookToUpdate.getTitle());
        assertEquals("Новый Автор", bookToUpdate.getAuthor());
        assertEquals(2014, bookToUpdate.getYear());
    }

    @Test
    public void addBookToPersonTest() throws Exception {
        Book bookForAdding = bookService.findOneById(1);
        assertNull(bookForAdding.getReader());
        assertNull(bookForAdding.getTakenAt());

        Person personForAdding = peopleService.findOneById(new Random().nextInt(2) + 1);

        mockMvc.perform(patch("/library/books/{id}/person", bookForAdding.getId())
                        .flashAttr("person", personForAdding))
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/library/books/" + bookForAdding.getId()));

        bookForAdding = bookService.findOneById(1);
        assertNotNull(bookForAdding.getReader());
        assertNotNull(bookForAdding.getTakenAt());
        assertFalse(bookForAdding.isExpired());
    }

    @Test
    public void freeBookTest() throws Exception {
        Book bookForFreeing = bookService.findOneById(2);
        assertNotNull(bookForFreeing.getReader());
        assertNotNull(bookForFreeing.getTakenAt());
        assertTrue(bookForFreeing.isExpired());

        mockMvc.perform(patch("/library/books/{id}/free", bookForFreeing.getId()))
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/library/books/" + bookForFreeing.getId()));

        bookForFreeing = bookService.findOneById(2);
        assertNull(bookForFreeing.getReader());
        assertNull(bookForFreeing.getTakenAt());
    }

    @Test
    public void searchBooksExistsTest() throws Exception {
        mvcResult = mockMvc.perform(get("/library/books/search")
                        .param("startString", "втор"))
                .andExpectAll(
                        model().size(2),
                        model().attribute("startString", "втор"),
                        model().attributeExists("books"),
                        status().isOk())
                .andReturn();

        modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);
        assertEquals("books/search", modelAndView.getViewName());

        List<Book> foundBooks = (List<Book>) modelAndView.getModel().get("books");
        assertEquals(1, foundBooks.size());
        assertEquals("Второе название", foundBooks.get(0).getTitle());
    }

    @Test
    public void searchBooksDoesNotExistTest() throws Exception {
        mvcResult = mockMvc.perform(get("/library/books/search")
                        .param("startString", "строка"))
                .andExpectAll(
                        model().size(2),
                        model().attribute("startString", "строка"),
                        model().attributeExists("books"),
                        status().isOk())
                .andReturn();

        modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);
        assertEquals("books/search", modelAndView.getViewName());

        List<Book> foundBooks = (List<Book>) modelAndView.getModel().get("books");
        assertTrue(foundBooks.isEmpty());
    }

    @Test
    public void deleteBookTest() throws Exception {
        List<Book> booksInBase = bookService.findAll();
        assertEquals(2, booksInBase.size());

        Book bookForDeleting = booksInBase.get(1);
        assertEquals("Второе название", bookForDeleting.getTitle());

        mockMvc.perform(delete("/library/books/{id}", bookForDeleting.getId()))
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/library/books"));

        booksInBase = bookService.findAll();
        assertEquals(1, booksInBase.size());
        assertFalse(booksInBase.contains(bookForDeleting));
    }
}