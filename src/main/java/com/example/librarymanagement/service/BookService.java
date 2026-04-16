package com.example.librarymanagement.service;

import com.example.librarymanagement.dto.BookDto;
import com.example.librarymanagement.entity.Book;
import com.example.librarymanagement.entity.Category;
import com.example.librarymanagement.exception.BookSaveException;
import com.example.librarymanagement.exception.ResourceNotFoundException;
import com.example.librarymanagement.mapper.BookMapper;
import com.example.librarymanagement.repository.AuthorRepository;
import com.example.librarymanagement.repository.BookRepository;
import com.example.librarymanagement.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private static final Logger log = LoggerFactory.getLogger(BookService.class);

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

    public BookDto createBook(BookDto bookDto) {
        Book book = bookMapper.toEntity(bookDto);
        Book savedBook = bookRepository.save(book);
        return bookMapper.toDto(savedBook);
    }

    public List<BookDto> createBooksBulk(List<BookDto> bookDtos) {
        return bookDtos.stream()
                .map(this::createBook)
                .toList();
    }

    public List<BookDto> createBooksBulkWithoutTransaction(List<BookDto> bookDtos) {
        log.info("Массовое создание книг (БЕЗ транзакции). Получено книг: {}", bookDtos.size());

        return bookDtos.stream()
                .map(bookDto -> {
                    Book book = bookMapper.toEntity(bookDto);

                    Optional.ofNullable(bookDto.getAuthorId())
                            .flatMap(authorRepository::findById)
                            .ifPresent(book::setAuthorEntity);

                    Optional.ofNullable(bookDto.getCategoryIds())
                            .ifPresent(categoryIds -> {
                                List<Category> categories = categoryRepository.findAllById(categoryIds);
                                book.setCategories(categories);
                            });

                    if (book.getTitle() == null || book.getTitle().isBlank()) {
                        log.error("Название книги не может быть пустым");
                        throw new BookSaveException("Название книги не может быть пустым");
                    }

                    Book savedBook = bookRepository.save(book);
                    log.info("Книга сохранена: {}", savedBook.getTitle());

                    return bookMapper.toDto(savedBook);
                })
                .toList();
    }

    @Transactional
    public List<BookDto> createBooksBulkWithTransaction(List<BookDto> bookDtos) {
        log.info("Массовое создание книг (С транзакцией). Получено книг: {}", bookDtos.size());

        List<BookDto> result = bookDtos.stream()
                .map(bookDto -> {
                    Book book = bookMapper.toEntity(bookDto);

                    Optional.ofNullable(bookDto.getAuthorId())
                            .flatMap(authorRepository::findById)
                            .ifPresent(book::setAuthorEntity);

                    Optional.ofNullable(bookDto.getCategoryIds())
                            .ifPresent(categoryIds -> {
                                List<Category> categories = categoryRepository.findAllById(categoryIds);
                                book.setCategories(categories);
                            });

                    Book savedBook = bookRepository.save(book);
                    log.info("Книга сохранена: {}", savedBook.getTitle());

                    if (book.getTitle() == null || book.getTitle().isBlank()) {
                        log.error("Название книги не может быть пустым");
                        throw new BookSaveException("Название книги не может быть пустым");
                    }

                    return bookMapper.toDto(savedBook);
                })
                .toList();

        log.info("Все книги сохранены успешно, транзакция зафиксирована");
        return result;
    }

    public List<BookDto> getAllBooks() {
        return bookRepository.findAllWithDetails().stream()
                .sorted(Comparator.comparing(Book::getId))
                .map(bookMapper::toDto)
                .toList();
    }

    public BookDto getBookById(Long id) {
        log.debug("Поиск книги по id: {}", id);
        return bookRepository.findWithAuthorAndCategoriesById(id)
                .map(bookMapper::toDto)
                .orElseThrow(() -> {
                    log.warn("Книга не найдена с id: {}", id);
                    return new ResourceNotFoundException("Книга не найдена с id: " + id);
                });
    }

    public BookDto updateBook(Long id, BookDto bookDto) {
        log.info("Обновление книги с id: {}", id);
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
                    log.info("Книга с id: {} обновлена", id);
                    return bookMapper.toDto(updatedBook);
                })
                .orElseThrow(() -> {
                    log.warn("Книга не найдена для обновления с id: {}", id);
                    return new ResourceNotFoundException("Книга не найдена с id: " + id);
                });
    }

    public boolean deleteBook(Long id) {
        log.info("Удаление книги с id: {}", id);
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            log.info("Книга с id: {} удалена", id);
            return true;
        }
        log.warn("Книга не найдена для удаления с id: {}", id);
        return false;
    }

    public List<BookDto> getBooksByAuthor(String author) {
        log.debug("Поиск книг по автору: {}", author);
        return bookRepository.findByAuthor(author).stream()
                .map(bookMapper::toDto)
                .toList();
    }

    public List<BookDto> getBooksByAuthorId(Long authorId) {
        log.debug("Поиск книг по id автора: {}", authorId);
        return bookRepository.findByAuthorEntityId(authorId).stream()
                .map(bookMapper::toDto)
                .toList();
    }

    public List<BookDto> getBooksByCategoryId(Long categoryId) {
        log.debug("Поиск книг по id категории: {}", categoryId);
        return bookRepository.findByCategoriesId(categoryId).stream()
                .map(bookMapper::toDto)
                .toList();
    }
}