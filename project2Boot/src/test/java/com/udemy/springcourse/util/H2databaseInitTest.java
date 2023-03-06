package com.udemy.springcourse.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
public class H2databaseInitTest {
    @Value("${sql.script.create.table.person}")
    protected String createTablePerson;

    @Value("${sql.script.create.table.book}")
    protected String createTableBook;

    @Value("${sql.script.add.people}")
    protected String addPeople;

    @Value("${sql.script.add.books}")
    protected String addBooks;

    @Value("${sql.script.drop.table.person}")
    protected String dropTablePerson;

    @Value("${sql.script.drop.table.book}")
    protected String dropTableBook;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute(createTablePerson);
        jdbcTemplate.execute(createTableBook);
        jdbcTemplate.execute(addPeople);
        jdbcTemplate.execute(addBooks);
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute(dropTableBook);
        jdbcTemplate.execute(dropTablePerson);
    }
}