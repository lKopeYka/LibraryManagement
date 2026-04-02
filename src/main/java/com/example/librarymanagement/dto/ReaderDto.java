package com.example.librarymanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReaderDto {

    @Schema(description = "Уникальный идентификатор читателя", example = "1")
    private Long id;

    @Schema(description = "Имя читателя", example = "Иван Петров", required = true)
    @NotBlank(message = "Имя читателя не может быть пустым")
    @Size(min = 2, max = 100, message = "Имя читателя должно быть от 2 до 100 символов")
    private String name;

    @Schema(description = "Email читателя", example = "ivan@mail.com", required = true)
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Неверный формат email")
    private String email;

    @Schema(description = "Дата регистрации", example = "2026-01-01")
    @PastOrPresent(message = "Дата регистрации не может быть в будущем")
    private LocalDate registrationDate;

    @Schema(description = "Телефон читателя", example = "+7-999-123-45-67")
    @Pattern(regexp = "^\\+?[0-9\\-\\s]{10,20}$", message = "Неверный формат телефона")
    private String phone;

    @Schema(description = "Адрес читателя", example = "ул. Ленина, д. 1, кв. 5")
    @Size(max = 200, message = "Адрес не должен превышать 200 символов")
    private String address;
}