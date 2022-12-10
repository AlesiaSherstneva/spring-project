package com.udemy.springcourse.services;

import com.udemy.springcourse.pojo.Person;
import com.udemy.springcourse.repositories.PeopleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.Random.class)
class PeopleServiceTest {
    @Mock
    PeopleRepository peopleRepository;

    @InjectMocks
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
    void findAll() {
        when(peopleRepository.findAll()).thenReturn(testPeople);
        assertSame(testPeople, peopleService.findAll());
        verify(peopleRepository).findAll();
    }

    @Test
    void findOneById() {
        when(peopleRepository.findById(anyInt())).thenReturn(Optional.of(testPeople.get(0)));
        assertNotNull(peopleService.findOneById(anyInt()));
        assertEquals("Test", peopleService.findOneById(anyInt()).getName());
        verify(peopleRepository, times(2)).findById(anyInt());
    }

    @Test
    void findOneByName() {
        when(peopleRepository.findByName(anyString())).thenReturn(Optional.of(testPeople.get(0)));
        assertNotNull(peopleService.findOneByName("Some string"));
        assertEquals("Test", peopleService.findOneByName("Another string").getName());
        verify(peopleRepository, times(2)).findByName(anyString());
    }

    @Test
    void save() {
        peopleService.save(testPerson);
        verify(peopleRepository, times(1)).save(testPerson);
    }

    @Test
    void update() {
        peopleService.update(anyInt(), testPerson);
        verify(peopleRepository, times(1)).save(testPerson);
    }

    @Test
    void delete() {
        peopleService.delete(anyInt());
        verify(peopleRepository, times(1)).deleteById(anyInt());
    }
}