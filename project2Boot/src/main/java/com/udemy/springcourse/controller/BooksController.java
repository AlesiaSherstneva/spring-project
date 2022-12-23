package com.udemy.springcourse.controller;

import com.udemy.springcourse.pojo.Book;
import com.udemy.springcourse.pojo.Person;
import com.udemy.springcourse.services.BookService;
import com.udemy.springcourse.services.PeopleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@SuppressWarnings("SpringMVCViewInspection")
@Controller
@RequestMapping("/library/books")
public class BooksController {
    private final PeopleService peopleService;
    private final BookService bookService;

    public BooksController(PeopleService peopleService, BookService bookService) {
        this.peopleService = peopleService;
        this.bookService = bookService;
    }

    @GetMapping
    public String showBooks(@RequestParam(value = "page", required = false, defaultValue = "-1") int page,
                            @RequestParam(value = "books_per_page", required = false, defaultValue = "-1") int booksPerPage,
                            @RequestParam(value = "sort_by_year", required = false, defaultValue = "false") boolean sortByYear,
                            Model model) {
        List<Book> books;
        if (page != -1 && booksPerPage != -1) {
            books = sortByYear
                    ? bookService.findAndPageAndSortByYear(page, booksPerPage)
                    : bookService.findAndPage(page, booksPerPage);
        } else if (page == -1 && booksPerPage == -1 && sortByYear) {
            books = bookService.findAndSortByYear();
        } else {
            books = bookService.findAll();
        }
        model.addAttribute("books", books);
        return "books/show";
    }

    @GetMapping("/{id}")
    public String showBook(@PathVariable("id") int id, Model model,
                           @ModelAttribute("person") Person person) {
        Book book = bookService.findOneById(id);
        model.addAttribute("book", book);
        if (book.getReader() != null) {
            model.addAttribute("reader", book.getReader());
        } else {
            model.addAttribute("people", peopleService.findAll());
        }
        return "books/profile";
    }

    @GetMapping("/new")
    public String addBook(@ModelAttribute("book") Book book) {
        return "books/new";
    }

    @PostMapping()
    public String createBook(@ModelAttribute("book") @Valid Book book,
                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "books/new";
        bookService.save(book);
        return "redirect:/library/books";
    }

    @GetMapping("/{id}/edit")
    public String editBook(@PathVariable("id") int id, Model model) {
        model.addAttribute("book", bookService.findOneById(id));
        return "books/edit";
    }

    @PatchMapping("/{id}")
    public String updateBook(@ModelAttribute("book") @Valid Book book,
                             BindingResult bindingResult,
                             @PathVariable("id") int id) {
        if (bindingResult.hasErrors()) return "books/edit";
        bookService.update(id, book);
        return "redirect:/library/books";
    }

    @PatchMapping("/{id}/person")
    public String addBookToPerson(@ModelAttribute("person") Person person,
                                  @PathVariable("id") int id) {
        bookService.addBookToPerson(person, id);
        return "redirect:/library/books/{id}";
    }

    @PatchMapping("/{id}/free")
    public String freeBook(@PathVariable("id") int id) {
        bookService.freeBook(id);
        return "redirect:/library/books/{id}";
    }

    @GetMapping("/search")
    public String searchBook(@RequestParam(required = false, defaultValue = "") String startString,
                             Model model) {
        model.addAttribute("startString", startString);
        if (!startString.isEmpty()) {
            model.addAttribute("books", bookService.searchBooks(startString));
        }
        return "books/search";
    }

    @DeleteMapping("/{id}")
    public String deleteBook(@PathVariable("id") int id) {
        bookService.delete(id);
        return "redirect:/library/books";
    }
}