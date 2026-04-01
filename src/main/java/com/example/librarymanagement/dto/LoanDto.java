package com.example.librarymanagement.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanDto {

    private Long id;

    @NotNull(message = "ID книги не может быть пустым")
    private Long bookId;

    @NotNull(message = "ID читателя не может быть пустым")
    private Long readerId;

    private String bookTitle;
    private String readerName;

    @PastOrPresent(message = "Дата выдачи не может быть в будущем")
    private LocalDate loanDate;

    @Future(message = "Дата возврата должна быть в будущем")
    private LocalDate dueDate;

    @PastOrPresent(message = "Дата фактического возврата не может быть в будущем")
    private LocalDate returnDate;

    private boolean returned;
}