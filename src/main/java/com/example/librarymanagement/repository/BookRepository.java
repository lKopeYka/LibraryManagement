package com.example.librarymanagement.repository;

import com.example.librarymanagement.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByAuthor(String author);
    List<Book> findByAuthorEntityId(Long authorId);
    List<Book> findByCategoriesId(Long categoryId);
    @EntityGraph(attributePaths = {"authorEntity", "categories"})
    @Query("SELECT b FROM Book b")
    List<Book> findAllWithAuthorAndCategories();

    // Решение 2: для конкретной книги
    @EntityGraph(attributePaths = {"authorEntity", "categories"})
    Optional<Book> findWithAuthorAndCategoriesById(Long id);
}
