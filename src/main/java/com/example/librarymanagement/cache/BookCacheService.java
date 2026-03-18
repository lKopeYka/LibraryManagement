package com.example.librarymanagement.cache;

import com.example.librarymanagement.dto.BookDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class BookCacheService {

    private final Map<BookSearchKey, Page<BookDto>> cache = new HashMap<>();

    public void put(BookSearchKey key, Page<BookDto> page) {
        cache.put(key, page);
        System.out.println("Данные добавлены в кэш. Размер кэша: " + cache.size());
    }

    public Page<BookDto> get(BookSearchKey key) {
        Page<BookDto> result = cache.get(key);
        if (result != null) {
            System.out.println("Данные получены из кэша");
        } else {
            System.out.println("Данных в кэше нет");
        }
        return result;
    }

    public void clear() {
        cache.clear();
        System.out.println("Кэш очищен");
    }

    public void removeByAuthor(String authorName) {
        cache.entrySet().removeIf(entry -> entry.getKey().getAuthorName() != null
                && entry.getKey().getAuthorName().equals(authorName));
        System.out.println("Удалены записи кэша для автора: " + authorName);
    }

    public int size() {
        return cache.size();
    }
}