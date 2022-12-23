package com.udemy.springcourse.services;

import com.udemy.springcourse.pojo.Book;
import com.udemy.springcourse.pojo.Person;
import com.udemy.springcourse.repositories.BooksRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.Random.class)
class BookServiceWithMockRepositoryTest {
    @Mock
    BooksRepository booksRepository;

    @InjectMocks
    BookService bookService;

    Book testBook;
    Person testPerson;
    List<Book> testBooks;

    @BeforeEach
    void setUp() {
        testBook = new Book();
        testPerson = new Person();
        testBooks = new ArrayList<>();
        testBooks.add(testBook);
    }

    @Test
    void findAll() {
        when(booksRepository.findAll()).thenReturn(testBooks);
        assertSame(testBooks, bookService.findAll());
        verify(booksRepository, times(1)).findAll();
    }

    @Test
    void findAndSortByYear() {
        when(booksRepository.findAll(Sort.by("year"))).thenReturn(testBooks);
        assertEquals(testBook, bookService.findAndSortByYear().get(0));
        verify(booksRepository, times(1)).findAll(Sort.by("year"));
    }

    @Test
    void findAndPage() {
        PageImpl<Book> pagedBooks = new PageImpl<>(testBooks);
        when(booksRepository.findAll(isA(Pageable.class))).thenReturn(pagedBooks);
        assertEquals(testBooks, bookService.findAndPage(1, 1));
        verify(booksRepository, times(1)).findAll(isA(Pageable.class));
    }

    @Test
    void findAndPageAndSortByYear() {
        PageImpl<Book> pagedBooks = new PageImpl<>(testBooks);
        when(booksRepository.findAll(isA(Pageable.class))).thenReturn(pagedBooks);
        for (int i = 1; i <= 10; i++) {
            assertEquals(testBooks, bookService.findAndPage(i, i));
        }
        verify(booksRepository, times(10)).findAll(isA(Pageable.class));
    }

    @Test
    void findOneById() {
        when(booksRepository.findById(anyInt())).thenReturn(Optional.of(testBook));
        assertEquals(testBook, bookService.findOneById(anyInt()));
        verify(booksRepository, times(1)).findById(anyInt());
    }

    @Test
    void findByReader() {
        when(booksRepository.findByReader(testPerson)).thenReturn(testBooks);
        assertEquals(testBook, bookService.findByReader(testPerson).get(0));
        verify(booksRepository, times(1)).findByReader(testPerson);
    }

    @Test
    void save() {
        bookService.save(testBook);
        bookService.save(testBook);
        bookService.save(testBook);
        verify(booksRepository, times(3)).save(testBook);
    }

    @Test
    void update() {
        testBook.setReader(testPerson);
        testBook.setTakenAt(new Date());
        when(booksRepository.findById(anyInt())).thenReturn(Optional.of(testBook));
        bookService.update(1, testBook);
        assertEquals(1, testBook.getId());
        assertNotNull(testBook.getTakenAt());
        verify(booksRepository, times(1)).save(testBook);
    }

    @Test
    void addBookToPerson() {
        when(booksRepository.findById(anyInt())).thenReturn(Optional.of(testBook));
        bookService.addBookToPerson(testPerson, anyInt());
        assertEquals(testPerson, testBook.getReader());
        assertNotNull(testBook.getTakenAt());
        verify(booksRepository, times(1)).save(testBook);
    }

    @Test
    void freeBook() {
        testBook.setReader(testPerson);
        testBook.setTakenAt(new Date());
        when(booksRepository.findById(anyInt())).thenReturn(Optional.of(testBook));
        bookService.freeBook(anyInt());
        assertNull(testBook.getReader());
        assertNull(testBook.getTakenAt());
        verify(booksRepository, times(1)).save(testBook);
    }

    @Test
    void searchBooks() {
        when(booksRepository.findByTitleStartingWithIgnoreCase(anyString())).thenReturn(testBooks);
        assertEquals(testBooks, bookService.searchBooks(anyString()));
        verify(booksRepository, times(1))
                .findByTitleStartingWithIgnoreCase(anyString());
    }

    @Test
    void delete() {
        bookService.delete(anyInt());
        verify(booksRepository, times(1)).deleteById(anyInt());
    }
}