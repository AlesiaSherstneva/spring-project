package com.udemy.springcourse.services;

import com.udemy.springcourse.pojo.Book;
import com.udemy.springcourse.pojo.Person;
import com.udemy.springcourse.repositories.BooksRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.Random.class)
class BookServiceTest {
    @Mock
    BooksRepository booksRepository;

    @InjectMocks
    BookService bookService;

    Book testBook;
    List<Book> testBooks;
    Random random;

    @BeforeEach
    void setUp() {
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
        assertSame(testBooks, receivedBooks);
        verify(booksRepository, times(1)).findAll();
    }

    @Test
    void findAndSortByYearTest() {
        // given
        for (int i = 0; i < 3; i++) testBooks.add(new Book());
        when(booksRepository.findAll(eq(Sort.by("year")))).thenReturn(testBooks);
        // when
        List<Book> receivedBooks = bookService.findAndSortByYear();
        // then
        assertEquals(4, receivedBooks.size());
        verify(booksRepository, times(1)).findAll(Sort.by("year"));
    }

    @Test
    void findAndPageTest() {
        // given
        int page = random.nextInt(100), itemsOnPage = random.nextInt(100);
        when(booksRepository.findAll(PageRequest.of(page, itemsOnPage))).thenReturn(new PageImpl<>(testBooks));
        // when
        List<Book> receivedBooks = bookService.findAndPage(page, itemsOnPage);
        // then
        assertIterableEquals(testBooks, receivedBooks);
        verify(booksRepository, times(1)).findAll(PageRequest.of(page, itemsOnPage));
    }

    @Test
    void findAndPageAndSortByYearTest() {
        // given
        int page = random.nextInt(100), itemsOnPage = random.nextInt(100);
        when(booksRepository.findAll(PageRequest.of(page, itemsOnPage, Sort.by("year"))))
                .thenReturn(new PageImpl<>(testBooks));
        // when
        List<Book> receivedBooks = bookService.findAndPageAndSortByYear(page, itemsOnPage);
        // then
        assertIterableEquals(testBooks, receivedBooks);
        verify(booksRepository, times(1))
                .findAll(PageRequest.of(page, itemsOnPage, Sort.by("year")));
    }

    @Test
    void findOneByIdTest() {
        // test with existing book
        // given
        int testId = random.nextInt(100);
        testBook.setId(testId);
        when(booksRepository.findById(anyInt())).thenReturn(Optional.of(testBook));
        // when
        Book receivedBook = bookService.findOneById(anyInt());
        // then
        assertEquals(testId, receivedBook.getId());

        // test with null book
        // given
        when(booksRepository.findById(anyInt())).thenReturn(Optional.empty());
        // when
        receivedBook = bookService.findOneById(anyInt());
        // then
        assertNull(receivedBook);

        verify(booksRepository, times(2)).findById(anyInt());
    }

    @Test
    void findByReaderTest() {
        // given
        when(booksRepository.findByReader(any(Person.class))).thenReturn(testBooks);
        // when
        List<Book> receivedBooks = bookService.findByReader(new Person());
        // then
        assertEquals(1, receivedBooks.size());
        assertSame(testBook, receivedBooks.get(0));
        verify(booksRepository, times(1)).findByReader(any(Person.class));
    }

    @Test
    void saveTest() {
        // given, when
        for(int i = 0; i < 5; i++) bookService.save(new Book());
        // then
        verify(booksRepository, times(5)).save(any(Book.class));
    }

    @Test
    void updateTest() {
        // given
        when(booksRepository.findById(anyInt())).thenReturn(Optional.of(testBook));
        // when
        for (int i = 0; i < 7; i++) bookService.update(random.nextInt(1000), testBook);
        // then
        verify(booksRepository, times(7)).findById(anyInt());
        verify(booksRepository, times(7)).save(any(Book.class));
    }

    @Test
    void addBookToPersonTest() {
        // given
        assertNull(testBook.getReader());
        assertNull(testBook.getTakenAt());
        when(booksRepository.findById(anyInt())).thenReturn(Optional.of(testBook));
        // when
        bookService.addBookToPerson(new Person(), random.nextInt(500));
        // then
        assertNotNull(testBook.getReader());
        assertNotNull(testBook.getTakenAt());
        verify(booksRepository, times(1)).save(any(Book.class));
    }

    @Test
    void freeBookTest() {
        // given
        testBook.setReader(new Person());
        testBook.setTakenAt(new Date());
        when(booksRepository.findById(anyInt())).thenReturn(Optional.of(testBook));
        // when
        bookService.freeBook(random.nextInt(200));
        // then
        assertNull(testBook.getReader());
        assertNull(testBook.getTakenAt());
        verify(booksRepository, times(1)).save(any(Book.class));
    }

    @Test
    void searchBooksTest() {
        // given
        when(booksRepository.findByTitleStartingWithIgnoreCase(anyString())).thenReturn(testBooks);
        // when
        List<Book> receivedBooks = bookService.searchBooks("search string");
        // then
        assertIterableEquals(testBooks, receivedBooks);
        verify(booksRepository, times(1)).findByTitleStartingWithIgnoreCase(anyString());
    }

    @Test
    void deleteTest() {
        // given, when
        for (int i = 0; i < 10; i++) bookService.delete(random.nextInt(700));
        // then
        verify(booksRepository, times(10)).deleteById(anyInt());
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(booksRepository);
    }
}