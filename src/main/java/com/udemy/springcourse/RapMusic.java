package com.udemy.springcourse;

import java.util.Random;

public class RapMusic implements Music{
    @Override
    public String getSong() {
        String[] songs = {"Morgenstern - Noviy merin",
                "Instasamka - Juicy",
                "Ganvest - Goba"};
        Random random = new Random();
        int randomSong = random.nextInt(3);
        return songs[randomSong];
    }
}
