package com.example.librarymanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDto {

    @Schema(description = "Уникальный идентификатор автора", example = "1")
    private Long id;

    @Schema(description = "Имя автора", example = "Лев Толстой", required = true)
    @NotBlank(message = "Имя автора не может быть пустым")
    @Size(min = 2, max = 100, message = "Имя автора должно быть от 2 до 100 символов")
    private String name;

    @Schema(description = "Дата рождения", example = "1828-09-09")
    @Past(message = "Дата рождения должна быть в прошлом")
    private LocalDate birthDate;

    @Schema(description = "Страна рождения", example = "Россия")
    @Size(max = 50, message = "Страна рождения не должна превышать 50 символов")
    private String birthCountry;

    @Schema(description = "Биография", example = "Великий русский писатель")
    @Size(max = 1000, message = "Биография не должна превышать 1000 символов")
    private String biography;
}