package com.udemy.springcourse.services;

import com.udemy.springcourse.pojo.Book;
import com.udemy.springcourse.pojo.Person;
import com.udemy.springcourse.repositories.BooksRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.Random.class)
class BookServiceTest {
    @MockBean
    BooksRepository booksRepository;

    @Autowired
    BookService bookService;

    Book testBook;
    Person testPerson;
    List<Book> testBooks;

    Random random;

    @BeforeEach
    void setUp() {
        testPerson = new Person();

        testBook = new Book();
        testBooks = new ArrayList<>();
        testBooks.add(testBook);

        random = new Random();
    }

    @Test
    void findAllTest() {
        // given
        when(booksRepository.findAll()).thenReturn(testBooks);
        // when
        List<Book> receivedBooks = bookService.findAll();
        // then
        assertIterableEquals(testBooks, receivedBooks);
        verify(booksRepository, times(1)).findAll();
    }

    @Test
    void findAndSortByYearTest() {
        // given
        when(booksRepository.findAll(Sort.by("year"))).thenReturn(testBooks);
        // when
        List<Book> receivedBooks = bookService.findAndSortByYear();
        // then
        assertIterableEquals(testBooks, receivedBooks);
        assertEquals(testBook, receivedBooks.get(0));
        verify(booksRepository, times(1)).findAll(Sort.by("year"));
    }

    @Test
    void findAndPageTest() {
        // given
        PageImpl<Book> pagedBooks = new PageImpl<>(testBooks);
        when(booksRepository.findAll(isA(Pageable.class))).thenReturn(pagedBooks);
        // when
        List<Book> receivedBooks = bookService.findAndPage(random.nextInt(100), random.nextInt(100));
        // then
        assertIterableEquals(testBooks, receivedBooks);
        verify(booksRepository, times(1)).findAll(isA(Pageable.class));
    }

    @Test
    void findAndPageAndSortByYearTest() {
        // given
        PageImpl<Book> pagedBooks = new PageImpl<>(testBooks);
        when(booksRepository.findAll(isA(Pageable.class))).thenReturn(pagedBooks);
        // when, then
        for (int i = 1; i <= 10; i++) {
            assertEquals(testBooks, bookService.findAndPageAndSortByYear(i, i));
        }
        verify(booksRepository, times(10)).findAll(isA(Pageable.class));
    }

    @Test
    void findOneByIdTest() {
        // given
        when(booksRepository.findById(anyInt())).thenReturn(Optional.of(testBook));
        // when
        Book receivedBook = bookService.findOneById(anyInt());
        // then
        assertEquals(testBook, receivedBook);
        verify(booksRepository, times(1)).findById(anyInt());
    }

    @Test
    void findByReaderTest() {
        // given
        when(booksRepository.findByReader(any(Person.class))).thenReturn(testBooks);
        // when
        List<Book> receivedBooks = bookService.findByReader(testPerson);
        // then
        assertEquals(1, receivedBooks.size());
        assertEquals(testBook, receivedBooks.get(0));
        verify(booksRepository, times(1)).findByReader(any(Person.class));
    }

    @Test
    void saveTest() {
        // given, when
        for (int i = 0; i < 3; i++) bookService.save(testBook);
        // then
        verify(booksRepository, times(3)).save(any(Book.class));
    }

    @Test
    void updateTest() {
        // given
        testBook.setReader(testPerson);
        testBook.setTakenAt(new Date());
        when(booksRepository.findById(anyInt())).thenReturn(Optional.of(testBook));
        // when
        int testPersonId = random.nextInt(200);
        bookService.update(testPersonId, testBook);
        // then
        assertEquals(testPersonId, testBook.getId());
        assertNotNull(testBook.getTakenAt());
        verify(booksRepository, times(1)).findById(testPersonId);
        verify(booksRepository, times(1)).save(any(Book.class));
    }

    @Test
    void addBookToPersonTest() {
        // given
        when(booksRepository.findById(anyInt())).thenReturn(Optional.of(testBook));
        // when
        int testBookId = random.nextInt(400);
        bookService.addBookToPerson(testPerson, testBookId);
        // then
        assertEquals(testPerson, testBook.getReader());
        assertNotNull(testBook.getTakenAt());
        verify(booksRepository, times(1)).findById(testBookId);
        verify(booksRepository, times(1)).save(any(Book.class));
    }

    @Test
    void freeBookTest() {
        // given
        testBook.setReader(testPerson);
        testBook.setTakenAt(new Date());
        when(booksRepository.findById(anyInt())).thenReturn(Optional.of(testBook));
        // when
        int testBookId = random.nextInt(140);
        bookService.freeBook(testBookId);
        // then
        assertNull(testBook.getReader());
        assertNull(testBook.getTakenAt());
        verify(booksRepository, times(1)).findById(testBookId);
        verify(booksRepository, times(1)).save(testBook);
    }

    @Test
    void searchBooksTest() {
        // given
        when(booksRepository.findByTitleStartingWithIgnoreCase(anyString())).thenReturn(testBooks);
        // when
        List<Book> receivedBooks = bookService.searchBooks(anyString());
        // then
        assertIterableEquals(testBooks, receivedBooks);
        verify(booksRepository, times(1)).findByTitleStartingWithIgnoreCase(anyString());
    }

    @Test
    void deleteTest() {
        // given, when
        bookService.delete(anyInt());
        // then
        verify(booksRepository, times(1)).deleteById(anyInt());
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(booksRepository);
    }
}