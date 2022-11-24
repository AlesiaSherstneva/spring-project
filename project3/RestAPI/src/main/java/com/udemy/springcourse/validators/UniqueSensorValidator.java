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
    private final ModelMapper modelMapper;

    @Autowired
    public UniqueSensorValidator(SensorsService sensorsService, ModelMapper modelMapper) {
        this.sensorsService = sensorsService;
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return SensorDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object object, Errors errors) {
        SensorDTO sensorDTO = (SensorDTO) object;
        Sensor sensor = modelMapper.map(sensorDTO, Sensor.class);
        Sensor sensorInBase = sensorsService.findOneByName(sensor.getName());
        if (sensorInBase != null) {
            errors.rejectValue("name", "", "Такой сенсор уже зарегистрирован!");
        }
    }
}
