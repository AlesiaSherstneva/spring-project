package com.udemy.springcourse.pojos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "measurement")
@Getter
@Setter
@NoArgsConstructor
public class Measurement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotNull
    @Min(value = -100, message = "Значение не может быть меньше, чем -100")
    @Max(value = 100, message = "Значение не может быть больше, чем 100")
    @Column(name = "value")
    private Double value;

    @NotNull(message = "Значение должно быть true или false")
    @Column(name = "raining")
    private boolean raining;

    @ManyToOne
    @JoinColumn(name = "sensor_id", referencedColumnName = "id")
    private Sensor sensor;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}