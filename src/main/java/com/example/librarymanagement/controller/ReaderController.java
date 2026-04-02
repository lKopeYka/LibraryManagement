package com.example.librarymanagement.controller;

import com.example.librarymanagement.dto.ReaderDto;
import com.example.librarymanagement.service.ReaderService;
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

@Tag(name = "Readers", description = "Управление читателями")
@RestController
@RequestMapping("/api/readers")
public class ReaderController {

    private final ReaderService readerService;

    @Autowired
    public ReaderController(ReaderService readerService) {
        this.readerService = readerService;
    }

    @Operation(summary = "Получить всех читателей", description = "Возвращает список всех читателей")
    @GetMapping
    public ResponseEntity<List<ReaderDto>> getAllReaders() {
        return ResponseEntity.ok(readerService.getAllReaders());
    }

    @Operation(summary = "Получить читателя по ID", description = "Возвращает читателя по его идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Читатель найден"),
            @ApiResponse(responseCode = "404", description = "Читатель не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReaderDto> getReaderById(@PathVariable Long id) {
        ReaderDto reader = readerService.getReaderById(id);
        return ResponseEntity.ok(reader);
    }

    @Operation(summary = "Получить читателя по email", description = "Возвращает читателя по email")
    @GetMapping("/email/{email}")
    public ResponseEntity<ReaderDto> getReaderByEmail(@PathVariable String email) {
        ReaderDto reader = readerService.getReaderByEmail(email);
        return ResponseEntity.ok(reader);
    }

    @Operation(summary = "Создать нового читателя", description = "Добавляет нового читателя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Читатель создан"),
            @ApiResponse(responseCode = "400", description = "Неверные данные")
    })
    @PostMapping
    public ResponseEntity<ReaderDto> createReader(@Valid @RequestBody ReaderDto readerDto) {
        ReaderDto createdReader = readerService.createReader(readerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReader);
    }

    @Operation(summary = "Обновить читателя", description = "Обновляет данные существующего читателя")
    @PutMapping("/{id}")
    public ResponseEntity<ReaderDto> updateReader(@PathVariable Long id, @Valid @RequestBody ReaderDto readerDto) {
        ReaderDto updatedReader = readerService.updateReader(id, readerDto);
        return ResponseEntity.ok(updatedReader);
    }

    @Operation(summary = "Удалить читателя", description = "Удаляет читателя по идентификатору")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReader(@PathVariable Long id) {
        boolean deleted = readerService.deleteReader(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}