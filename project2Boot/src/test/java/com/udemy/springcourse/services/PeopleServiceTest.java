package com.udemy.springcourse.services;

import com.udemy.springcourse.pojo.Person;
import com.udemy.springcourse.repositories.PeopleRepository;
import org.junit.jupiter.api.*;
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
        // given
        when(peopleRepository.findAll()).thenReturn(testPeople);
        // when
        List<Person> receivedPeople = peopleService.findAll();
        // then
        assertIterableEquals(testPeople, receivedPeople);
        verify(peopleRepository).findAll();
    }

    @Test
    void findOneByIdTest() {
        // given
        when(peopleRepository.findById(anyInt())).thenReturn(Optional.of(testPerson));
        // when
        Person receivedPerson = peopleService.findOneById(anyInt());
        // then
        assertNotNull(receivedPerson);
        assertEquals("Test", receivedPerson.getName());
        verify(peopleRepository, times(1)).findById(anyInt());
    }

    @Test
    void findOneByNameTest() {
        // given
        when(peopleRepository.findByName(anyString())).thenReturn(Optional.of(testPerson));
        // when
        Person receivedPerson = peopleService.findOneByName(anyString());
        // then
        assertNotNull(receivedPerson);
        assertEquals("Test", receivedPerson.getName());
        verify(peopleRepository, times(1)).findByName(anyString());
    }

    @Test
    void saveTest() {
        // given, when
        for (int i = 0; i < 5; i++) peopleService.save(new Person());
        // then
        verify(peopleRepository, times(5)).save(any(Person.class));
    }

    @Test
    void updateTest() {
        // given, when
        peopleService.update(anyInt(), testPerson);
        // then
        verify(peopleRepository, times(1)).save(testPerson);
    }

    @Test
    void deleteTest() {
        // given, when
        for (int i = 0; i < 7; i++) peopleService.delete(anyInt());
        // then
        verify(peopleRepository, times(7)).deleteById(anyInt());
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(peopleRepository);
    }
}