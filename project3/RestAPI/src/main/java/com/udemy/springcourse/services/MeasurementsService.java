package com.udemy.springcourse.services;

import com.udemy.springcourse.pojos.Measurement;
import com.udemy.springcourse.repositories.MeasurementsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class MeasurementsService {
    private final MeasurementsRepository measurementsRepository;

    @Autowired
    public MeasurementsService(MeasurementsRepository measurementsRepository) {
        this.measurementsRepository = measurementsRepository;
    }

    public List<Measurement> findAll() {
        return measurementsRepository.findAll();
    }

    public int countRainyDays() {
        List<Measurement> measurements = measurementsRepository.findAll();
        int counter = 0;
        for(Measurement measurement: measurements) {
            if (measurement.isRaining()) {
                counter++;
            }
        }
        return counter;
    }

    @Transactional
    public void save(Measurement measurement) {
        measurement.setCreatedAt(LocalDateTime.now());
        measurementsRepository.save(measurement);
    }
}
