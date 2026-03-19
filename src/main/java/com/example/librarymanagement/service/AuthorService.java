package com.example.librarymanagement.service;

import com.example.librarymanagement.dto.AuthorDto;
import com.example.librarymanagement.dto.AuthorWithBooksDto;
import com.example.librarymanagement.dto.BookDto;
import com.example.librarymanagement.entity.Author;
import com.example.librarymanagement.entity.Book;
import com.example.librarymanagement.mapper.AuthorMapper;
import com.example.librarymanagement.mapper.BookMapper;
import com.example.librarymanagement.repository.AuthorRepository;
import com.example.librarymanagement.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


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
        Author author = authorMapper.toEntity(authorDto);
        Author savedAuthor = authorRepository.save(author);
        return authorMapper.toDto(savedAuthor);
    }

    public List<AuthorDto> getAllAuthors() {
        return authorRepository.findAll().stream()
                .map(authorMapper::toDto)
                .toList();
    }

    public AuthorDto getAuthorById(Long id) {
        return authorRepository.findById(id)
                .map(authorMapper::toDto)
                .orElse(null);
    }

    public AuthorDto updateAuthor(Long id, AuthorDto authorDto) {
        return authorRepository.findById(id)
                .map(existingAuthor -> {
                    existingAuthor.setName(authorDto.getName());
                    existingAuthor.setBirthDate(authorDto.getBirthDate());
                    existingAuthor.setBirthCountry(authorDto.getBirthCountry());
                    existingAuthor.setBiography(authorDto.getBiography());
                    Author updatedAuthor = authorRepository.save(existingAuthor);
                    return authorMapper.toDto(updatedAuthor);
                })
                .orElse(null);
    }

    public boolean deleteAuthor(Long id) {
        return authorRepository.findById(id)
                .map(author -> {
                    List<Book> books = bookRepository.findByAuthorEntityId(id);
                    for (Book book : books) {
                        book.setAuthorEntity(null);
                        bookRepository.save(book);
                    }
                    authorRepository.delete(author);
                    return true;
                })
                .orElse(false);
    }

    public void saveAuthorWithBooksWithoutTransaction(AuthorWithBooksDto dto) {
        Author author = authorMapper.toEntity(dto.getAuthor());
        Author savedAuthor = authorRepository.save(author);

        for (BookDto bookDto : dto.getBooks()) {
            bookDto.setAuthorId(savedAuthor.getId());
            Book book = bookMapper.toEntity(bookDto);
            bookRepository.save(book);

            if (book.getTitle().contains("Ошибка")) {
                throw new RuntimeException("Ошибка при сохранении книги!");
            }
        }
    }

    @Transactional
    public void saveAuthorWithBooksWithTransaction(AuthorWithBooksDto dto) {
        Author author = authorMapper.toEntity(dto.getAuthor());
        Author savedAuthor = authorRepository.save(author);

        for (BookDto bookDto : dto.getBooks()) {
            bookDto.setAuthorId(savedAuthor.getId());
            Book book = bookMapper.toEntity(bookDto);
            bookRepository.save(book);

            if (book.getTitle().contains("Ошибка")) {
                throw new RuntimeException("Ошибка при сохранении книги!");
            }
        }
    }
}