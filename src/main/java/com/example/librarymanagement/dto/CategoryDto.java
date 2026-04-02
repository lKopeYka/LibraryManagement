package com.example.librarymanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {

    @Schema(description = "Уникальный идентификатор категории", example = "1")
    private Long id;

    @Schema(description = "Название категории", example = "Роман", required = true)
    @NotBlank(message = "Название категории не может быть пустым")
    @Size(min = 2, max = 50, message = "Название категории должно быть от 2 до 50 символов")
    private String name;

    @Schema(description = "Описание категории", example = "Художественные произведения")
    @Size(max = 200, message = "Описание не должно превышать 200 символов")
    private String description;
}