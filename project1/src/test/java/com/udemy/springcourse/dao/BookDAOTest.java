package com.udemy.springcourse.dao;

import com.udemy.springcourse.config.TestConfig;
import com.udemy.springcourse.pojo.Book;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@Sql({"/jdbc/drop-book.sql", "/jdbc/drop-person.sql", "/jdbc/create-person.sql",
        "/jdbc/create-book.sql", "/jdbc/insert-people.sql", "/jdbc/insert-books.sql"})
@ContextConfiguration(classes = TestConfig.class)
@TestMethodOrder(MethodOrderer.Random.class)
class BookDAOTest {
    private final BookDAO bookDAO;

    @Autowired
    public BookDAOTest(JdbcTemplate jdbcTemplate) {
        bookDAO = new BookDAO(jdbcTemplate);
    }

    @Test
    void showBooksTest() {
        // given, when
        List<Book> booksInBase = bookDAO.showBooks();
        // then
        assertEquals(2, booksInBase.size());
    }

    @Test
    void showBooksByPersonTest() {
        // given, when
        List<Book> firstPersonBooks = bookDAO.showBooksByPerson(1);
        List<Book> secondPersonBooks = bookDAO.showBooksByPerson(2);
        // then
        assertEquals(1, firstPersonBooks.size());
        assertEquals(0, secondPersonBooks.size());
    }

    @Test
    void showBookTest() {
        // given, when
        Book firstBook = bookDAO.showBook(1);
        Book secondBook = bookDAO.showBook(2);
        Book thirdBook = bookDAO.showBook(new Random().nextInt(1000) + 3);
        // then
        assertEquals("Первое название", firstBook.getTitle());
        assertEquals("Второй Автор", secondBook.getAuthor());
        assertNull(thirdBook);
    }

    @Test
    void saveBookTest() {
        // given
        List<Book> booksInBase = bookDAO.showBooks();
        assertEquals(2, booksInBase.size());

        Book testBook = new Book();
        testBook.setTitle("Новое название");
        testBook.setAuthor("Новый Автор");
        testBook.setYear(1900);
        // when
        bookDAO.save(testBook);
        // then
        booksInBase = bookDAO.showBooks();
        assertEquals(3, booksInBase.size());

        Book receivedBook = bookDAO.showBook(3);
        assertEquals(testBook, receivedBook);
    }

    @Test
    void freeBookTest() {
        // given
        Book bookWithReader = bookDAO.showBook(2);
        assertNotNull(bookWithReader.getPerson_id());
        //when
        bookDAO.free(2);
        // then
        bookWithReader = bookDAO.showBook(2);
        assertNull(bookWithReader.getPerson_id());
    }

    @Test
    void updateBookTest() {
        // given
        Book updatingBook = bookDAO.showBook(1);
        assertEquals("Первое название", updatingBook.getTitle());
        assertEquals("Первый Автор", updatingBook.getAuthor());
        assertEquals(2011, updatingBook.getYear());
        assertNull(updatingBook.getPerson_id());

        updatingBook.setTitle("Новое название");
        updatingBook.setAuthor("Новый Автор");
        updatingBook.setPerson_id(3);
        // when
        bookDAO.update(updatingBook.getId(), updatingBook);
        // then
        updatingBook = bookDAO.showBook(1);
        assertEquals("Новое название", updatingBook.getTitle());
        assertEquals("Новый Автор", updatingBook.getAuthor());
        assertEquals(2011, updatingBook.getYear());
        assertNotNull(updatingBook.getPerson_id());
    }

    @Test
    void deleteBookTest() {
        // given
        List<Book> booksInBase = bookDAO.showBooks();
        assertEquals(2, booksInBase.size());

        Book deletingBook = bookDAO.showBook(1);
        assertNotNull(deletingBook);
        // when
        bookDAO.delete(1);
        // then
        booksInBase = bookDAO.showBooks();
        assertEquals(1, booksInBase.size());

        deletingBook = bookDAO.showBook(1);
        assertNull(deletingBook);
    }
}