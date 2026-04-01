package com.example.librarymanagement.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReaderDto {

    private Long id;

    @NotBlank(message = "Имя читателя не может быть пустым")
    @Size(min = 2, max = 100, message = "Имя читателя должно быть от 2 до 100 символов")
    private String name;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Неверный формат email")
    private String email;

    @PastOrPresent(message = "Дата регистрации не может быть в будущем")
    private LocalDate registrationDate;

    @Pattern(regexp = "^\\+?[0-9\\-\\s]{10,20}$", message = "Неверный формат телефона")
    private String phone;

    @Size(max = 200, message = "Адрес не должен превышать 200 символов")
    private String address;
}