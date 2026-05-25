package com.example.librarymanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {

    @Schema(description = "Уникальный идентификатор книги", example = "1")
    private Long id;

    @Schema(description = "Название книги", example = "Война и мир", required = true)
    @NotBlank(message = "Название книги не может быть пустым")
    @Size(min = 1, max = 200, message = "Название книги должно быть от 1 до 200 символов")
    private String title;

    @Schema(description = "Год издания", example = "1869")
    @Min(value = 0, message = "Год издания не может быть отрицательным")
    @Max(value = 2100, message = "Год издания не может быть больше 2100")
    private Integer publicationYear;

    @Schema(description = "ISBN книги", example = "978-5-17-135127-6")
    @Pattern(regexp = "^(97(8|9))?\\d{9}(\\d|X)$", message = "Неверный формат ISBN")
    private String isbn;

    @Schema(description = "Описание книги", example = "Роман-эпопея о войне с Наполеоном")
    @Size(max = 1000, message = "Описание не должно превышать 1000 символов")
    private String description;

    @Schema(description = "ID автора", example = "1")
    private Long authorId;

    @Schema(description = "Список ID категорий", example = "[1, 2]")
    private List<Long> categoryIds;
}