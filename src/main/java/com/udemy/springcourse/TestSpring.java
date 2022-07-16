package com.udemy.springcourse;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import static com.udemy.springcourse.Genre.*;

public class TestSpring {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("applicationContext.xml");

        MusicPlayer player = context.getBean("musicPlayer", MusicPlayer.class);

        player.playMusic(CLASSICAL);
        player.playMusic(ROCK);
        System.out.println("Player: " + player.getName());
        System.out.println("Volume: " + player.getVolume());

        context.close();
    }
}
