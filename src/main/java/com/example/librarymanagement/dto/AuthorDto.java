package com.example.librarymanagement.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDto {

    private Long id;

    @NotBlank(message = "Имя автора не может быть пустым")
    @Size(min = 2, max = 100, message = "Имя автора должно быть от 2 до 100 символов")
    private String name;

    @Past(message = "Дата рождения должна быть в прошлом")
    private LocalDate birthDate;

    @Size(max = 50, message = "Страна рождения не должна превышать 50 символов")
    private String birthCountry;

    @Size(max = 1000, message = "Биография не должна превышать 1000 символов")
    private String biography;
}