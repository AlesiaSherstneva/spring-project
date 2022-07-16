package com.udemy.springcourse;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Random;

@Component
public class ClassicalMusic implements Music {

    @PostConstruct
    public void doInit() {
        System.out.println("Doing initialisation");
    }

    @PreDestroy
    public void doDestroy() {
        System.out.println("Doing destruction");
    }

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
