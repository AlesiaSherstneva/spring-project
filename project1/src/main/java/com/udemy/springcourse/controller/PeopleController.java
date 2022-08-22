package com.udemy.springcourse.controller;

import com.udemy.springcourse.dao.PersonDAO;
import com.udemy.springcourse.pojo.Person;
import com.udemy.springcourse.validators.UniquePersonValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/people")
public class PeopleController {
    private final PersonDAO personDAO;
    private final UniquePersonValidator validator;

    @Autowired
    public PeopleController(PersonDAO personDAO, UniquePersonValidator validator) {
        this.personDAO = personDAO;
        this.validator = validator;
    }

    @GetMapping
    public String showPeople(Model model) {
        model.addAttribute("people", personDAO.showPeople());
        return "people/show";
    }

    @GetMapping("{id}")
    public String showPerson(@PathVariable("id") int id, Model model) {
        model.addAttribute("person", personDAO.showPerson(id));
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
        personDAO.save(person);
        return "redirect:/people";
    }

    @PostMapping("{id}")
    public String deletePerson(@PathVariable("id") int id) {
        personDAO.delete(id);
        return "redirect:/people";
    }
}
