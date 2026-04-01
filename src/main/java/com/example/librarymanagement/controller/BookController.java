package com.example.librarymanagement.controller;

import com.example.librarymanagement.dto.BookDto;
import com.example.librarymanagement.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<List<BookDto>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBookById(@PathVariable Long id) {
        BookDto book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }

    @PostMapping
    public ResponseEntity<BookDto> createBook(@Valid @RequestBody BookDto bookDto) {
        BookDto createdBook = bookService.createBook(bookDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDto> updateBook(@PathVariable Long id, @Valid @RequestBody BookDto bookDto) {
        BookDto updatedBook = bookService.updateBook(id, bookDto);
        return ResponseEntity.ok(updatedBook);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        boolean deleted = bookService.deleteBook(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<BookDto>> getBooksByAuthor(@RequestParam(required = false) String author) {
        if (author != null && !author.isEmpty()) {
            return ResponseEntity.ok(bookService.getBooksByAuthor(author));
        }
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/by-author/{authorId}")
    public ResponseEntity<List<BookDto>> getBooksByAuthorId(@PathVariable Long authorId) {
        return ResponseEntity.ok(bookService.getBooksByAuthorId(authorId));
    }

    @GetMapping("/by-category/{categoryId}")
    public ResponseEntity<List<BookDto>> getBooksByCategoryId(@PathVariable Long categoryId) {
        return ResponseEntity.ok(bookService.getBooksByCategoryId(categoryId));
    }

    @GetMapping("/search/advanced/page")
    public ResponseEntity<Page<BookDto>> searchBooksWithPagination(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer fromYear,
            @RequestParam(required = false) Integer toYear,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<BookDto> books = bookService.searchBooksWithPagination(author, title, fromYear, toYear, categoryId, page, size);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/search/advanced/native")
    public ResponseEntity<Page<BookDto>> searchBooksNative(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer fromYear,
            @RequestParam(required = false) Integer toYear,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<BookDto> books = bookService.searchBooksNative(author, title, fromYear, toYear, categoryId, page, size);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/page")
    public ResponseEntity<Page<BookDto>> getBooksWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        Page<BookDto> bookPage = bookService.getBooksWithPagination(page, size, sortBy, direction);
        return ResponseEntity.ok(bookPage);
    }

    @GetMapping("/page/simple")
    public ResponseEntity<Page<BookDto>> getBooksWithSimplePagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<BookDto> bookPage = bookService.getBooksWithPagination(page, size);
        return ResponseEntity.ok(bookPage);
    }

    @GetMapping("/page/search/cached")
    public ResponseEntity<Page<BookDto>> getBooksByAuthorNameWithPaginationAndCache(
            @RequestParam String authorName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<BookDto> bookPage = bookService.getBooksByAuthorNameWithPaginationAndCache(authorName, page, size);
        return ResponseEntity.ok(bookPage);
    }

    @PostMapping("/with-cache")
    public ResponseEntity<BookDto> createBookWithCache(@Valid @RequestBody BookDto bookDto) {
        BookDto createdBook = bookService.createBookWithCacheInvalidation(bookDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
    }

    @PutMapping("/with-cache/{id}")
    public ResponseEntity<BookDto> updateBookWithCache(@PathVariable Long id, @Valid @RequestBody BookDto bookDto) {
        BookDto updatedBook = bookService.updateBookWithCacheInvalidation(id, bookDto);
        if (updatedBook == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedBook);
    }

    @DeleteMapping("/with-cache/{id}")
    public ResponseEntity<Void> deleteBookWithCache(@PathVariable Long id) {
        boolean deleted = bookService.deleteBookWithCacheInvalidation(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}