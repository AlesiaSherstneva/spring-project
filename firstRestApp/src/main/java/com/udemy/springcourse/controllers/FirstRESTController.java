package com.udemy.springcourse.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class FirstRESTController {
    @ResponseBody
    @GetMapping("/sayHello")
    public String sayHello() {
        return "Hello world!";
    }
}
