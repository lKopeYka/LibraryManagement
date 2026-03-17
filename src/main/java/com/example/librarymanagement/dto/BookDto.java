package com.example.librarymanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {
    private Long id;
    private String title;
    private String author;
    private Integer publicationYear;
    private String isbn;
    private String description;
    private Long authorId;
    private List<Long> categoryIds;
}