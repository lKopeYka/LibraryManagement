package com.example.librarymanagement.dto;

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

    @NotNull(message = "Автор не может быть пустым")
    @Valid
    private AuthorDto author;

    @Valid
    private List<BookDto> books;
}