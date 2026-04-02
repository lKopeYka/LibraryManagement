package com.example.librarymanagement.controller;

import com.example.librarymanagement.dto.BookDto;
import com.example.librarymanagement.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "Получить все книги", description = "Возвращает список всех книг")
    @GetMapping
    public ResponseEntity<List<BookDto>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @Operation(summary = "Получить книгу по ID", description = "Возвращает книгу по её идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Книга найдена"),
            @ApiResponse(responseCode = "404", description = "Книга не найдена")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBookById(@PathVariable Long id) {
        BookDto book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }

    @Operation(summary = "Создать новую книгу", description = "Добавляет новую книгу в библиотеку")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Книга создана"),
            @ApiResponse(responseCode = "400", description = "Неверные данные")
    })
    @PostMapping
    public ResponseEntity<BookDto> createBook(@Valid @RequestBody BookDto bookDto) {
        BookDto createdBook = bookService.createBook(bookDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
    }

    @Operation(summary = "Обновить книгу", description = "Обновляет данные существующей книги")
    @PutMapping("/{id}")
    public ResponseEntity<BookDto> updateBook(@PathVariable Long id, @Valid @RequestBody BookDto bookDto) {
        BookDto updatedBook = bookService.updateBook(id, bookDto);
        return ResponseEntity.ok(updatedBook);
    }

    @Operation(summary = "Удалить книгу", description = "Удаляет книгу по идентификатору")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        boolean deleted = bookService.deleteBook(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Поиск книг", description = "Поиск книг по автору")
    @GetMapping("/search")
    public ResponseEntity<List<BookDto>> getBooksByAuthor(@RequestParam(required = false) String author) {
        if (author != null && !author.isEmpty()) {
            return ResponseEntity.ok(bookService.getBooksByAuthor(author));
        }
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @Operation(summary = "Сложный поиск с пагинацией", description = "Поиск книг по автору, названию, году и категории")
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

    @Operation(summary = "Сложный поиск с пагинацией (native SQL)", description = "Поиск книг через native SQL")
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

    @Operation(summary = "Получить книги с пагинацией", description = "Возвращает страницу книг")
    @GetMapping("/page")
    public ResponseEntity<Page<BookDto>> getBooksWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        Page<BookDto> bookPage = bookService.getBooksWithPagination(page, size, sortBy, direction);
        return ResponseEntity.ok(bookPage);
    }

    @Operation(summary = "Поиск с кэшем", description = "Поиск книг по автору с использованием in-memory кэша")
    @GetMapping("/page/search/cached")
    public ResponseEntity<Page<BookDto>> getBooksByAuthorNameWithPaginationAndCache(
            @RequestParam String authorName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<BookDto> bookPage = bookService.getBooksByAuthorNameWithPaginationAndCache(authorName, page, size);
        return ResponseEntity.ok(bookPage);
    }

    @Operation(summary = "Создать книгу с инвалидацией кэша", description = "Создаёт книгу и очищает кэш")
    @PostMapping("/with-cache")
    public ResponseEntity<BookDto> createBookWithCache(@Valid @RequestBody BookDto bookDto) {
        BookDto createdBook = bookService.createBookWithCacheInvalidation(bookDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
    }

    @Operation(summary = "Обновить книгу с инвалидацией кэша", description = "Обновляет книгу и очищает кэш")
    @PutMapping("/with-cache/{id}")
    public ResponseEntity<BookDto> updateBookWithCache(@PathVariable Long id, @Valid @RequestBody BookDto bookDto) {
        BookDto updatedBook = bookService.updateBookWithCacheInvalidation(id, bookDto);
        if (updatedBook == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedBook);
    }

    @Operation(summary = "Удалить книгу с инвалидацией кэша", description = "Удаляет книгу и очищает кэш")
    @DeleteMapping("/with-cache/{id}")
    public ResponseEntity<Void> deleteBookWithCache(@PathVariable Long id) {
        boolean deleted = bookService.deleteBookWithCacheInvalidation(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}