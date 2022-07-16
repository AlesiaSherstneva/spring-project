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

        ClassicalMusic classicalMusic1 = context.getBean("classicalMusic", ClassicalMusic.class);
        ClassicalMusic classicalMusic2 = context.getBean("classicalMusic", ClassicalMusic.class);
        System.out.println(classicalMusic1 == classicalMusic2);

        RockMusic rockMusic1 = context.getBean("rockMusic", RockMusic.class);
        RockMusic rockMusic2 = context.getBean("rockMusic", RockMusic.class);
        System.out.println(rockMusic1 == rockMusic2);

        context.close();
    }
}
