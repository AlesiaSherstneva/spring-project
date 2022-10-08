package com.udemy.springcourse.dao;

import com.udemy.springcourse.pojo.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PersonDAO {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PersonDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Person> showPeople() {
        return jdbcTemplate.query("SELECT * FROM Person ORDER BY name", new BeanPropertyRowMapper<>(Person.class));
    }

    public Person showPerson(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM Person WHERE id = ?",
                    new BeanPropertyRowMapper<>(Person.class), id);
        } catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }

    public Person showPerson(String name) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM Person WHERE name = ?",
                    new BeanPropertyRowMapper<>(Person.class), name);
        } catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }

    public void save(Person person) {
        jdbcTemplate.update("INSERT INTO Person(name, year) VALUES (?, ?)",
                person.getName(), person.getYear());
    }

    public void update(int id, Person person) {
        jdbcTemplate.update("UPDATE Person SET name = ?, year = ? WHERE id = ?",
                person.getName(), person.getYear(), id);
    }

    public void delete(int id) {
        jdbcTemplate.update("DELETE FROM Person WHERE id = ?", id);
    }
}