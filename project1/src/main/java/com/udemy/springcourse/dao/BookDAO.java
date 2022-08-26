package com.udemy.springcourse.dao;

import com.udemy.springcourse.pojo.Book;
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
        return jdbcTemplate.query("SELECT * FROM Book ORDER BY title", new BeanPropertyRowMapper<>(Book.class));
    }

    public List<Book> showBooksByPerson(int person_id) {
        return jdbcTemplate.query("SELECT * FROM Book WHERE person_id = ?",
                new BeanPropertyRowMapper<>(Book.class), person_id);
    }

    public Book showBook(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM Book WHERE id = ?",
                    new BeanPropertyRowMapper<>(Book.class), id);
        } catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }

    public void save(Book book) {
        jdbcTemplate.update("INSERT INTO Book(title, author, year) VALUES (?, ?, ?)",
                book.getTitle(), book.getAuthor(), book.getYear());
    }

    public void free(int id) {
        jdbcTemplate.update("UPDATE Book set person_id = NULL where id = ?", id);
    }

    public void update(int id, Book book) {
        jdbcTemplate.update("UPDATE Book SET person_id = ?, title = ?, author = ?, year = ? WHERE id = ?",
                book.getPerson_id(), book.getTitle(), book.getAuthor(), book.getYear(), id);
    }

    public void delete(int id) {
        jdbcTemplate.update("DELETE FROM Book WHERE id = ?", id);
    }
}
