package com.udemy.springcourse.services;

import com.udemy.springcourse.pojos.Measurement;
import com.udemy.springcourse.repositories.MeasurementsRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.Random.class)
class MeasurementsServiceTest {
    @MockBean
    private MeasurementsRepository measurementsRepository;

    @Autowired
    private MeasurementsService measurementsService;

    private List<Measurement> testMeasurements;

    @BeforeEach
    public void setUp() {
        testMeasurements = new ArrayList<>();

        Measurement measurement1 = new Measurement();
        measurement1.setRaining(true);
        testMeasurements.add(measurement1);

        Measurement measurement2 = new Measurement();
        measurement2.setRaining(false);
        testMeasurements.add(measurement2);
    }

    @Test
    public void findAllMeasurementsTest() {
        // given
        when(measurementsRepository.findAll()).thenReturn(testMeasurements);
        // when
        List<Measurement> receivedMeasurements = measurementsService.findAll();
        // then
        assertEquals(2, receivedMeasurements.size());
        assertIterableEquals(testMeasurements, receivedMeasurements);

        verify(measurementsRepository, times(1)).findAll();
    }

    @Test
    public void findAllMeasurementsIfEmptyTest() {
        // given
        when(measurementsRepository.findAll()).thenReturn(Collections.emptyList());
        // when
        List<Measurement> receivedMeasurements = measurementsService.findAll();
        // then
        assertTrue(receivedMeasurements.isEmpty());

        verify(measurementsRepository, times(1)).findAll();
    }

    @Test
    public void countOneRainyDayTest() {
        // given
        when(measurementsRepository.findAll()).thenReturn(testMeasurements);
        // when
        long rainyDays = measurementsService.countRainyDays();
        // then
        assertEquals(1, rainyDays);

        verify(measurementsRepository, times(1)).findAll();
    }

    @Test
    public void countOneThousandRainyDays() {
        // given
        for (int i = 0; i < 999; i++) {
            Measurement measurement = new Measurement();
            measurement.setRaining(true);
            testMeasurements.add(measurement);
        }
        when(measurementsRepository.findAll()).thenReturn(testMeasurements);
        // when
        long rainyDays = measurementsService.countRainyDays();
        // then
        assertEquals(1000, rainyDays);

        verify(measurementsRepository, times(1)).findAll();
    }

    @Test
    public void countZeroRainyDays() {
        // given
        when(measurementsRepository.findAll()).thenReturn(Collections.emptyList());
        // when
        long rainyDays = measurementsService.countRainyDays();
        // then
        assertEquals(0, rainyDays);

        verify(measurementsRepository, times(1)).findAll();
    }

    @Test
    public void saveNewMeasurementTest() {
        // given, when
        Measurement testMeasurement = new Measurement();
        assertNull(testMeasurement.getCreatedAt());
        // when
        measurementsService.save(testMeasurement);
        // then
        assertNotNull(testMeasurement.getCreatedAt());
        verify(measurementsRepository, times(1)).save(any(Measurement.class));
    }

    @AfterEach
    public void tearDown() {
        verifyNoMoreInteractions(measurementsRepository);
    }
}