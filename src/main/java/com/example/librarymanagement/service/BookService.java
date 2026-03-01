package com.example.librarymanagement.service;

import com.example.librarymanagement.dto.BookDto;
import com.example.librarymanagement.mapper.BookMapper;
import com.example.librarymanagement.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Autowired
    public BookService(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    public List<BookDto> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toDto)
                .toList();
    }

    public List<BookDto> getBooksByAuthor(String author) {
        return bookRepository.findAll().stream()
                .filter(book -> book.getAuthor().equalsIgnoreCase(author))
                .map(bookMapper::toDto)
                .toList();
    }

    public BookDto getBookById(Long id) {
        return bookRepository.findById(id)
                .map(bookMapper::toDto)
                .orElse(null);
    }
}