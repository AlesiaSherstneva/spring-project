package com.udemy.springcourse;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static com.udemy.springcourse.Genre.*;

public class TestSpring {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(SpringConfig.class);

        MusicPlayer player = context.getBean("musicPlayer", MusicPlayer.class);

        player.playMusic(CLASSICAL);
        player.playMusic(ROCK);
        System.out.println("Player: " + player.getName());
        System.out.println("Volume: " + player.getVolume());

        ClassicalMusic classicalMusic1 = context.getBean("classicalMusic", ClassicalMusic.class);

        context.close();
    }
}
