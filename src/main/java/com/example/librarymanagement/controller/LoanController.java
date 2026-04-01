package com.example.librarymanagement.controller;

import com.example.librarymanagement.dto.LoanDto;
import com.example.librarymanagement.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    @Autowired
    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping
    public ResponseEntity<List<LoanDto>> getAllLoans() {
        return ResponseEntity.ok(loanService.getAllLoans());
    }

    @GetMapping("/active")
    public ResponseEntity<List<LoanDto>> getActiveLoans() {
        return ResponseEntity.ok(loanService.getActiveLoans());
    }

    @GetMapping("/completed")
    public ResponseEntity<List<LoanDto>> getCompletedLoans() {
        return ResponseEntity.ok(loanService.getCompletedLoans());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanDto> getLoanById(@PathVariable Long id) {
        LoanDto loan = loanService.getLoanById(id);
        return ResponseEntity.ok(loan);
    }

    @GetMapping("/reader/{readerId}")
    public ResponseEntity<List<LoanDto>> getLoansByReaderId(@PathVariable Long readerId) {
        return ResponseEntity.ok(loanService.getLoansByReaderId(readerId));
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<LoanDto>> getLoansByBookId(@PathVariable Long bookId) {
        return ResponseEntity.ok(loanService.getLoansByBookId(bookId));
    }

    @PostMapping
    public ResponseEntity<LoanDto> createLoan(@Valid @RequestBody LoanDto loanDto) {
        try {
            LoanDto createdLoan = loanService.createLoan(loanDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdLoan);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<LoanDto> returnBook(@PathVariable Long id) {
        LoanDto updatedLoan = loanService.returnBook(id);
        return ResponseEntity.ok(updatedLoan);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LoanDto> updateLoan(@PathVariable Long id, @Valid @RequestBody LoanDto loanDto) {
        LoanDto updatedLoan = loanService.updateLoan(id, loanDto);
        return ResponseEntity.ok(updatedLoan);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLoan(@PathVariable Long id) {
        boolean deleted = loanService.deleteLoan(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}