package com.udemy.springcourse.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/first")
public class FirstController {

    @GetMapping("/hello")
    public String helloPage(@RequestParam(value = "name", required = false) String name,
                            @RequestParam(value = "surname", required = false) String surname,
                            Model model) {
    /*    System.out.println("Hello, " + name + " " + surname);*/
        model.addAttribute("message", "Hello, " + name + " " + surname);
        return "first/hello";
    }

    @GetMapping("/goodbye")
    public String goodByePage() {
        return "first/goodbye";
    }

    @GetMapping("/calculator")
    public String calculator(@RequestParam("a") int a,
                             @RequestParam("b") int b,
                             @RequestParam("action") String action,
                             Model model) {
        double result = 0;
        switch (action) {
            case "multiplication" : result = a + b;
            break;
            case "addition" : result = a * b;
            break;
            case "subtraction" : result = a - b;
            break;
            case "division" : result = (double) a / b;
        }
        model.addAttribute("result", "The result of the operation is " + result);
        return "first/calculator";
    }
}
