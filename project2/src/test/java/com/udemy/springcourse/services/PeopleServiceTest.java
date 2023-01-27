package com.udemy.springcourse.services;

import com.udemy.springcourse.pojo.Person;
import com.udemy.springcourse.repositories.PeopleRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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
        assertSame(testPeople, receivedPeople);
        verify(peopleRepository).findAll();
    }

    @Test
    void findOneByIdTest() {
        // given
        testPerson.setName("Test");
        testPerson.setYear(2005);
        when(peopleRepository.findById(anyInt())).thenReturn(Optional.of(testPerson));
        // when
        Person receivedPerson = peopleService.findOneById(anyInt());
        // then
        assertEquals("Test", receivedPerson.getName());
        assertEquals(2005, receivedPerson.getYear());
        verify(peopleRepository, times(1)).findById(anyInt());
    }

    @Test
    void findOneByNameTest() {
        // given
        testPerson.setName("Test");
        when(peopleRepository.findByName(anyString())).thenReturn(Optional.of(testPerson));
        // when
        Person receivedPerson = peopleService.findOneByName(anyString());
        // then
        assertEquals("Test", receivedPerson.getName());
        assertEquals(0, receivedPerson.getYear());
        verify(peopleRepository, times(1)).findByName(anyString());
    }

    @Test
    void saveTest() {
        // given, when
        for (int i = 0; i < 4; i++) peopleService.save(new Person());
        // then
        verify(peopleRepository, times(4)).save(any(Person.class));
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
        peopleService.delete(anyInt());
        // then
        verify(peopleRepository, times(1)).deleteById(anyInt());
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(peopleRepository);
    }
}