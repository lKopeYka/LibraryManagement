package com.example.librarymanagement.service;

import com.example.librarymanagement.dto.BookDto;
import com.example.librarymanagement.entity.Book;
import com.example.librarymanagement.entity.Author;
import com.example.librarymanagement.entity.Category;
import com.example.librarymanagement.mapper.BookMapper;
import com.example.librarymanagement.repository.BookRepository;
import com.example.librarymanagement.repository.AuthorRepository;
import com.example.librarymanagement.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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

    // CREATE - создать книгу
    public BookDto createBook(BookDto bookDto) {
        Book book = bookMapper.toEntity(bookDto);
        Book savedBook = bookRepository.save(book);
        return bookMapper.toDto(savedBook);
    }

    // READ ALL - получить все книги (теперь с @EntityGraph!)
    public List<BookDto> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    // READ BY ID - получить книгу по ID
    public BookDto getBookById(Long id) {
        return bookRepository.findById(id)
                .map(bookMapper::toDto)
                .orElse(null);
    }

    // UPDATE - обновить книгу
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

    // DELETE - удалить книгу
    public boolean deleteBook(Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // ПОИСК ПО АВТОРУ (строка)
    public List<BookDto> getBooksByAuthor(String author) {
        return bookRepository.findByAuthor(author).stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    // ПОИСК ПО ID АВТОРА
    public List<BookDto> getBooksByAuthorId(Long authorId) {
        return bookRepository.findByAuthorEntityId(authorId).stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    // ПОИСК ПО КАТЕГОРИИ
    public List<BookDto> getBooksByCategoryId(Long categoryId) {
        return bookRepository.findByCategoriesId(categoryId).stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }
}