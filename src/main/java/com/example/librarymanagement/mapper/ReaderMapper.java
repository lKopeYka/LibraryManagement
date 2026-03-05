package com.example.librarymanagement.mapper;

import com.example.librarymanagement.dto.ReaderDto;
import com.example.librarymanagement.entity.Reader;
import org.springframework.stereotype.Component;

@Component
public class ReaderMapper {

    public ReaderDto toDto(Reader reader) {
        if (reader == null) {
            return null;
        }

        ReaderDto dto = new ReaderDto();
        dto.setId(reader.getId());
        dto.setName(reader.getName());
        dto.setEmail(reader.getEmail());
        dto.setRegistrationDate(reader.getRegistrationDate());
        dto.setPhone(reader.getPhone());
        dto.setAddress(reader.getAddress());
        return dto;
    }

    public Reader toEntity(ReaderDto dto) {
        if (dto == null) {
            return null;
        }

        Reader reader = new Reader();
        reader.setId(dto.getId());
        reader.setName(dto.getName());
        reader.setEmail(dto.getEmail());
        reader.setRegistrationDate(dto.getRegistrationDate());
        reader.setPhone(dto.getPhone());
        reader.setAddress(dto.getAddress());
        return reader;
    }
}