package com.example.librarymanagement.service;

import com.example.librarymanagement.cache.BookCacheService;
import com.example.librarymanagement.cache.BookSearchKey;
import com.example.librarymanagement.dto.BookDto;
import com.example.librarymanagement.entity.Book;
import com.example.librarymanagement.entity.Author;
import com.example.librarymanagement.entity.Category;
import com.example.librarymanagement.mapper.BookMapper;
import com.example.librarymanagement.repository.BookRepository;
import com.example.librarymanagement.repository.AuthorRepository;
import com.example.librarymanagement.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final BookCacheService cacheService;

    @Autowired
    public BookService(BookRepository bookRepository,
                       AuthorRepository authorRepository,
                       CategoryRepository categoryRepository,
                       BookMapper bookMapper,
                       BookCacheService cacheService) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.categoryRepository = categoryRepository;
        this.bookMapper = bookMapper;
        this.cacheService = cacheService;
    }

    public BookDto createBook(BookDto bookDto) {
        Book book = bookMapper.toEntity(bookDto);
        Book savedBook = bookRepository.save(book);
        return bookMapper.toDto(savedBook);
    }

    public List<BookDto> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    public BookDto getBookById(Long id) {
        return bookRepository.findById(id)
                .map(bookMapper::toDto)
                .orElse(null);
    }

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

    public boolean deleteBook(Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<BookDto> getBooksByAuthor(String author) {
        return bookRepository.findByAuthor(author).stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<BookDto> getBooksByAuthorId(Long authorId) {
        return bookRepository.findByAuthorEntityId(authorId).stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<BookDto> getBooksByCategoryId(Long categoryId) {
        return bookRepository.findByCategoriesId(categoryId).stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<BookDto> getBooksByAuthorNameJPQL(String authorName) {
        return bookRepository.findBooksByAuthorNameJPQL(authorName).stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<BookDto> getBooksByAuthorNameContainingJPQL(String authorName) {
        return bookRepository.findBooksByAuthorNameContainingJPQL(authorName).stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<BookDto> getBooksByAuthorNameNative(String authorName) {
        return bookRepository.findBooksByAuthorNameNative(authorName).stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<BookDto> getBooksByAuthorNameContainingNative(String authorName) {
        return bookRepository.findBooksByAuthorNameContainingNative(authorName).stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    public Page<BookDto> getBooksWithPagination(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return bookRepository.findAll(pageable)
                .map(bookMapper::toDto);
    }

    public Page<BookDto> getBooksWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findAll(pageable)
                .map(bookMapper::toDto);
    }

    public Page<BookDto> getBooksByAuthorNameWithPagination(String authorName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> bookPage = bookRepository.findBooksByAuthorNameJPQL(authorName, pageable);
        return bookPage.map(bookMapper::toDto);
    }

    public Page<BookDto> getBooksByAuthorNameContainingWithPagination(String authorName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> bookPage = bookRepository.findBooksByAuthorNameContainingJPQL(authorName, pageable);
        return bookPage.map(bookMapper::toDto);
    }

    public Page<BookDto> getBooksByAuthorNameWithPaginationAndCache(String authorName, int page, int size) {
        BookSearchKey key = new BookSearchKey(authorName, page, size, null, null);

        Page<BookDto> cachedResult = cacheService.get(key);
        if (cachedResult != null) {
            return cachedResult;
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Book> bookPage = bookRepository.findBooksByAuthorNameJPQL(authorName, pageable);
        Page<BookDto> result = bookPage.map(bookMapper::toDto);

        cacheService.put(key, result);
        return result;
    }

    public BookDto createBookWithCacheInvalidation(BookDto bookDto) {
        BookDto createdBook = createBook(bookDto);

        if (createdBook != null && bookDto.getAuthorId() != null) {
            authorRepository.findById(bookDto.getAuthorId())
                    .ifPresent(author -> cacheService.removeByAuthor(author.getName()));
        }

        return createdBook;
    }

    public BookDto updateBookWithCacheInvalidation(Long id, BookDto bookDto) {
        BookDto updatedBook = updateBook(id, bookDto);

        if (updatedBook != null && bookDto.getAuthorId() != null) {
            authorRepository.findById(bookDto.getAuthorId())
                    .ifPresent(author -> cacheService.removeByAuthor(author.getName()));
        }

        return updatedBook;
    }

    public boolean deleteBookWithCacheInvalidation(Long id) {
        return bookRepository.findById(id)
                .map(book -> {
                    String authorName = book.getAuthorEntity() != null ? book.getAuthorEntity().getName() : null;
                    boolean deleted = deleteBook(id);
                    if (deleted && authorName != null) {
                        cacheService.removeByAuthor(authorName);
                    }
                    return deleted;
                })
                .orElse(false);
    }
}