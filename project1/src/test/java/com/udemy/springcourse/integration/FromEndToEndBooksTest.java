package com.udemy.springcourse.integration;

import com.udemy.springcourse.config.TestConfig;
import com.udemy.springcourse.controllers.BooksController;
import com.udemy.springcourse.dao.BookDAO;
import com.udemy.springcourse.dao.PersonDAO;
import com.udemy.springcourse.pojo.Book;
import com.udemy.springcourse.pojo.Person;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Random;

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
    private final PersonDAO personDAO;
    private final BookDAO bookDAO;

    private final MockMvc mockMvc;

    @Autowired
    public FromEndToEndBooksTest(JdbcTemplate jdbcTemplate) {
        personDAO = new PersonDAO(jdbcTemplate);
        bookDAO = new BookDAO(jdbcTemplate);

        mockMvc = MockMvcBuilders.standaloneSetup(new BooksController(personDAO, bookDAO))
                .build();
    }

    @Test
    public void showBooksTest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/books"))
                .andExpectAll(
                        model().size(1),
                        model().attributeExists("books"),
                        status().isOk(),
                        forwardedUrl("books/show"))
                .andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);

        List<Book> receivedBooks = (List<Book>) modelAndView.getModel().get("books");
        assertEquals(2, receivedBooks.size());
    }

    @Test
    public void createNewBookTest() throws Exception {
        List<Book> booksInBase = bookDAO.showBooks();
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

        booksInBase = bookDAO.showBooks();
        assertEquals(3, booksInBase.size());
        assertTrue(booksInBase.contains(bookToSave));
    }

    @Test
    public void updateBookTest() throws Exception {
        Book bookToUpdate = bookDAO.showBook(1);
        assertEquals("Первое название", bookToUpdate.getTitle());
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

        bookToUpdate = bookDAO.showBook(1);
        assertEquals("Новое название", bookToUpdate.getTitle());
        assertEquals("Новый Автор", bookToUpdate.getAuthor());
        assertEquals(2014, bookToUpdate.getYear());
    }

    @Test
    public void addBookToPersonTest() throws Exception {
        Book bookForAdding = bookDAO.showBook(1);
        assertNull(bookForAdding.getPerson_id());

        Person personForAdding = personDAO.showPerson(new Random().nextInt(2) + 1);

        mockMvc.perform(patch("/books/{id}/person", bookForAdding.getId())
                        .flashAttr("person", personForAdding))
                .andExpectAll(
                        model().size(1),
                        model().attributeExists("person"),
                        status().is3xxRedirection(),
                        redirectedUrl("/books/" + bookForAdding.getId())
                );

        bookForAdding = bookDAO.showBook(1);
        assertNotNull(bookForAdding.getPerson_id());
    }

    @Test
    public void freeBookTest() throws Exception {
        Book bookForFreeing = bookDAO.showBook(2);
        assertNotNull(bookForFreeing.getPerson_id());

        mockMvc.perform(patch("/books/{id}/free", bookForFreeing.getId()))
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/books/" + bookForFreeing.getId())
                );

        bookForFreeing = bookDAO.showBook(2);
        assertNull(bookForFreeing.getPerson_id());
    }

    @Test
    public void deleteBookTest() throws Exception {
        List<Book> booksInBase = bookDAO.showBooks();
        assertEquals(2, booksInBase.size());
        Book bookForDeleting = booksInBase.get(1);

        mockMvc.perform(delete("/books/{id}", bookForDeleting.getId()))
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/books")
                );

        booksInBase = bookDAO.showBooks();
        assertEquals(1, booksInBase.size());
        assertFalse(booksInBase.contains(bookForDeleting));
    }
}