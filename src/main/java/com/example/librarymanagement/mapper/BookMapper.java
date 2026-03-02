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
    //dto to entity
    public Book toEntity(BookDto dto) {
        if (dto == null) {
            return null;
        }
        Book book = new Book();
        book.setId(dto.getId());
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setYear(dto.getYear());
        return book;
    }
}