package com.udemy.springcourse.services;

import com.udemy.springcourse.pojo.Person;
import com.udemy.springcourse.repositories.PeopleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.Random.class)
class PeopleServiceTest {
    @MockBean
    PeopleRepository peopleRepository;

    @Autowired
    PeopleService peopleService;

    Person testPerson;
    List<Person> testPeople;

    @BeforeEach
    void setUp() {
        testPerson = new Person();
        testPerson.setName("Test");

        testPeople = new ArrayList<>();
        testPeople.add(testPerson);
    }

    @Test
    void findAllTest() {
        when(peopleRepository.findAll()).thenReturn(testPeople);
        assertSame(testPeople, peopleService.findAll());
        verify(peopleRepository).findAll();
    }

    @Test
    void findOneByIdTest() {
        when(peopleRepository.findById(anyInt())).thenReturn(Optional.of(testPerson));
        assertNotNull(peopleService.findOneById(anyInt()));
        assertEquals("Test", peopleService.findOneById(anyInt()).getName());
        verify(peopleRepository, times(2)).findById(anyInt());
    }

    @Test
    void findOneByNameTest() {
        when(peopleRepository.findByName(anyString())).thenReturn(Optional.of(testPerson));
        assertNotNull(peopleService.findOneByName(anyString()));
        assertEquals("Test", peopleService.findOneByName(anyString()).getName());
        verify(peopleRepository, times(2)).findByName(anyString());
    }

    @Test
    void saveTest() {
        peopleService.save(testPerson);
        verify(peopleRepository, times(1)).save(testPerson);
    }

    @Test
    void updateTest() {
        peopleService.update(anyInt(), testPerson);
        verify(peopleRepository, times(1)).save(testPerson);
    }

    @Test
    void deleteTest() {
        peopleService.delete(anyInt());
        verify(peopleRepository, times(1)).deleteById(anyInt());
    }
}