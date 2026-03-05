package com.example.librarymanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanDto {
    private Long id;
    private Long bookId;
    private Long readerId;
    private String bookTitle;
    private String readerName;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnDate;

    public boolean isReturned() {
        return returnDate != null;
    }
}