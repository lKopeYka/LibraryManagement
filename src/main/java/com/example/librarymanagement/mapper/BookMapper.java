package com.example.librarymanagement.mapper;

import com.example.librarymanagement.dto.BookDto;
import com.example.librarymanagement.model.Book;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {
    //entity to dto
    public BookDto toDto(Book book) {
        if (book == null) {
            return null;
        }
        BookDto dto = new BookDto();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setYear(book.getYear());
        return dto;
    }
}