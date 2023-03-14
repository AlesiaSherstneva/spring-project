package com.udemy.springcourse.pojo;

import com.udemy.springcourse.validators.CurrentYear;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Objects;


@Entity
@Table(name = "Person")
@Getter
@Setter
@NoArgsConstructor
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotNull(message = "ФИО не может быть пустым")
    @Pattern(regexp = "([А-ЯЁ][а-яА-ЯёЁ\\-]+\\s){2}[А-ЯЁ][а-яё]+",
            message = "Должен быть формат \"Фамилия Имя Отчество\" на русском языке")
    @Column(name = "name")
    private String name;

    @Min(value = 1900, message = "Год рождения должен быть больше 1900")
    @CurrentYear
    @Column(name = "year")
    private int year;

    @OneToMany(mappedBy = "reader")
    private List<Book> books;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person person = (Person) o;
        return getYear() == person.getYear() && getName().equals(person.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getYear());
    }
}