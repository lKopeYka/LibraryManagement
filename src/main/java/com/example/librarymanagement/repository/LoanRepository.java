package com.example.librarymanagement.repository;

import com.example.librarymanagement.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    // Все выдачи читателя
    List<Loan> findByReaderId(Long readerId);

    // Все выдачи книги
    List<Loan> findByBookId(Long bookId);

    // Активные выдачи (книги не возвращены) - ЗАМЕНА для findByReturnedFalse()
    List<Loan> findByReturnDateIsNull();

    // Завершённые выдачи (книги возвращены)
    List<Loan> findByReturnDateIsNotNull();
}