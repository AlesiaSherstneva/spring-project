package com.udemy.springcourse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class MusicPlayer {
    private final Music music1;
    private final Music music2;

    @Autowired
    public MusicPlayer(@Qualifier("classicalMusic") Music music1,
                       @Qualifier("rockMusic") Music music2) {
        this.music1 = music1;
        this.music2 = music2;
    }

    public void playMusic(Genre genre) {
        if(genre == Genre.CLASSICAL) {
            System.out.println(music1.getSong());
        } else {
            System.out.println(music2.getSong());
        }
    }
}
