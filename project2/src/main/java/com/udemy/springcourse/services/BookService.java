package com.udemy.springcourse.services;

import com.udemy.springcourse.pojo.Book;
import com.udemy.springcourse.pojo.Person;
import com.udemy.springcourse.repositories.BooksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<Book> searchBooks(String startString) {
        return booksRepository.findByTitleStartingWith(startString);
    }

    @Transactional
    public void save(Book book) {
        booksRepository.save(book);
    }

    @Transactional
    public void update(int id, Book book) {
        book.setId(id);
        booksRepository.save(book);
    }

    @Transactional
    public void delete(int id) {
        booksRepository.deleteById(id);
    }
}
