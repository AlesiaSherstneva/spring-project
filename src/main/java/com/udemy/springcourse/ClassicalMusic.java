package com.udemy.springcourse;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@Scope("singleton")
public class ClassicalMusic implements Music {
    @Override
    public String getSong() {
        String[] songs = {"Franz Liszt - Hungarian Rhapsody",
                "Wolfgang Amadeus Mozart - Requiem",
                "Ludwig van Beethoven - Moonlight sonata"};
        Random random = new Random();
        int randomSong = random.nextInt(3);
        return "playing: " + songs[randomSong];
    }
}
