package com.udemy.springcourse.services;

import org.springframework.stereotype.Service;

@Service
public class AdminService {
    public void doAdminStuff() {
        System.out.println("Only admin here");
    }
}
