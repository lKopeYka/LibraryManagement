package com.example.librarymanagement.service;

import com.example.librarymanagement.dto.LoanDto;
import com.example.librarymanagement.entity.Book;
import com.example.librarymanagement.entity.Loan;
import com.example.librarymanagement.entity.Reader;
import com.example.librarymanagement.exception.ResourceNotFoundException;
import com.example.librarymanagement.mapper.LoanMapper;
import com.example.librarymanagement.repository.BookRepository;
import com.example.librarymanagement.repository.LoanRepository;
import com.example.librarymanagement.repository.ReaderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ReaderRepository readerRepository;

    @Mock
    private LoanMapper loanMapper;

    @InjectMocks
    private LoanService loanService;

    private LoanDto loanDto;
    private Loan loan;
    private Book book;
    private Reader reader;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);

        reader = new Reader();
        reader.setId(1L);

        loanDto = new LoanDto();
        loanDto.setId(1L);
        loanDto.setBookId(1L);
        loanDto.setReaderId(1L);
        loanDto.setLoanDate(LocalDate.now());
        loanDto.setDueDate(LocalDate.now().plusDays(14));

        loan = new Loan();
        loan.setId(1L);
        loan.setBook(book);
        loan.setReader(reader);
        loan.setLoanDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(14));
    }

    @Test
    void getAllLoans_Success() {
        when(loanRepository.findAll()).thenReturn(List.of(loan));
        when(loanMapper.toDto(loan)).thenReturn(loanDto);

        List<LoanDto> result = loanService.getAllLoans();

        assertEquals(1, result.size());
        verify(loanRepository, times(1)).findAll();
    }

    @Test
    void getLoanById_Success() {
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanMapper.toDto(loan)).thenReturn(loanDto);

        LoanDto result = loanService.getLoanById(1L);

        assertNotNull(result);
    }

    @Test
    void getLoanById_NotFound() {
        when(loanRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            loanService.getLoanById(999L);
        });
    }

    @Test
    void getLoansByReaderId_Success() {
        when(loanRepository.findByReaderId(1L)).thenReturn(List.of(loan));
        when(loanMapper.toDto(loan)).thenReturn(loanDto);

        List<LoanDto> result = loanService.getLoansByReaderId(1L);

        assertEquals(1, result.size());
    }

    @Test
    void getLoansByBookId_Success() {
        when(loanRepository.findByBookId(1L)).thenReturn(List.of(loan));
        when(loanMapper.toDto(loan)).thenReturn(loanDto);

        List<LoanDto> result = loanService.getLoansByBookId(1L);

        assertEquals(1, result.size());
    }

    @Test
    void getActiveLoans_Success() {
        when(loanRepository.findByReturnDateIsNull()).thenReturn(List.of(loan));
        when(loanMapper.toDto(loan)).thenReturn(loanDto);

        List<LoanDto> result = loanService.getActiveLoans();

        assertEquals(1, result.size());
    }

    @Test
    void getCompletedLoans_Success() {
        when(loanRepository.findByReturnDateIsNotNull()).thenReturn(List.of(loan));
        when(loanMapper.toDto(loan)).thenReturn(loanDto);

        List<LoanDto> result = loanService.getCompletedLoans();

        assertEquals(1, result.size());
    }

    @Test
    void createLoan_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(readerRepository.findById(1L)).thenReturn(Optional.of(reader));
        when(loanMapper.toEntity(any(LoanDto.class))).thenReturn(loan);
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);
        when(loanMapper.toDto(any(Loan.class))).thenReturn(loanDto);

        LoanDto result = loanService.createLoan(loanDto);

        assertNotNull(result);
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    void createLoan_LoanDateNull_ShouldSetToNow() {
        LoanDto dto = new LoanDto();
        dto.setBookId(1L);
        dto.setReaderId(1L);
        dto.setLoanDate(null);
        dto.setDueDate(LocalDate.now().plusDays(14));

        Loan loanEntity = new Loan();
        loanEntity.setLoanDate(null);
        loanEntity.setDueDate(dto.getDueDate());

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(readerRepository.findById(1L)).thenReturn(Optional.of(reader));
        when(loanMapper.toEntity(any(LoanDto.class))).thenReturn(loanEntity);
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(loanMapper.toDto(any(Loan.class))).thenReturn(dto);

        LoanDto result = loanService.createLoan(dto);

        assertNotNull(result);
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    void createLoan_DueDateNull_ShouldSetToNowPlus14() {
        LoanDto dto = new LoanDto();
        dto.setBookId(1L);
        dto.setReaderId(1L);
        dto.setLoanDate(LocalDate.now());
        dto.setDueDate(null);

        Loan loanEntity = new Loan();
        loanEntity.setLoanDate(dto.getLoanDate());
        loanEntity.setDueDate(null);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(readerRepository.findById(1L)).thenReturn(Optional.of(reader));
        when(loanMapper.toEntity(any(LoanDto.class))).thenReturn(loanEntity);
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(loanMapper.toDto(any(Loan.class))).thenReturn(dto);

        LoanDto result = loanService.createLoan(dto);

        assertNotNull(result);
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    void createLoan_BothDatesNull_ShouldSetBoth() {
        LoanDto dto = new LoanDto();
        dto.setBookId(1L);
        dto.setReaderId(1L);
        dto.setLoanDate(null);
        dto.setDueDate(null);

        Loan loanEntity = new Loan();
        loanEntity.setLoanDate(null);
        loanEntity.setDueDate(null);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(readerRepository.findById(1L)).thenReturn(Optional.of(reader));
        when(loanMapper.toEntity(any(LoanDto.class))).thenReturn(loanEntity);
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(loanMapper.toDto(any(Loan.class))).thenReturn(dto);

        LoanDto result = loanService.createLoan(dto);

        assertNotNull(result);
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    void createLoan_BookNotFound() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            loanDto.setBookId(999L);
            loanService.createLoan(loanDto);
        });
    }

    @Test
    void createLoan_ReaderNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(readerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            loanDto.setReaderId(999L);
            loanService.createLoan(loanDto);
        });
    }

    @Test
    void returnBook_Success() {
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);
        when(loanMapper.toDto(loan)).thenReturn(loanDto);

        LoanDto result = loanService.returnBook(1L);

        assertNotNull(result);
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    void returnBook_NotFound() {
        when(loanRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            loanService.returnBook(999L);
        });
    }

    @Test
    void updateLoan_Success() {
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);
        when(loanMapper.toDto(loan)).thenReturn(loanDto);

        LoanDto result = loanService.updateLoan(1L, loanDto);

        assertNotNull(result);
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    void updateLoan_UpdateReturnDateOnly() {
        LoanDto updateDto = new LoanDto();
        updateDto.setReturnDate(LocalDate.now());

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);
        when(loanMapper.toDto(loan)).thenReturn(loanDto);

        LoanDto result = loanService.updateLoan(1L, updateDto);

        assertNotNull(result);
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    void updateLoan_UpdateBook() {
        Book newBook = new Book();
        newBook.setId(2L);

        LoanDto updateDto = new LoanDto();
        updateDto.setBookId(2L);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(bookRepository.findById(2L)).thenReturn(Optional.of(newBook));
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);
        when(loanMapper.toDto(loan)).thenReturn(loanDto);

        LoanDto result = loanService.updateLoan(1L, updateDto);

        assertNotNull(result);
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    void updateLoan_UpdateReader() {
        Reader newReader = new Reader();
        newReader.setId(2L);

        LoanDto updateDto = new LoanDto();
        updateDto.setReaderId(2L);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(readerRepository.findById(2L)).thenReturn(Optional.of(newReader));
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);
        when(loanMapper.toDto(loan)).thenReturn(loanDto);

        LoanDto result = loanService.updateLoan(1L, updateDto);

        assertNotNull(result);
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    void updateLoan_UpdateBookNotFound() {
        LoanDto updateDto = new LoanDto();
        updateDto.setBookId(999L);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            loanService.updateLoan(1L, updateDto);
        });
    }

    @Test
    void updateLoan_UpdateReaderNotFound() {
        LoanDto updateDto = new LoanDto();
        updateDto.setReaderId(999L);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(readerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            loanService.updateLoan(1L, updateDto);
        });
    }

    @Test
    void updateLoan_NotFound() {
        when(loanRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            loanService.updateLoan(999L, loanDto);
        });
    }

    @Test
    void deleteLoan_Success() {
        when(loanRepository.existsById(1L)).thenReturn(true);

        boolean result = loanService.deleteLoan(1L);

        assertTrue(result);
        verify(loanRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteLoan_NotFound() {
        when(loanRepository.existsById(999L)).thenReturn(false);

        boolean result = loanService.deleteLoan(999L);

        assertFalse(result);
        verify(loanRepository, never()).deleteById(anyLong());
    }
}