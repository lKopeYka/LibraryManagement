package com.example.librarymanagement.controller;

import com.example.librarymanagement.dto.ReaderDto;
import com.example.librarymanagement.service.ReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

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
        if (reader == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(reader);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ReaderDto> getReaderByEmail(@PathVariable String email) {
        ReaderDto reader = readerService.getReaderByEmail(email);
        if (reader == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(reader);
    }

    @PostMapping
    public ResponseEntity<ReaderDto> createReader(@RequestBody ReaderDto readerDto) {
        ReaderDto createdReader = readerService.createReader(readerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReader);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReaderDto> updateReader(@PathVariable Long id, @RequestBody ReaderDto readerDto) {
        ReaderDto updatedReader = readerService.updateReader(id, readerDto);
        if (updatedReader == null) {
            return ResponseEntity.notFound().build();
        }
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