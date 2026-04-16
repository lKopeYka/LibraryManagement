package com.example.librarymanagement.service;

import com.example.librarymanagement.dto.AuthorDto;
import com.example.librarymanagement.dto.AuthorWithBooksDto;
import com.example.librarymanagement.dto.BookDto;
import com.example.librarymanagement.entity.Author;
import com.example.librarymanagement.entity.Book;
import com.example.librarymanagement.exception.ResourceNotFoundException;
import com.example.librarymanagement.mapper.AuthorMapper;
import com.example.librarymanagement.mapper.BookMapper;
import com.example.librarymanagement.repository.AuthorRepository;
import com.example.librarymanagement.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorMapper authorMapper;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private AuthorService authorService;

    private AuthorDto authorDto;
    private Author author;
    private BookDto bookDto;
    private Book book;

    @BeforeEach
    void setUp() {
        authorDto = new AuthorDto();
        authorDto.setId(1L);
        authorDto.setName("Лев Толстой");

        author = new Author();
        author.setId(1L);
        author.setName("Лев Толстой");

        bookDto = new BookDto();
        bookDto.setTitle("Война и мир");

        book = new Book();
        book.setTitle("Война и мир");
    }

    @Test
    void createAuthor_Success() {
        when(authorMapper.toEntity(authorDto)).thenReturn(author);
        when(authorRepository.save(any(Author.class))).thenReturn(author);
        when(authorMapper.toDto(author)).thenReturn(authorDto);

        AuthorDto result = authorService.createAuthor(authorDto);

        assertNotNull(result);
        verify(authorRepository, times(1)).save(any(Author.class));
    }

    @Test
    void getAllAuthors_Success() {
        when(authorRepository.findAll()).thenReturn(List.of(author));
        when(authorMapper.toDto(author)).thenReturn(authorDto);

        List<AuthorDto> result = authorService.getAllAuthors();

        assertEquals(1, result.size());
    }

    @Test
    void getAuthorById_Success() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(authorMapper.toDto(author)).thenReturn(authorDto);

        AuthorDto result = authorService.getAuthorById(1L);

        assertNotNull(result);
        assertEquals("Лев Толстой", result.getName());
    }

    @Test
    void getAuthorById_NotFound() {
        when(authorRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            authorService.getAuthorById(999L);
        });
    }

    @Test
    void updateAuthor_Success() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(authorRepository.save(any(Author.class))).thenReturn(author);
        when(authorMapper.toDto(author)).thenReturn(authorDto);

        AuthorDto result = authorService.updateAuthor(1L, authorDto);

        assertNotNull(result);
        verify(authorRepository, times(1)).save(any(Author.class));
    }

    @Test
    void updateAuthor_NotFound() {
        when(authorRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            authorService.updateAuthor(999L, authorDto);
        });
    }

    @Test
    void deleteAuthor_Success() {
        when(authorRepository.existsById(1L)).thenReturn(true);

        boolean result = authorService.deleteAuthor(1L);

        assertTrue(result);
        verify(authorRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteAuthor_NotFound() {
        when(authorRepository.existsById(999L)).thenReturn(false);

        boolean result = authorService.deleteAuthor(999L);

        assertFalse(result);
        verify(authorRepository, never()).deleteById(anyLong());
    }

    @Test
    void saveAuthorWithBooksWithoutTransaction_Success() {
        AuthorWithBooksDto dto = new AuthorWithBooksDto();
        dto.setAuthor(authorDto);
        dto.setBooks(List.of(bookDto));

        when(authorMapper.toEntity(authorDto)).thenReturn(author);
        when(authorRepository.save(any(Author.class))).thenReturn(author);
        when(bookMapper.toEntity(bookDto)).thenReturn(book);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        assertDoesNotThrow(() -> {
            authorService.saveAuthorWithBooksWithoutTransaction(dto);
        });
    }

    @Test
    void saveAuthorWithBooksWithoutTransaction_Error() {
        AuthorWithBooksDto dto = new AuthorWithBooksDto();
        dto.setAuthor(authorDto);

        BookDto errorBookDto = new BookDto();
        errorBookDto.setTitle("");
        dto.setBooks(List.of(errorBookDto));

        when(authorMapper.toEntity(authorDto)).thenReturn(author);
        when(authorRepository.save(any(Author.class))).thenReturn(author);
        when(bookMapper.toEntity(errorBookDto)).thenReturn(book);
        doThrow(new RuntimeException("Ошибка при сохранении книги")).when(bookRepository).save(any(Book.class));

        assertThrows(RuntimeException.class, () -> {
            authorService.saveAuthorWithBooksWithoutTransaction(dto);
        });
    }

    @Test
    void saveAuthorWithBooksWithTransaction_Success() {
        AuthorWithBooksDto dto = new AuthorWithBooksDto();
        dto.setAuthor(authorDto);
        dto.setBooks(List.of(bookDto));

        when(authorMapper.toEntity(authorDto)).thenReturn(author);
        when(authorRepository.save(any(Author.class))).thenReturn(author);
        when(bookMapper.toEntity(bookDto)).thenReturn(book);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        assertDoesNotThrow(() -> {
            authorService.saveAuthorWithBooksWithTransaction(dto);
        });
    }

    @Test
    void saveAuthorWithBooksWithTransaction_Error() {
        AuthorWithBooksDto dto = new AuthorWithBooksDto();
        dto.setAuthor(authorDto);

        BookDto errorBookDto = new BookDto();
        errorBookDto.setTitle("");
        dto.setBooks(List.of(errorBookDto));

        when(authorMapper.toEntity(authorDto)).thenReturn(author);
        when(authorRepository.save(any(Author.class))).thenReturn(author);
        when(bookMapper.toEntity(errorBookDto)).thenReturn(book);
        doThrow(new RuntimeException("Ошибка при сохранении книги")).when(bookRepository).save(any(Book.class));

        assertThrows(RuntimeException.class, () -> {
            authorService.saveAuthorWithBooksWithTransaction(dto);
        });
    }
}