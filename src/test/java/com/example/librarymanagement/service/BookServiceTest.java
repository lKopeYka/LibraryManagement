package com.example.librarymanagement.service;

import com.example.librarymanagement.dto.BookDto;
import com.example.librarymanagement.entity.Author;
import com.example.librarymanagement.entity.Book;
import com.example.librarymanagement.entity.Category;
import com.example.librarymanagement.exception.ResourceNotFoundException;
import com.example.librarymanagement.mapper.BookMapper;
import com.example.librarymanagement.repository.BookRepository;
import com.example.librarymanagement.repository.AuthorRepository;
import com.example.librarymanagement.repository.CategoryRepository;
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
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookService bookService;

    private BookDto bookDto;
    private Book book;
    private Author author;
    private List<Category> categories;

    @BeforeEach
    void setUp() {
        author = new Author();
        author.setId(1L);
        author.setName("Лев Толстой");

        bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setTitle("Война и мир");
        bookDto.setAuthor("Лев Толстой");
        bookDto.setAuthorId(1L);
        bookDto.setCategoryIds(List.of(1L, 2L));

        book = new Book();
        book.setId(1L);
        book.setTitle("Война и мир");

        Category category1 = new Category();
        category1.setId(1L);
        Category category2 = new Category();
        category2.setId(2L);
        categories = List.of(category1, category2);
    }

    @Test
    void createBook_Success() {
        when(bookMapper.toEntity(bookDto)).thenReturn(book);
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto result = bookService.createBook(bookDto);

        assertNotNull(result);
        assertEquals("Война и мир", result.getTitle());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void createBooksBulk_Success() {
        when(bookMapper.toEntity(bookDto)).thenReturn(book);
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        List<BookDto> result = bookService.createBooksBulk(List.of(bookDto));

        assertEquals(1, result.size());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void createBooksBulkWithoutTransaction_Success() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(categoryRepository.findAllById(any())).thenReturn(categories);
        when(bookMapper.toEntity(bookDto)).thenReturn(book);
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        List<BookDto> result = bookService.createBooksBulkWithoutTransaction(List.of(bookDto));

        assertEquals(1, result.size());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void createBooksBulkWithoutTransaction_Error() {
        BookDto errorBookDto = new BookDto();
        errorBookDto.setTitle("Книга с Ошибкой");
        errorBookDto.setAuthorId(1L);

        Book errorBook = new Book();
        errorBook.setTitle("Книга с Ошибкой");

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(bookMapper.toEntity(errorBookDto)).thenReturn(errorBook);
        doThrow(new RuntimeException("Ошибка при сохранении книги")).when(bookRepository).save(any(Book.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookService.createBooksBulkWithoutTransaction(List.of(errorBookDto));
        });

        assertTrue(exception.getMessage().contains("Ошибка при сохранении книги"));
    }

    @Test
    void createBooksBulkWithTransaction_Success() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(categoryRepository.findAllById(any())).thenReturn(categories);
        when(bookMapper.toEntity(bookDto)).thenReturn(book);
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        List<BookDto> result = bookService.createBooksBulkWithTransaction(List.of(bookDto));

        assertEquals(1, result.size());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void createBooksBulkWithTransaction_RollbackOnError() {
        BookDto errorBookDto = new BookDto();
        errorBookDto.setTitle("Книга с Ошибкой");
        errorBookDto.setAuthorId(1L);

        Book errorBook = new Book();
        errorBook.setTitle("Книга с Ошибкой");

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(bookMapper.toEntity(errorBookDto)).thenReturn(errorBook);
        doThrow(new RuntimeException("Ошибка при сохранении книги")).when(bookRepository).save(any(Book.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookService.createBooksBulkWithTransaction(List.of(errorBookDto));
        });

        assertTrue(exception.getMessage().contains("Ошибка при сохранении книги"));
    }

    @Test
    void getAllBooks_Success() {
        when(bookRepository.findAllWithDetails()).thenReturn(List.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        List<BookDto> result = bookService.getAllBooks();

        assertEquals(1, result.size());
        verify(bookRepository, times(1)).findAllWithDetails();
    }

    @Test
    void getBookById_Success() {
        when(bookRepository.findWithAuthorAndCategoriesById(1L)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto result = bookService.getBookById(1L);

        assertNotNull(result);
        assertEquals("Война и мир", result.getTitle());
    }

    @Test
    void getBookById_NotFound() {
        when(bookRepository.findWithAuthorAndCategoriesById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            bookService.getBookById(999L);
        });
    }

    @Test
    void updateBook_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(categoryRepository.findAllById(any())).thenReturn(categories);
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto result = bookService.updateBook(1L, bookDto);

        assertNotNull(result);
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void updateBook_NotFound() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            bookService.updateBook(999L, bookDto);
        });
    }

    @Test
    void deleteBook_Success() {
        when(bookRepository.existsById(1L)).thenReturn(true);

        boolean result = bookService.deleteBook(1L);

        assertTrue(result);
        verify(bookRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteBook_NotFound() {
        when(bookRepository.existsById(999L)).thenReturn(false);

        boolean result = bookService.deleteBook(999L);

        assertFalse(result);
        verify(bookRepository, never()).deleteById(anyLong());
    }

    @Test
    void getBooksByAuthor_Success() {
        when(bookRepository.findByAuthor("Лев Толстой")).thenReturn(List.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        List<BookDto> result = bookService.getBooksByAuthor("Лев Толстой");

        assertEquals(1, result.size());
    }

    @Test
    void getBooksByAuthorId_Success() {
        when(bookRepository.findByAuthorEntityId(1L)).thenReturn(List.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        List<BookDto> result = bookService.getBooksByAuthorId(1L);

        assertEquals(1, result.size());
    }

    @Test
    void getBooksByCategoryId_Success() {
        when(bookRepository.findByCategoriesId(1L)).thenReturn(List.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        List<BookDto> result = bookService.getBooksByCategoryId(1L);

        assertEquals(1, result.size());
    }
}