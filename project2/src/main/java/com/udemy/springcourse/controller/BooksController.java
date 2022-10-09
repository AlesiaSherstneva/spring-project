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

@Controller
@RequestMapping("/books")
public class BooksController {
    private final PeopleService peopleService;
    private final BookService bookService;

    public BooksController(PeopleService peopleService, BookService bookService) {
        this.peopleService = peopleService;
        this.bookService = bookService;
    }


    @GetMapping
    public String showBooks(Model model) {
        model.addAttribute("books", bookService.findAll());
        return "books/show";
    }

    @GetMapping("/{id}")
    public String showBook(@PathVariable("id") int id, Model model,
                           @ModelAttribute("person") Person person) {
        Book book = bookService.findOne(id);
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
        return "redirect:/books";
    }

/*    @GetMapping("/{id}/edit")
    public String editBook(@PathVariable("id") int id, Model model) {
        model.addAttribute("book", bookDAO.showBook(id));
        return "books/edit";
    }*/

/*    @PatchMapping("/{id}")
    public String updateBook(@ModelAttribute("book") @Valid Book book,
                             BindingResult bindingResult,
                             @PathVariable("id") int id) {
        if (bindingResult.hasErrors()) return "books/edit";
        bookDAO.update(id, book);
        return "redirect:/books";
    }*/

/*    @PatchMapping("/{id}/person")
    public String addBookToPerson(@ModelAttribute("person") Person person,
                                  @PathVariable("id") int id) {
        Book book = bookDAO.showBook(id);
        book.setPerson_id(person.getId());
        bookDAO.update(id, book);
        return "redirect:/books/{id}";
    }*/

/*    @PatchMapping("/{id}/free")
    public String freeBook(@PathVariable("id") int id) {
        bookDAO.free(id);
        return "redirect:/books/{id}";
    }*/

/*    @DeleteMapping("/{id}")
    public String deleteBook(@PathVariable("id") int id) {
        bookDAO.delete(id);
        return "redirect:/books";
    }*/
}
