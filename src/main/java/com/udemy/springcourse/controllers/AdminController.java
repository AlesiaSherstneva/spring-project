package com.udemy.springcourse.controllers;

import com.udemy.springcourse.dao.PersonDAO;
import com.udemy.springcourse.pojo.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final PersonDAO personDAO;

    @Autowired
    public AdminController(PersonDAO personDAO) {
        this.personDAO = personDAO;
    }

    @GetMapping
    public String adminPage(Model model, @ModelAttribute("person") Person person) {
        model.addAttribute("people", personDAO.index());
        return "admin/adminPage";
    }

    @PatchMapping("/add")
    public String makeAdmin(@ModelAttribute("person") Person person) {
        System.out.println(person.getId());
        return "redirect:/people";
    }
}
