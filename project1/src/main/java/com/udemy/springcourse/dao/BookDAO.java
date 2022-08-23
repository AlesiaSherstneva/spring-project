package com.udemy.springcourse.dao;

import com.udemy.springcourse.pojo.Book;
import com.udemy.springcourse.pojo.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookDAO {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BookDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Book> showBooks() {
        return jdbcTemplate.query("SELECT * FROM Book", new BeanPropertyRowMapper<>(Book.class));
    }

    public Book showBook(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM Book WHERE id = ?",
                    new BeanPropertyRowMapper<>(Book.class), id);
        } catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }
}
