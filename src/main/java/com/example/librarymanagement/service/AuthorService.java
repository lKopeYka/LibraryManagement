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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorService {

    private static final Logger log = LoggerFactory.getLogger(AuthorService.class);

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final AuthorMapper authorMapper;
    private final BookMapper bookMapper;

    @Autowired
    public AuthorService(AuthorRepository authorRepository,
                         BookRepository bookRepository,
                         AuthorMapper authorMapper,
                         BookMapper bookMapper) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
        this.authorMapper = authorMapper;
        this.bookMapper = bookMapper;
    }

    public AuthorDto createAuthor(AuthorDto authorDto) {
        log.info("Создание нового автора: {}", authorDto.getName());
        Author author = authorMapper.toEntity(authorDto);
        Author savedAuthor = authorRepository.save(author);
        log.info("Автор создан с id: {}", savedAuthor.getId());
        return authorMapper.toDto(savedAuthor);
    }

    public List<AuthorDto> getAllAuthors() {
        log.debug("Получение всех авторов");
        return authorRepository.findAll().stream()
                .map(authorMapper::toDto)
                .collect(Collectors.toList());
    }

    public AuthorDto getAuthorById(Long id) {
        log.debug("Поиск автора по id: {}", id);
        return authorRepository.findById(id)
                .map(authorMapper::toDto)
                .orElseThrow(() -> {
                    log.warn("Автор не найден с id: {}", id);
                    return new ResourceNotFoundException("Автор не найден с id: " + id);
                });
    }

    public AuthorDto updateAuthor(Long id, AuthorDto authorDto) {
        log.info("Обновление автора с id: {}", id);
        return authorRepository.findById(id)
                .map(existingAuthor -> {
                    existingAuthor.setName(authorDto.getName());
                    existingAuthor.setBirthDate(authorDto.getBirthDate());
                    existingAuthor.setBirthCountry(authorDto.getBirthCountry());
                    existingAuthor.setBiography(authorDto.getBiography());
                    Author updatedAuthor = authorRepository.save(existingAuthor);
                    log.info("Автор с id: {} обновлён", id);
                    return authorMapper.toDto(updatedAuthor);
                })
                .orElseThrow(() -> {
                    log.warn("Автор не найден для обновления с id: {}", id);
                    return new ResourceNotFoundException("Автор не найден с id: " + id);
                });
    }

    public boolean deleteAuthor(Long id) {
        log.info("Удаление автора с id: {}", id);
        if (authorRepository.existsById(id)) {
            authorRepository.deleteById(id);
            log.info("Автор с id: {} удалён", id);
            return true;
        }
        log.warn("Автор не найден для удаления с id: {}", id);
        return false;
    }

    private void saveAuthorWithBooks(AuthorWithBooksDto dto) {
        Author author = authorMapper.toEntity(dto.getAuthor());
        Author savedAuthor = authorRepository.save(author);
        log.info("Автор сохранен: {}", savedAuthor.getId());

        for (BookDto bookDto : dto.getBooks()) {
            bookDto.setAuthorId(savedAuthor.getId());
            Book book = bookMapper.toEntity(bookDto);
            bookRepository.save(book);
            log.info("Книга сохранена: {}", book.getTitle());

            if (book.getTitle().contains("Ошибка")) {
                log.error("Ошибка при сохранении книги: {}", book.getTitle());
                throw new RuntimeException("Ошибка при сохранении книги!");
            }
        }
        log.info("Все книги сохранены успешно");
    }

    public void saveAuthorWithBooksWithoutTransaction(AuthorWithBooksDto dto) {
        log.info("Сохранение автора с книгами (без транзакции)");
        saveAuthorWithBooks(dto);
    }

    @Transactional
    public void saveAuthorWithBooksWithTransaction(AuthorWithBooksDto dto) {
        log.info("Сохранение автора с книгами (с транзакцией)");
        saveAuthorWithBooks(dto);
        log.info("Транзакция зафиксирована");
    }
}