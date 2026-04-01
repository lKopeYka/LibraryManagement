package com.example.librarymanagement.controller;

import com.example.librarymanagement.dto.ReaderDto;
import com.example.librarymanagement.service.ReaderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/readers")
public class ReaderController {

    private final ReaderService readerService;

    @Autowired
    public ReaderController(ReaderService readerService) {
        this.readerService = readerService;
    }

    @GetMapping
    public ResponseEntity<List<ReaderDto>> getAllReaders() {
        return ResponseEntity.ok(readerService.getAllReaders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReaderDto> getReaderById(@PathVariable Long id) {
        ReaderDto reader = readerService.getReaderById(id);
        return ResponseEntity.ok(reader);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ReaderDto> getReaderByEmail(@PathVariable String email) {
        ReaderDto reader = readerService.getReaderByEmail(email);
        return ResponseEntity.ok(reader);
    }

    @PostMapping
    public ResponseEntity<ReaderDto> createReader(@Valid @RequestBody ReaderDto readerDto) {
        ReaderDto createdReader = readerService.createReader(readerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReader);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReaderDto> updateReader(@PathVariable Long id, @Valid @RequestBody ReaderDto readerDto) {
        ReaderDto updatedReader = readerService.updateReader(id, readerDto);
        return ResponseEntity.ok(updatedReader);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReader(@PathVariable Long id) {
        boolean deleted = readerService.deleteReader(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}