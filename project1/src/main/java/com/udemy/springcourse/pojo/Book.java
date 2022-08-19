package com.udemy.springcourse.pojo;

public class Book {
    private int book_id;
    private int person_id;

    private String title;
    private String author;
    private int year;

    public Book() {
    }

    public Book(int book_id, int person_id, String title, String author, int year) {
        this.book_id = book_id;
        this.person_id = person_id;
        this.title = title;
        this.author = author;
        this.year = year;
    }

    public int getBook_id() {
        return book_id;
    }

    public void setBook_id(int book_id) {
        this.book_id = book_id;
    }

    public int getPerson_id() {
        return person_id;
    }

    public void setPerson_id(int person_id) {
        this.person_id = person_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
