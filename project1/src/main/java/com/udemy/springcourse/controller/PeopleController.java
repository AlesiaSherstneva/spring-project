package com.udemy.springcourse.controller;

import com.udemy.springcourse.dao.PersonDAO;
import com.udemy.springcourse.pojo.Person;
import com.udemy.springcourse.validators.UniquePersonValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @GetMapping("/new")
    public String addPerson(@ModelAttribute("person") Person person) {
        return "people/new";
    }

    @PostMapping()
    public String create(@ModelAttribute("person") @Valid Person person,
                         BindingResult bindingResult) {
        validator.validate(person, bindingResult);
        if (bindingResult.hasErrors()) return "people/new";
        personDAO.save(person);
        return "redirect:people";
    }
}
