package com.udemy.springcourse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class MusicPlayer {
    private Music music1;
    private Music music2;
    private Music music3;

    @Autowired
    public MusicPlayer(@Qualifier("rockMusic") Music music1,
                       @Qualifier("rapMusic") Music music2,
                       @Qualifier("classicalMusic") Music music3) {
        this.music1 = music1;
        this.music2 = music2;
        this.music3 = music3;
    }

    public String playMusic() {
        return "Playing: " + music1.getSong() + ", " + music2.getSong() + ", " + music3.getSong();
    }
}
