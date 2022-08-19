package com.udemy.springcourse.pojo;

public class Person {
    private int user_id;

    private String full_name;

    private int year_of_birth;

    public Person() {
    }

    public Person(int user_id, String full_name, int year_of_birth) {
        this.user_id = user_id;
        this.full_name = full_name;
        this.year_of_birth = year_of_birth;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public int getYear_of_birth() {
        return year_of_birth;
    }

    public void setYear_of_birth(int year_of_birth) {
        this.year_of_birth = year_of_birth;
    }
}
