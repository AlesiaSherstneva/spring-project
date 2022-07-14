package com.udemy.springcourse;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestSpring {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("applicationContext.xml");

/*        Music music = context.getBean("rockMusic", Music.class);
        MusicPlayer player = new MusicPlayer(music);
        player.playMusic();

        Music music1 = context.getBean("classicalMusic", Music.class);
        player = new MusicPlayer(music1);
        player.playMusic();

        Music music2 = context.getBean("rapMusic", Music.class);
        player = new MusicPlayer(music2);
        player.playMusic();*/

/*        MusicPlayer player = context.getBean("musicPlayer", MusicPlayer.class);
        player.playMusic();*/

        Computer computer = context.getBean("computer", Computer.class);
        System.out.println(computer);

        context.close();
    }
}
