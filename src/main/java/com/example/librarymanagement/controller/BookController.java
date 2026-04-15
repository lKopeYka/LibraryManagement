package com.example.librarymanagement.controller;

import com.example.librarymanagement.dto.BookDto;
import com.example.librarymanagement.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Books", description = "Управление книгами")
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Operation(summary = "Массовое создание книг (bulk-операция)")
    @PostMapping("/bulk")
    public ResponseEntity<List<BookDto>> createBooksBulk(@Valid @RequestBody List<BookDto> bookDtos) {
        List<BookDto> createdBooks = bookService.createBooksBulk(bookDtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBooks);
    }

    @Operation(summary = "Массовое создание книг (без транзакции)")
    @PostMapping("/bulk/without-transaction")
    public ResponseEntity<List<BookDto>> createBooksBulkWithoutTransaction(@Valid @RequestBody List<BookDto> bookDtos) {
        List<BookDto> createdBooks = bookService.createBooksBulkWithoutTransaction(bookDtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBooks);
    }

    @Operation(summary = "Массовое создание книг (с транзакцией)")
    @PostMapping("/bulk/with-transaction")
    public ResponseEntity<List<BookDto>> createBooksBulkWithTransaction(@Valid @RequestBody List<BookDto> bookDtos) {
        List<BookDto> createdBooks = bookService.createBooksBulkWithTransaction(bookDtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBooks);
    }
}