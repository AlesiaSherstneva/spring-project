package com.udemy.springcourse.controller;

import com.udemy.springcourse.pojo.Book;
import com.udemy.springcourse.pojo.Person;
import com.udemy.springcourse.services.BookService;
import com.udemy.springcourse.services.PeopleService;
import com.udemy.springcourse.validators.UniquePersonValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/library/people")
public class PeopleController {
    private final PeopleService peopleService;
    private final BookService bookService;
    private final UniquePersonValidator validator;

    @Autowired
    public PeopleController(PeopleService peopleService, BookService bookService, UniquePersonValidator validator) {
        this.peopleService = peopleService;
        this.bookService = bookService;
        this.validator = validator;
    }

    @GetMapping
    public String showPeople(Model model) {
        model.addAttribute("people", peopleService.findAll());
        return "people/show";
    }

    @GetMapping("/{id}")
    public String showPerson(@PathVariable("id") int id, Model model) {
        Person person = peopleService.findOneById(id);
        List<Book> books = bookService.findByReader(person);
        model.addAttribute("person", person);
        if (!books.isEmpty()) {
            model.addAttribute("books", books);
        }
        return "people/profile";
    }

    @GetMapping("/new")
    public String addPerson(@ModelAttribute("person") Person person) {
        return "people/new";
    }

    @PostMapping()
    public String createPerson(@ModelAttribute("person") @Valid Person person,
                               BindingResult bindingResult) {
        validator.validate(person, bindingResult);
        if (bindingResult.hasErrors()) return "people/new";
        peopleService.save(person);
        return "redirect:/library/people";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") int id, Model model) {
        model.addAttribute("person", peopleService.findOneById(id));
        return "people/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("person") @Valid Person person,
                         BindingResult bindingResult,
                         @PathVariable("id") int id) {
        validator.validate(person, bindingResult);
        if (bindingResult.hasErrors()) return "people/edit";
        peopleService.update(id, person);
        return "redirect:/library/people";
    }

    @DeleteMapping("/{id}")
    public String deletePerson(@PathVariable("id") int id) {
        peopleService.delete(id);
        return "redirect:/library/people";
    }
}