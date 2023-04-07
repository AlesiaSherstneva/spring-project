package com.udemy.springcourse.services;

import com.udemy.springcourse.pojos.Sensor;
import com.udemy.springcourse.repositories.SensorsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class SensorsServiceTest {
    @MockBean
    private SensorsRepository sensorsRepository;

    @Autowired
    private SensorsService sensorsService;

    private Sensor testSensor;

    @BeforeEach
    void setUp() {
        testSensor = new Sensor();
    }

    @Test
    void saveTest() {
        // given, when
        for (int i = 0; i < 7; i++) sensorsService.save(new Sensor());
        // then
        verify(sensorsRepository, times(7)).save(any(Sensor.class));
    }

    @Test
    void findOneByNameIfPresentTest() {
        findByName();
    }

    @Test
    void findOneByNameOrElseThrowExceptionIfPresentTest() {
        findByName();
    }

    private void findByName() {
        // given
        when(sensorsRepository.findByName(anyString())).thenReturn(Optional.of(testSensor));
        // when
        Optional<Sensor> receivedSensor = sensorsService.findOneByName(anyString());
        // then
        assertTrue(receivedSensor.isPresent());
        receivedSensor.ifPresent(sensor -> assertSame(testSensor, sensor));
    }

    @Test
    void findOneByNameIfNotPresentTest() {
        // given
        when(sensorsRepository.findByName(anyString())).thenReturn(Optional.empty());
        // when
        Optional<Sensor> receivedSensor = sensorsService.findOneByName(anyString());
        // then
        assertFalse(receivedSensor.isPresent());
    }

    @Test
    void findOneByNameOrElseThrowExceptionIfNotPresentTest() {
        // given
        when(sensorsRepository.findByName(anyString())).thenReturn(Optional.empty());
        // when, then
        try {
            sensorsService.findOneByNameOrElseThrowException(anyString());
            fail("There should be SensorNotFoundException");
        } catch (Exception exception) {
            assertEquals("SensorNotFoundException", exception.getClass().getSimpleName());
            assertEquals(RuntimeException.class, exception.getClass().getSuperclass());
        }
    }
}