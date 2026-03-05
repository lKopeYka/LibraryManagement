package com.example.librarymanagement.controller;

import com.example.librarymanagement.dto.LoanDto;
import com.example.librarymanagement.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    @Autowired
    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    // Все выдачи
    @GetMapping
    public ResponseEntity<List<LoanDto>> getAllLoans() {
        return ResponseEntity.ok(loanService.getAllLoans());
    }

    // Активные выдачи (книги на руках)
    @GetMapping("/active")
    public ResponseEntity<List<LoanDto>> getActiveLoans() {
        return ResponseEntity.ok(loanService.getActiveLoans());
    }

    // Завершённые выдачи
    @GetMapping("/completed")
    public ResponseEntity<List<LoanDto>> getCompletedLoans() {
        return ResponseEntity.ok(loanService.getCompletedLoans());
    }

    // Выдача по ID
    @GetMapping("/{id}")
    public ResponseEntity<LoanDto> getLoanById(@PathVariable Long id) {
        LoanDto loan = loanService.getLoanById(id);
        if (loan == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(loan);
    }

    // Выдачи читателя
    @GetMapping("/reader/{readerId}")
    public ResponseEntity<List<LoanDto>> getLoansByReaderId(@PathVariable Long readerId) {
        return ResponseEntity.ok(loanService.getLoansByReaderId(readerId));
    }

    // Выдачи книги
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<LoanDto>> getLoansByBookId(@PathVariable Long bookId) {
        return ResponseEntity.ok(loanService.getLoansByBookId(bookId));
    }

    // Создать выдачу
    @PostMapping
    public ResponseEntity<LoanDto> createLoan(@RequestBody LoanDto loanDto) {
        try {
            LoanDto createdLoan = loanService.createLoan(loanDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdLoan);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Вернуть книгу
    @PutMapping("/{id}/return")
    public ResponseEntity<LoanDto> returnBook(@PathVariable Long id) {
        LoanDto updatedLoan = loanService.returnBook(id);
        if (updatedLoan == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedLoan);
    }

    // Обновить выдачу
    @PutMapping("/{id}")
    public ResponseEntity<LoanDto> updateLoan(@PathVariable Long id, @RequestBody LoanDto loanDto) {
        LoanDto updatedLoan = loanService.updateLoan(id, loanDto);
        if (updatedLoan == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedLoan);
    }

    // Удалить выдачу
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLoan(@PathVariable Long id) {
        boolean deleted = loanService.deleteLoan(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}