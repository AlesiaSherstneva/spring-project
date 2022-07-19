package com.udemy.springcourse;

import java.util.Random;

public class ClassicalMusic implements Music {
    @Override
    public String getSong() {
        String[] songs = {"Franz Liszt - Hungarian Rhapsody",
                "Wolfgang Amadeus Mozart - Requiem",
                "Ludwig van Beethoven - Moonlight sonata"};
        Random random = new Random();
        int randomSong = random.nextInt(3);
        return songs[randomSong];
    }
}
