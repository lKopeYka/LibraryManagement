package com.example.librarymanagement.service;

import com.example.librarymanagement.dto.BookDto;
import com.example.librarymanagement.entity.Book;
import com.example.librarymanagement.entity.Category;
import com.example.librarymanagement.mapper.BookMapper;
import com.example.librarymanagement.repository.BookRepository;
import com.example.librarymanagement.repository.AuthorRepository;
import com.example.librarymanagement.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final BookMapper bookMapper;

    @Autowired
    public BookService(BookRepository bookRepository,
                       AuthorRepository authorRepository,
                       CategoryRepository categoryRepository,
                       BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.categoryRepository = categoryRepository;
        this.bookMapper = bookMapper;
    }

    // Create
    public BookDto createBook(BookDto bookDto) {
        Book book = bookMapper.toEntity(bookDto);
        Book savedBook = bookRepository.save(book);
        return bookMapper.toDto(savedBook);
    }

    // Read all
    public List<BookDto> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toDto)
                .toList();
    }

    // Read by id
    public BookDto getBookById(Long id) {
        return bookRepository.findById(id)
                .map(bookMapper::toDto)
                .orElse(null);
    }

    // Update
    public BookDto updateBook(Long id, BookDto bookDto) {
        return bookRepository.findById(id)
                .map(existingBook -> {
                    existingBook.setTitle(bookDto.getTitle());
                    existingBook.setAuthor(bookDto.getAuthor());
                    existingBook.setPublicationYear(bookDto.getPublicationYear());
                    existingBook.setIsbn(bookDto.getIsbn());
                    existingBook.setDescription(bookDto.getDescription());

                    if (bookDto.getAuthorId() != null) {
                        authorRepository.findById(bookDto.getAuthorId())
                                .ifPresent(existingBook::setAuthorEntity);
                    }

                    if (bookDto.getCategoryIds() != null) {
                        List<Category> categories = categoryRepository.findAllById(bookDto.getCategoryIds());
                        existingBook.setCategories(categories);
                    }

                    Book updatedBook = bookRepository.save(existingBook);
                    return bookMapper.toDto(updatedBook);
                })
                .orElse(null);
    }

    // Delete
    public boolean deleteBook(Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Поиск по автору (строка)
    public List<BookDto> getBooksByAuthor(String author) {
        return bookRepository.findByAuthor(author).stream()
                .map(bookMapper::toDto)
                .toList();
    }

    // Поиск по ID автора
    public List<BookDto> getBooksByAuthorId(Long authorId) {
        return bookRepository.findByAuthorEntityId(authorId).stream()
                .map(bookMapper::toDto)
                .toList();
    }

    // Поиск по категории
    public List<BookDto> getBooksByCategoryId(Long categoryId) {
        return bookRepository.findByCategoriesId(categoryId).stream()
                .map(bookMapper::toDto)
                .toList();
    }
    // Метод для демонстрации проблемы N+1
    public List<BookDto> getAllBooksWithAuthorNPlusOne() {
        // 1 запрос: получаем все книги
        List<Book> books = bookRepository.findAll();

        // Здесь Hibernate сделает N дополнительных запросов,
        // когда мы начнём обращаться к authorEntity
        for (Book book : books) {
            // Принудительно вызываем загрузку автора
            if (book.getAuthorEntity() != null) {
                book.getAuthorEntity().getName(); // ТРИГГЕР: здесь будет отдельный запрос для каждой книги!
            }
        }

        return books.stream()
                .map(bookMapper::toDto)
                .toList();
    }
    public List<BookDto> getAllBooksWithAuthorAndCategoriesOptimized() {
        // Один запрос с JOIN, загружающий всё сразу
        List<Book> books = bookRepository.findAllWithAuthorAndCategories();

        return books.stream()
                .map(bookMapper::toDto)
                .toList();
    }
}