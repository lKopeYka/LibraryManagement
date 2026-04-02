package com.example.librarymanagement.controller;

import com.example.librarymanagement.dto.LoanDto;
import com.example.librarymanagement.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Loans", description = "Управление выдачами книг")
@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    @Autowired
    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @Operation(summary = "Получить все выдачи", description = "Возвращает список всех выдач")
    @GetMapping
    public ResponseEntity<List<LoanDto>> getAllLoans() {
        return ResponseEntity.ok(loanService.getAllLoans());
    }

    @Operation(summary = "Получить активные выдачи", description = "Возвращает список активных выдач (книги на руках)")
    @GetMapping("/active")
    public ResponseEntity<List<LoanDto>> getActiveLoans() {
        return ResponseEntity.ok(loanService.getActiveLoans());
    }

    @Operation(summary = "Получить завершённые выдачи", description = "Возвращает список завершённых выдач (книги возвращены)")
    @GetMapping("/completed")
    public ResponseEntity<List<LoanDto>> getCompletedLoans() {
        return ResponseEntity.ok(loanService.getCompletedLoans());
    }

    @Operation(summary = "Получить выдачу по ID", description = "Возвращает выдачу по её идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Выдача найдена"),
            @ApiResponse(responseCode = "404", description = "Выдача не найдена")
    })
    @GetMapping("/{id}")
    public ResponseEntity<LoanDto> getLoanById(@PathVariable Long id) {
        LoanDto loan = loanService.getLoanById(id);
        return ResponseEntity.ok(loan);
    }

    @Operation(summary = "Получить выдачи читателя", description = "Возвращает список выдач конкретного читателя")
    @GetMapping("/reader/{readerId}")
    public ResponseEntity<List<LoanDto>> getLoansByReaderId(@PathVariable Long readerId) {
        return ResponseEntity.ok(loanService.getLoansByReaderId(readerId));
    }

    @Operation(summary = "Получить выдачи книги", description = "Возвращает список выдач конкретной книги")
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<LoanDto>> getLoansByBookId(@PathVariable Long bookId) {
        return ResponseEntity.ok(loanService.getLoansByBookId(bookId));
    }

    @Operation(summary = "Создать новую выдачу", description = "Регистрирует выдачу книги читателю")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Выдача создана"),
            @ApiResponse(responseCode = "400", description = "Неверные данные")
    })
    @PostMapping
    public ResponseEntity<LoanDto> createLoan(@Valid @RequestBody LoanDto loanDto) {
        try {
            LoanDto createdLoan = loanService.createLoan(loanDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdLoan);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Вернуть книгу", description = "Отмечает выдачу как возвращённую")
    @PutMapping("/{id}/return")
    public ResponseEntity<LoanDto> returnBook(@PathVariable Long id) {
        LoanDto updatedLoan = loanService.returnBook(id);
        return ResponseEntity.ok(updatedLoan);
    }

    @Operation(summary = "Обновить выдачу", description = "Обновляет данные существующей выдачи")
    @PutMapping("/{id}")
    public ResponseEntity<LoanDto> updateLoan(@PathVariable Long id, @Valid @RequestBody LoanDto loanDto) {
        LoanDto updatedLoan = loanService.updateLoan(id, loanDto);
        return ResponseEntity.ok(updatedLoan);
    }

    @Operation(summary = "Удалить выдачу", description = "Удаляет выдачу по идентификатору")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLoan(@PathVariable Long id) {
        boolean deleted = loanService.deleteLoan(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}