package com.udemy.springcourse;

import java.util.List;
import java.util.Random;

public class MusicPlayer {
    private Genres genres;

    public MusicPlayer(Genres genres) {
        this.genres = genres;
    }

    public Genres getGenres() {
        return genres;
    }

    public void setGenres(Genres genres) {
        this.genres = genres;
    }

    public void playMusic() {
        Random random = new Random();
        System.out.println("Playing: "
                + genres.getMusicList().get(random.nextInt(3)).getSong());
    }
}
