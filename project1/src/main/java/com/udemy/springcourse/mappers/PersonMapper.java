package com.udemy.springcourse.mappers;

import com.udemy.springcourse.pojo.Person;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonMapper implements RowMapper<Person> {

    @Override
    public Person mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Person person = new Person();
        person.setId(resultSet.getInt("person_id"));
        person.setName(resultSet.getString("full_name"));
        person.setYear(resultSet.getInt("year_of_birth"));
        return person;
    }
}
