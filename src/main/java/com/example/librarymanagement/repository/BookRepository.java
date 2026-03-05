package com.example.librarymanagement.repository;

import com.example.librarymanagement.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByAuthor(String author);

    List<Book> findByAuthorEntityId(Long authorId);

    List<Book> findByCategoriesId(Long categoryId);

    @EntityGraph(attributePaths = {"authorEntity", "categories"})
    @Override
    List<Book> findAll();

    @EntityGraph(attributePaths = {"authorEntity", "categories"})
    Optional<Book> findWithAuthorAndCategoriesById(Long id);
}