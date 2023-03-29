package com.udemy.springcourse.pojos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "sensor")
@Getter
@Setter
@NoArgsConstructor
public class Sensor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotEmpty(message = "Имя сенсора не должно быть пустым!")
    @Size(min = 3, max = 30, message = "Имя сенсора должно содержать от 3 до 30 символов")
    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "sensor")
    private List<Measurement> measurements;
}