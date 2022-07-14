package com.udemy.springcourse;

import org.springframework.stereotype.Component;

@Component
public class ClassicalMusic implements Music {
    @Override
    public String getSong() {
        return "Franz Liszt - Hungarian Rhapsody";
    }
}
