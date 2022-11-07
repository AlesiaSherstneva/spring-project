package com.udemy.springcourse.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
public class PersonDTO {
    @NotEmpty(message = "Name should not be empty")
    @Size(min = 2, max = 30, message = "Name should be between two and thirty characters")
    private String name;

    @Min(value = 0, message = "Age should be greater than 0")
    private int age;

    @Email
    @NotEmpty(message = "Email should not be empty")
    private String email;
}
