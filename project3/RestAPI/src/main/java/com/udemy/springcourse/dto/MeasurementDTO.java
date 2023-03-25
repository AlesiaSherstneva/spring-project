package com.udemy.springcourse.dto;

import com.udemy.springcourse.pojos.Sensor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class MeasurementDTO {
    @NotNull(message = "Значение не может быть пустым")
    @Min(value = -100, message = "Значение не может быть меньше, чем -100")
    @Max(value = 100, message = "Значение не может быть больше, чем 100")
    private double value;

    @NotNull(message = "Значение должно быть true или false")
    private Boolean raining;

    @NotNull(message = "Должен быть указан сенсор!")
    private SensorDTO sensor;
}