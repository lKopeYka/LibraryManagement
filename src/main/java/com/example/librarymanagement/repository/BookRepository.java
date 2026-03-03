package com.example.librarymanagement.repository;

import com.example.librarymanagement.model.Book;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class BookRepository {

    private final Map<Long, Book> books = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public List<Book> findAll() {
        return new ArrayList<>(books.values());
    }

    public Optional<Book> findById(Long id) {
        return Optional.ofNullable(books.get(id));
    }

    public void save(Book book) {
        if (book.getId() == null) {
            book.setId(idGenerator.getAndIncrement());
        }
        books.put(book.getId(), book);
    }

    // Инициализация тестовыми данными
    {
        Book book1 = new Book(null, "Война и мир", "Лев Толстой", 1869, "978-5-17-135127-6");
        Book book2 = new Book(null, "Преступление и наказание", "Федор Достоевский", 1866, "978-5-04-116618-6");
        Book book3 = new Book(null, "Анна Каренина", "Лев Толстой", 1877, "978-5-699-19112-0");

        save(book1);
        save(book2);
        save(book3);
    }
}