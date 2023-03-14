package com.udemy.springcourse.pojo;

import com.udemy.springcourse.validators.CurrentYear;
import lombok.*;

import javax.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"id"})
public class Person {
    private int id;

    @NotNull(message = "ФИО не может быть пустым")
    @Pattern(regexp = "([А-ЯЁ][а-яА-ЯёЁ\\-]+\\s){2}[А-ЯЁ][а-яё]+",
            message = "Должен быть формат \"Фамилия Имя Отчество\" на русском языке")
    private String name;

    @Min(value = 1900, message = "Год рождения должен быть больше 1900")
    @CurrentYear
    private int year;
}