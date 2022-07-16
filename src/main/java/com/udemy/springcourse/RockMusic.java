package com.udemy.springcourse;

import org.springframework.stereotype.Component;

import java.util.Random;

public class RockMusic implements Music {
    @Override
    public String getSong() {
        String[] songs = {"Billy Talent - Fallen leaves",
                "Green Day - Holiday",
                "Royal Republic - Tommy Gun"};
        Random random = new Random();
        int randomSong = random.nextInt(3);
        return "playing: " + songs[randomSong];
    }
}
