package com.example.librarymanagement.service;

import com.example.librarymanagement.dto.ReaderDto;
import com.example.librarymanagement.entity.Reader;
import com.example.librarymanagement.exception.ResourceNotFoundException;
import com.example.librarymanagement.mapper.ReaderMapper;
import com.example.librarymanagement.repository.ReaderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@Disabled
@ExtendWith(MockitoExtension.class)
class ReaderServiceTest {

    @Mock
    private ReaderRepository readerRepository;

    @Mock
    private ReaderMapper readerMapper;

    @InjectMocks
    private ReaderService readerService;

    private ReaderDto readerDto;
    private Reader reader;

    @BeforeEach
    void setUp() {
        readerDto = new ReaderDto();
        readerDto.setId(1L);
        readerDto.setName("Иван Петров");
        readerDto.setEmail("ivan@mail.com");

        reader = new Reader();
        reader.setId(1L);
        reader.setName("Иван Петров");
        reader.setEmail("ivan@mail.com");
    }

    @Test
    void getAllReaders_Success() {
        when(readerRepository.findAll()).thenReturn(List.of(reader));
        when(readerMapper.toDto(reader)).thenReturn(readerDto);

        List<ReaderDto> result = readerService.getAllReaders();

        assertEquals(1, result.size());
    }

    @Test
    void getReaderById_Success() {
        when(readerRepository.findById(1L)).thenReturn(Optional.of(reader));
        when(readerMapper.toDto(reader)).thenReturn(readerDto);

        ReaderDto result = readerService.getReaderById(1L);

        assertNotNull(result);
    }

    @Test
    void getReaderById_NotFound() {
        when(readerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            readerService.getReaderById(999L);
        });
    }

    @Test
    void getReaderByEmail_Success() {
        when(readerRepository.findByEmail("ivan@mail.com")).thenReturn(Optional.of(reader));
        when(readerMapper.toDto(reader)).thenReturn(readerDto);

        ReaderDto result = readerService.getReaderByEmail("ivan@mail.com");

        assertNotNull(result);
    }

    @Test
    void getReaderByEmail_NotFound() {
        when(readerRepository.findByEmail("notfound@mail.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            readerService.getReaderByEmail("notfound@mail.com");
        });
    }

    @Test
    void createReader_Success() {
        when(readerMapper.toEntity(readerDto)).thenReturn(reader);
        when(readerRepository.save(any(Reader.class))).thenReturn(reader);
        when(readerMapper.toDto(reader)).thenReturn(readerDto);

        ReaderDto result = readerService.createReader(readerDto);

        assertNotNull(result);
    }

    @Test
    void updateReader_Success() {
        when(readerRepository.findById(1L)).thenReturn(Optional.of(reader));
        when(readerRepository.save(any(Reader.class))).thenReturn(reader);
        when(readerMapper.toDto(reader)).thenReturn(readerDto);

        ReaderDto result = readerService.updateReader(1L, readerDto);

        assertNotNull(result);
    }

    @Test
    void updateReader_NotFound() {
        when(readerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            readerService.updateReader(999L, readerDto);
        });
    }

    @Test
    void deleteReader_Success() {
        when(readerRepository.existsById(1L)).thenReturn(true);

        boolean result = readerService.deleteReader(1L);

        assertTrue(result);
        verify(readerRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteReader_NotFound() {
        when(readerRepository.existsById(999L)).thenReturn(false);

        boolean result = readerService.deleteReader(999L);

        assertFalse(result);
        verify(readerRepository, never()).deleteById(anyLong());
    }
}