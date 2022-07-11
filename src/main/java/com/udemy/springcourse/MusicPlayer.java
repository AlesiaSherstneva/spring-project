package com.udemy.springcourse;

import java.util.ArrayList;
import java.util.List;

public class MusicPlayer {
/*    private List<Music> musicList = new ArrayList<>();*/
    private String name;
    private int volume;
    private Music music;

    public MusicPlayer() {
    }

    public MusicPlayer(Music music) {
        this.music = music;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

/*    public void setMusicList(List<Music> musicList) {
        this.musicList = musicList;
    }

    public List<Music> getMusicList() {
        return musicList;
    }

    public void playMusic(){
        for(Music music: musicList) {
            System.out.println("Playing: " + music.getSong());
        }
    }*/

    public Music getMusic() {
        return music;
    }

    public void setMusic(Music music) {
        this.music = music;
    }

    public void playMusic() {
        System.out.println("Playing: " + music.getSong());
    }
}
