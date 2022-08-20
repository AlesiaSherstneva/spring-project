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

    public int getId() {
        return user_id;
    }

    public void setId(int user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return full_name;
    }

    public void setName(String full_name) {
        this.full_name = full_name;
    }

    public int getYear() {
        return year_of_birth;
    }

    public void setYear(int year_of_birth) {
        this.year_of_birth = year_of_birth;
    }
}
