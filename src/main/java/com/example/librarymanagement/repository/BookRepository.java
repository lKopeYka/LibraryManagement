package com.example.librarymanagement.repository;

import com.example.librarymanagement.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByAuthor(String author);

    Page<Book> findByAuthor(String author, Pageable pageable);

    Page<Book> findByAuthorContaining(String author, Pageable pageable);

    List<Book> findByAuthorEntityId(Long authorId);

    List<Book> findByCategoriesId(Long categoryId);

    Page<Book> findByCategoriesId(Long categoryId, Pageable pageable);

    @EntityGraph(attributePaths = {"authorEntity", "categories"})
    @Override
    List<Book> findAll();

    @EntityGraph(attributePaths = {"authorEntity", "categories"})
    @Override
    Page<Book> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"authorEntity", "categories"})
    Optional<Book> findWithAuthorAndCategoriesById(Long id);

    @Query("SELECT b FROM Book b JOIN b.authorEntity a WHERE a.name = :authorName")
    List<Book> findBooksByAuthorNameJPQL(@Param("authorName") String authorName);

    @Query("SELECT b FROM Book b JOIN b.authorEntity a WHERE a.name = :authorName")
    Page<Book> findBooksByAuthorNameJPQL(@Param("authorName") String authorName, Pageable pageable);

    @Query("SELECT b FROM Book b JOIN b.authorEntity a WHERE a.name LIKE %:authorName%")
    List<Book> findBooksByAuthorNameContainingJPQL(@Param("authorName") String authorName);

    @Query("SELECT b FROM Book b JOIN b.authorEntity a WHERE a.name LIKE %:authorName%")
    Page<Book> findBooksByAuthorNameContainingJPQL(@Param("authorName") String authorName, Pageable pageable);

    @Query(value = "SELECT b.* FROM books b "
            + "JOIN authors a ON b.author_id = a.id "
            + "WHERE a.name = :authorName",
            nativeQuery = true)
    List<Book> findBooksByAuthorNameNative(@Param("authorName") String authorName);

    @Query(value = "SELECT b.* FROM books b "
            + "JOIN authors a ON b.author_id = a.id "
            + "WHERE a.name ILIKE %:authorName%",
            nativeQuery = true)
    List<Book> findBooksByAuthorNameContainingNative(@Param("authorName") String authorName);
}