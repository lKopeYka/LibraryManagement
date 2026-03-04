package com.example.librarymanagement.mapper;

import com.example.librarymanagement.dto.BookDto;
import com.example.librarymanagement.entity.Book;
import com.example.librarymanagement.entity.Author;
import com.example.librarymanagement.entity.Category;
import com.example.librarymanagement.repository.AuthorRepository;
import com.example.librarymanagement.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookMapper {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public BookDto toDto(Book book) {
        if (book == null) {
            return null;
        }

        BookDto dto = new BookDto();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setPublicationYear(book.getPublicationYear());
        dto.setIsbn(book.getIsbn());
        dto.setDescription(book.getDescription());

        if (book.getAuthorEntity() != null) {
            dto.setAuthorId(book.getAuthorEntity().getId());
        }

        if (book.getCategories() != null) {
            List<Long> categoryIds = book.getCategories().stream()
                    .map(Category::getId)
                    .collect(Collectors.toList());
            dto.setCategoryIds(categoryIds);
        }

        return dto;
    }

    public Book toEntity(BookDto dto) {
        if (dto == null) return null;

        Book book = new Book();
        book.setId(dto.getId());
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setPublicationYear(dto.getPublicationYear());
        book.setIsbn(dto.getIsbn());
        book.setDescription(dto.getDescription());

        if (dto.getAuthorId() != null) {
            authorRepository.findById(dto.getAuthorId())
                    .ifPresent(book::setAuthorEntity);
        }

        return book;
    }
}