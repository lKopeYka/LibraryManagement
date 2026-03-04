package com.example.librarymanagement.mapper;

import com.example.librarymanagement.dto.AuthorDto;
import com.example.librarymanagement.entity.Author;
import org.springframework.stereotype.Component;

@Component
public class AuthorMapper {

    public AuthorDto toDto(Author author) {
        if (author == null) {
            return null;
        }

        AuthorDto dto = new AuthorDto();
        dto.setId(author.getId());
        dto.setName(author.getName());
        dto.setBirthDate(author.getBirthDate());
        dto.setBirthCountry(author.getBirthCountry());
        dto.setBiography(author.getBiography());
        return dto;
    }

    public Author toEntity(AuthorDto dto) {
        if (dto == null) {
            return null;
        }

        Author author = new Author();
        author.setId(dto.getId());
        author.setName(dto.getName());
        author.setBirthDate(dto.getBirthDate());
        author.setBirthCountry(dto.getBirthCountry());
        author.setBiography(dto.getBiography());
        return author;
    }
}