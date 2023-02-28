package com.udemy.springcourse.validators;

import com.udemy.springcourse.dto.SensorDTO;
import com.udemy.springcourse.pojos.Sensor;
import com.udemy.springcourse.services.SensorsService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@SuppressWarnings("NullableProblems")
@Component
public class UniqueSensorValidator implements Validator {
    private final SensorsService sensorsService;

    @Autowired
    public UniqueSensorValidator(SensorsService sensorsService) {
        this.sensorsService = sensorsService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Sensor.class.equals(clazz);
    }

    @Override
    public void validate(Object object, Errors errors) {
        Sensor sensor = (Sensor) object;
        if (sensorsService.findOneByName(sensor.getName()).isPresent()) {
            errors.rejectValue("name", "", "Такой сенсор уже зарегистрирован!");
        }
    }
}