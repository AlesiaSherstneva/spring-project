package com.udemy.springcourse;

import org.springframework.stereotype.Component;

@Component
public class ClassicalMusic implements Music {
    private ClassicalMusic() {
    }

    public static ClassicalMusic getClassicalMusic(){
        return new ClassicalMusic();
    }

    public void doInit() {
        System.out.println("Doing initialization");
    }

    public void doDestroy() {
        System.out.println("Doing destruction");
    }

    @Override
    public String getSong() {
        return "Franz Liszt - Hungarian Rhapsody";
    }
}
