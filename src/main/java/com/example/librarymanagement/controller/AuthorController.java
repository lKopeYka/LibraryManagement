package com.example.librarymanagement.controller;

import com.example.librarymanagement.dto.AuthorDto;
import com.example.librarymanagement.dto.AuthorWithBooksDto;
import com.example.librarymanagement.service.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Authors", description = "Управление авторами")
@RestController
@RequestMapping("/api/authors")
public class AuthorController {

    private final AuthorService authorService;

    @Autowired
    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @Operation(summary = "Получить всех авторов", description = "Возвращает список всех авторов")
    @GetMapping
    public ResponseEntity<List<AuthorDto>> getAllAuthors() {
        return ResponseEntity.ok(authorService.getAllAuthors());
    }

    @Operation(summary = "Получить автора по ID", description = "Возвращает автора по его идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Автор найден"),
            @ApiResponse(responseCode = "404", description = "Автор не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AuthorDto> getAuthorById(@PathVariable Long id) {
        AuthorDto author = authorService.getAuthorById(id);
        return ResponseEntity.ok(author);
    }

    @Operation(summary = "Создать нового автора", description = "Добавляет нового автора")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Автор создан"),
            @ApiResponse(responseCode = "400", description = "Неверные данные")
    })
    @PostMapping
    public ResponseEntity<AuthorDto> createAuthor(@Valid @RequestBody AuthorDto authorDto) {
        AuthorDto createdAuthor = authorService.createAuthor(authorDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAuthor);
    }

    @Operation(summary = "Обновить автора", description = "Обновляет данные существующего автора")
    @PutMapping("/{id}")
    public ResponseEntity<AuthorDto> updateAuthor(@PathVariable Long id, @Valid @RequestBody AuthorDto authorDto) {
        AuthorDto updatedAuthor = authorService.updateAuthor(id, authorDto);
        return ResponseEntity.ok(updatedAuthor);
    }

    @Operation(summary = "Удалить автора", description = "Удаляет автора по идентификатору")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        boolean deleted = authorService.deleteAuthor(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Сохранить автора с книгами (без транзакции)", description = "Демонстрация частичного сохранения")
    @PostMapping("/with-books/without-transaction")
    public ResponseEntity<String> saveAuthorWithBooksWithoutTransaction(
            @Valid @RequestBody AuthorWithBooksDto dto) {
        try {
            authorService.saveAuthorWithBooksWithoutTransaction(dto);
            return ResponseEntity.ok("Сохранено (без транзакции)");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка: " + e.getMessage() + " - проверьте БД, автор мог сохраниться!");
        }
    }

    @Operation(summary = "Сохранить автора с книгами (с транзакцией)", description = "Демонстрация полного отката")
    @PostMapping("/with-books/with-transaction")
    public ResponseEntity<String> saveAuthorWithBooksWithTransaction(
            @Valid @RequestBody AuthorWithBooksDto dto) {
        try {
            authorService.saveAuthorWithBooksWithTransaction(dto);
            return ResponseEntity.ok("Сохранено (с транзакцией)");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка: " + e.getMessage() + " - всё откатилось, проверьте БД");
        }
    }
}