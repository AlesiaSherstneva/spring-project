package com.udemy.springcourse;

import org.springframework.stereotype.Component;

@Component
public class RockMusic implements Music {
    @Override
    public String getSong() {
        return "Billy Talent - Fallen leaves";
    }
}
