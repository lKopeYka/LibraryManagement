package com.example.librarymanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanDto {

    @Schema(description = "Уникальный идентификатор выдачи", example = "1")
    private Long id;

    @Schema(description = "ID книги", example = "1", required = true)
    @NotNull(message = "ID книги не может быть пустым")
    private Long bookId;

    @Schema(description = "ID читателя", example = "1", required = true)
    @NotNull(message = "ID читателя не может быть пустым")
    private Long readerId;

    @Schema(description = "Название книги", example = "Война и мир")
    private String bookTitle;

    @Schema(description = "Имя читателя", example = "Иван Петров")
    private String readerName;

    @Schema(description = "Дата выдачи", example = "2026-03-01")
    @PastOrPresent(message = "Дата выдачи не может быть в будущем")
    private LocalDate loanDate;

    @Schema(description = "Плановая дата возврата", example = "2026-03-15")
    @Future(message = "Дата возврата должна быть в будущем")
    private LocalDate dueDate;

    @Schema(description = "Фактическая дата возврата", example = "2026-03-10")
    @PastOrPresent(message = "Дата фактического возврата не может быть в будущем")
    private LocalDate returnDate;

    @Schema(description = "Статус возврата", example = "false")
    private boolean returned;
}