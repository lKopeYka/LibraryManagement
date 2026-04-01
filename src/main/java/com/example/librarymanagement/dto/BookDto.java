package com.example.librarymanagement.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {

    private Long id;

    @NotBlank(message = "Название книги не может быть пустым")
    @Size(min = 1, max = 200, message = "Название книги должно быть от 1 до 200 символов")
    private String title;

    @NotBlank(message = "Автор не может быть пустым")
    @Size(min = 1, max = 100, message = "Имя автора должно быть от 1 до 100 символов")
    private String author;

    @Min(value = 0, message = "Год издания не может быть отрицательным")
    @Max(value = 2100, message = "Год издания не может быть больше 2100")
    private Integer publicationYear;

    @Pattern(regexp = "^(97(8|9))?\\d{9}(\\d|X)$", message = "Неверный формат ISBN")
    private String isbn;

    @Size(max = 1000, message = "Описание не должно превышать 1000 символов")
    private String description;

    private Long authorId;
    private List<Long> categoryIds;
}