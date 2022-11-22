package com.udemy.springcourse.services;

import com.udemy.springcourse.pojos.Measurement;
import com.udemy.springcourse.repositories.MeasurementsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class MeasurementsService {
    private final MeasurementsRepository measurementsRepository;

    @Autowired
    public MeasurementsService(MeasurementsRepository measurementsRepository) {
        this.measurementsRepository = measurementsRepository;
    }

    @Transactional
    public void save(Measurement measurement) {
        measurement.setCreatedAt(LocalDateTime.now());
        measurementsRepository.save(measurement);
    }
}
