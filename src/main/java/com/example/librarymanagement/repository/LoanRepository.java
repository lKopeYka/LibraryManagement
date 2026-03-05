package com.example.librarymanagement.repository;

import com.example.librarymanagement.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByReaderId(Long readerId);
    List<Loan> findByBookId(Long bookId);
}