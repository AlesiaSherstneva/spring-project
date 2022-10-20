package com.udemy.springcourse.project2Boot.services;

import com.udemy.springcourse.project2Boot.pojo.Book;
import com.udemy.springcourse.project2Boot.pojo.Person;
import com.udemy.springcourse.project2Boot.repositories.BooksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BookService {
    private final BooksRepository booksRepository;

    @Autowired
    public BookService(BooksRepository booksRepository) {
        this.booksRepository = booksRepository;
    }

    public List<Book> findAll() {
        return booksRepository.findAll();
    }

    public List<Book> findAndSortByYear() {
        return booksRepository.findAll(Sort.by("year"));
    }

    public List<Book> findAndPage(int page, int itemsOnPage) {
        return booksRepository.findAll(PageRequest.of(page, itemsOnPage)).getContent();
    }

    public List<Book> findAndPageAndSortByYear(int page, int itemsOnPage) {
        return booksRepository.findAll(PageRequest.of(page, itemsOnPage, Sort.by("year"))).getContent();
    }

    public Book findOneById(int id) {
        Optional<Book> book = booksRepository.findById(id);
        return book.orElse(null);
    }

    public List<Book> findByReader(Person reader) {
        return booksRepository.findByReader(reader);
    }

    @Transactional
    public void save(Book book) {
        booksRepository.save(book);
    }

    @Transactional
    public void update(int id, Book book) {
        Book bookInBase = booksRepository.findById(id).orElse(null);
        if (bookInBase != null) {
            book.setReader(bookInBase.getReader());
            book.setTakenAt(bookInBase.getTakenAt());
        }
        book.setId(id);
        booksRepository.save(book);
    }

    @Transactional
    public void addBookToPerson(Person person, int id) {
        Book book = booksRepository.findById(id).orElse(null);
        if (book != null) {
            book.setReader(person);
            book.setTakenAt(new Date());
            booksRepository.save(book);
        }
    }

    @Transactional
    public void freeBook(int id) {
        Book book = booksRepository.findById(id).orElse(null);
        if (book != null) {
            book.setReader(null);
            book.setTakenAt(null);
            booksRepository.save(book);
        }
    }

    public List<Book> searchBooks(String startString) {
        return booksRepository.findByTitleStartingWithIgnoreCase(startString);
    }

    @Transactional
    public void delete(int id) {
        booksRepository.deleteById(id);
    }
}
