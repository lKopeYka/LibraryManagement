package com.example.librarymanagement.cache;

import java.util.Objects;

public class BookSearchKey {
    private final String authorName;
    private final Integer page;
    private final Integer size;
    private final String sortBy;
    private final String direction;

    public BookSearchKey(String authorName, Integer page, Integer size, String sortBy, String direction) {
        this.authorName = authorName;
        this.page = page;
        this.size = size;
        this.sortBy = sortBy;
        this.direction = direction;
    }

    public String getAuthorName() {
        return authorName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookSearchKey that = (BookSearchKey) o;
        return Objects.equals(authorName, that.authorName)
                && Objects.equals(page, that.page)
                && Objects.equals(size, that.size)
                && Objects.equals(sortBy, that.sortBy)
                && Objects.equals(direction, that.direction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authorName, page, size, sortBy, direction);
    }
}