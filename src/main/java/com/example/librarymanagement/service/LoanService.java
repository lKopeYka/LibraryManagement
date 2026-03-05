package com.example.librarymanagement.service;

import com.example.librarymanagement.dto.LoanDto;
import com.example.librarymanagement.entity.Book;
import com.example.librarymanagement.entity.Loan;
import com.example.librarymanagement.entity.Reader;
import com.example.librarymanagement.mapper.LoanMapper;
import com.example.librarymanagement.repository.BookRepository;
import com.example.librarymanagement.repository.LoanRepository;
import com.example.librarymanagement.repository.ReaderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final ReaderRepository readerRepository;
    private final LoanMapper loanMapper;

    @Autowired
    public LoanService(LoanRepository loanRepository,
                       BookRepository bookRepository,
                       ReaderRepository readerRepository,
                       LoanMapper loanMapper) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.readerRepository = readerRepository;
        this.loanMapper = loanMapper;
    }

    // Получить все выдачи
    public List<LoanDto> getAllLoans() {
        return loanRepository.findAll().stream()
                .map(loanMapper::toDto)
                .collect(Collectors.toList());
    }

    // Получить выдачу по ID
    public LoanDto getLoanById(Long id) {
        return loanRepository.findById(id)
                .map(loanMapper::toDto)
                .orElse(null);
    }

    // Получить выдачи читателя
    public List<LoanDto> getLoansByReaderId(Long readerId) {
        return loanRepository.findByReaderId(readerId).stream()
                .map(loanMapper::toDto)
                .collect(Collectors.toList());
    }

    // Получить выдачи книги
    public List<LoanDto> getLoansByBookId(Long bookId) {
        return loanRepository.findByBookId(bookId).stream()
                .map(loanMapper::toDto)
                .collect(Collectors.toList());
    }

    // Получить активные выдачи (книги на руках)
    public List<LoanDto> getActiveLoans() {
        return loanRepository.findByReturnDateIsNull().stream()
                .map(loanMapper::toDto)
                .collect(Collectors.toList());
    }

    // Получить завершённые выдачи
    public List<LoanDto> getCompletedLoans() {
        return loanRepository.findByReturnDateIsNotNull().stream()
                .map(loanMapper::toDto)
                .collect(Collectors.toList());
    }

    // Создать новую выдачу
    @Transactional
    public LoanDto createLoan(LoanDto loanDto) {
        Book book = bookRepository.findById(loanDto.getBookId())
                .orElseThrow(() -> new RuntimeException("Книга не найдена с ID: " + loanDto.getBookId()));

        Reader reader = readerRepository.findById(loanDto.getReaderId())
                .orElseThrow(() -> new RuntimeException("Читатель не найден с ID: " + loanDto.getReaderId()));

        Loan loan = loanMapper.toEntity(loanDto);
        loan.setBook(book);
        loan.setReader(reader);

        // Если дата выдачи не указана, ставим сегодня
        if (loan.getLoanDate() == null) {
            loan.setLoanDate(LocalDate.now());
        }

        // Если дата возврата не указана, ставим через 14 дней
        if (loan.getDueDate() == null) {
            loan.setDueDate(LocalDate.now().plusDays(14));
        }

        Loan savedLoan = loanRepository.save(loan);
        return loanMapper.toDto(savedLoan);
    }

    // Отметить книгу как возвращённую
    @Transactional
    public LoanDto returnBook(Long id) {
        return loanRepository.findById(id)
                .map(loan -> {
                    loan.setReturnDate(LocalDate.now());
                    Loan updatedLoan = loanRepository.save(loan);
                    return loanMapper.toDto(updatedLoan);
                })
                .orElse(null);
    }

    // Обновить выдачу
    @Transactional
    public LoanDto updateLoan(Long id, LoanDto loanDto) {
        return loanRepository.findById(id)
                .map(existingLoan -> {
                    // Обновляем только разрешённые поля
                    if (loanDto.getDueDate() != null) {
                        existingLoan.setDueDate(loanDto.getDueDate());
                    }
                    if (loanDto.getReturnDate() != null) {
                        existingLoan.setReturnDate(loanDto.getReturnDate());
                    }

                    // Если хотим изменить книгу или читателя, нужно проверить их существование
                    if (loanDto.getBookId() != null && !loanDto.getBookId().equals(existingLoan.getBook().getId())) {
                        Book book = bookRepository.findById(loanDto.getBookId())
                                .orElseThrow(() -> new RuntimeException("Книга не найдена"));
                        existingLoan.setBook(book);
                    }

                    if (loanDto.getReaderId() != null && !loanDto.getReaderId().equals(existingLoan.getReader().getId())) {
                        Reader reader = readerRepository.findById(loanDto.getReaderId())
                                .orElseThrow(() -> new RuntimeException("Читатель не найдена"));
                        existingLoan.setReader(reader);
                    }

                    Loan updatedLoan = loanRepository.save(existingLoan);
                    return loanMapper.toDto(updatedLoan);
                })
                .orElse(null);
    }

    // Удалить выдачу
    public boolean deleteLoan(Long id) {
        if (loanRepository.existsById(id)) {
            loanRepository.deleteById(id);
            return true;
        }
        return false;
    }
}