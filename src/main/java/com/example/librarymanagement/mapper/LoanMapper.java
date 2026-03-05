package com.example.librarymanagement.mapper;

import com.example.librarymanagement.dto.LoanDto;
import com.example.librarymanagement.entity.Loan;
import org.springframework.stereotype.Component;

@Component
public class LoanMapper {

    public LoanDto toDto(Loan loan) {
        if (loan == null) {
            return null;
        }

        LoanDto dto = new LoanDto();
        dto.setId(loan.getId());
        dto.setLoanDate(loan.getLoanDate());
        dto.setDueDate(loan.getDueDate());
        dto.setReturnDate(loan.getReturnDate());

        if (loan.getBook() != null) {
            dto.setBookId(loan.getBook().getId());
            dto.setBookTitle(loan.getBook().getTitle());
        }

        if (loan.getReader() != null) {
            dto.setReaderId(loan.getReader().getId());
            dto.setReaderName(loan.getReader().getName());
        }

        return dto;
    }

    public Loan toEntity(LoanDto dto) {
        if (dto == null) {
            return null;
        }

        Loan loan = new Loan();
        loan.setId(dto.getId());
        loan.setLoanDate(dto.getLoanDate());
        loan.setDueDate(dto.getDueDate());
        loan.setReturnDate(dto.getReturnDate());
        // Book и Reader устанавливаются в сервисе через репозитории
        return loan;
    }
}