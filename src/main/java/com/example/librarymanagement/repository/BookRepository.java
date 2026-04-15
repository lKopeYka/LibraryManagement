package com.example.librarymanagement.repository;

import com.example.librarymanagement.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByAuthor(String author);
    List<Book> findByAuthorEntityId(Long authorId);
    List<Book> findByCategoriesId(Long categoryId);

    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.authorEntity a LEFT JOIN FETCH b.categories c")
    List<Book> findAllWithDetails();

    @EntityGraph(attributePaths = {"authorEntity", "categories"})
    @Query("SELECT b FROM Book b")
    List<Book> findAllWithDetailsViaEntityGraph();

    @EntityGraph(attributePaths = {"authorEntity", "categories"})
    Optional<Book> findWithAuthorAndCategoriesById(Long id);

    @EntityGraph(attributePaths = {"categories"})
    @Query("SELECT b FROM Book b "
            + "LEFT JOIN b.authorEntity a "
            + "WHERE (:author IS NULL OR a.name = :author) "
            + "AND (:title IS NULL OR b.title = :title) "
            + "AND (:fromYear IS NULL OR b.publicationYear >= :fromYear) "
            + "AND (:toYear IS NULL OR b.publicationYear <= :toYear)")
    Page<Book> searchBooksWithPagination(
            @Param("author") String author,
            @Param("title") String title,
            @Param("fromYear") Integer fromYear,
            @Param("toYear") Integer toYear,
            @Param("categoryId") Long categoryId,
            Pageable pageable);

    @Query(value = "SELECT DISTINCT b.* FROM books b "
            + "LEFT JOIN authors a ON b.author_id = a.id "
            + "LEFT JOIN book_category bc ON b.id = bc.book_id "
            + "LEFT JOIN categories c ON bc.category_id = c.id "
            + "WHERE (CAST(:author AS VARCHAR) IS NULL OR a.name = CAST(:author AS VARCHAR)) "
            + "AND (CAST(:title AS VARCHAR) IS NULL OR b.title = CAST(:title AS VARCHAR)) "
            + "AND (CAST(:fromYear AS INTEGER) IS NULL OR b.publication_year >= CAST(:fromYear AS INTEGER)) "
            + "AND (CAST(:toYear AS INTEGER) IS NULL OR b.publication_year <= CAST(:toYear AS INTEGER)) "
            + "AND (CAST(:categoryId AS BIGINT) IS NULL OR c.id = CAST(:categoryId AS BIGINT))",
            countQuery = "SELECT COUNT(DISTINCT b.id) FROM books b "
                    + "LEFT JOIN authors a ON b.author_id = a.id "
                    + "LEFT JOIN book_category bc ON b.id = bc.book_id "
                    + "LEFT JOIN categories c ON bc.category_id = c.id "
                    + "WHERE (CAST(:author AS VARCHAR) IS NULL OR a.name = CAST(:author AS VARCHAR)) "
                    + "AND (CAST(:title AS VARCHAR) IS NULL OR b.title = CAST(:title AS VARCHAR)) "
                    + "AND (CAST(:fromYear AS INTEGER) IS NULL OR b.publication_year >= CAST(:fromYear AS INTEGER)) "
                    + "AND (CAST(:toYear AS INTEGER) IS NULL OR b.publication_year <= CAST(:toYear AS INTEGER)) "
                    + "AND (CAST(:categoryId AS BIGINT) IS NULL OR c.id = CAST(:categoryId AS BIGINT))",
            nativeQuery = true)
    Page<Book> searchBooksNative(
            @Param("author") String author,
            @Param("title") String title,
            @Param("fromYear") Integer fromYear,
            @Param("toYear") Integer toYear,
            @Param("categoryId") Long categoryId,
            Pageable pageable);
}