package com.example.librarymanagement.repository;

import com.example.librarymanagement.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // Поиск по автору (строковое поле)
    List<Book> findByAuthor(String author);

    // Поиск по ID автора
    List<Book> findByAuthorEntityId(Long authorId);

    // Поиск по ID категории
    List<Book> findByCategoriesId(Long categoryId);

    // Переопределяем findAll() с @EntityGraph для загрузки автора и категорий одним запросом
    @EntityGraph(attributePaths = {"authorEntity", "categories"})
    @Override
    List<Book> findAll();

    // Поиск книги по ID с загрузкой автора и категорий
    @EntityGraph(attributePaths = {"authorEntity", "categories"})
    Optional<Book> findWithAuthorAndCategoriesById(Long id);
}