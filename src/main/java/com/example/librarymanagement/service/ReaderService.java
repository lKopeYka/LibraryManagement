package com.example.librarymanagement.service;

import com.example.librarymanagement.dto.ReaderDto;
import com.example.librarymanagement.entity.Reader;
import com.example.librarymanagement.mapper.ReaderMapper;
import com.example.librarymanagement.repository.ReaderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReaderService {

    private final ReaderRepository readerRepository;
    private final ReaderMapper readerMapper;

    @Autowired
    public ReaderService(ReaderRepository readerRepository, ReaderMapper readerMapper) {
        this.readerRepository = readerRepository;
        this.readerMapper = readerMapper;
    }

    public List<ReaderDto> getAllReaders() {
        return readerRepository.findAll().stream()
                .map(readerMapper::toDto)
                .collect(Collectors.toList());
    }

    public ReaderDto getReaderById(Long id) {
        return readerRepository.findById(id)
                .map(readerMapper::toDto)
                .orElse(null);
    }

    public ReaderDto getReaderByEmail(String email) {
        return readerRepository.findByEmail(email)
                .map(readerMapper::toDto)
                .orElse(null);
    }

    public ReaderDto createReader(ReaderDto readerDto) {
        Reader reader = readerMapper.toEntity(readerDto);
        Reader savedReader = readerRepository.save(reader);
        return readerMapper.toDto(savedReader);
    }

    public ReaderDto updateReader(Long id, ReaderDto readerDto) {
        return readerRepository.findById(id)
                .map(existingReader -> {
                    existingReader.setName(readerDto.getName());
                    existingReader.setEmail(readerDto.getEmail());
                    existingReader.setPhone(readerDto.getPhone());
                    existingReader.setAddress(readerDto.getAddress());
                    Reader updatedReader = readerRepository.save(existingReader);
                    return readerMapper.toDto(updatedReader);
                })
                .orElse(null);
    }

    public boolean deleteReader(Long id) {
        if (readerRepository.existsById(id)) {
            readerRepository.deleteById(id);
            return true;
        }
        return false;
    }
}