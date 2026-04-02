package com.example.librarymanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorWithBooksDto {

    @Schema(description = "Информация об авторе", required = true)
    @NotNull(message = "Автор не может быть пустым")
    @Valid
    private AuthorDto author;

    @Schema(description = "Список книг автора")
    @Valid
    private List<BookDto> books;
}