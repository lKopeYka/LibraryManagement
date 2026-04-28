package com.example.librarymanagement.service;

import com.example.librarymanagement.dto.BookDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class AsyncTaskService {

    private static final Logger log = LoggerFactory.getLogger(AsyncTaskService.class);

    private final ConcurrentMap<String, String> taskStatus = new ConcurrentHashMap<>();
    private final AtomicInteger threadSafeCounter = new AtomicInteger(0);
    private int nonThreadSafeCounter = 0;

    @Async
    public CompletableFuture<String> processBooksAsync(String taskId, List<BookDto> books) {
        log.info("Начало асинхронной обработки {} книг, taskId: {}", books.size(), taskId);
        taskStatus.put(taskId, "IN_PROGRESS");

        try {
            Thread.sleep(3000);
            log.info("Асинхронная обработка завершена, taskId: {}", taskId);
            taskStatus.put(taskId, "COMPLETED");
            return CompletableFuture.completedFuture("Обработка завершена");
        } catch (InterruptedException e) {
            log.error("Ошибка при асинхронной обработке, taskId: {}", taskId, e);
            taskStatus.put(taskId, "FAILED");
            return CompletableFuture.completedFuture("Ошибка: " + e.getMessage());
        }
    }

    public String getTaskStatus(String taskId) {
        return taskStatus.getOrDefault(taskId, "NOT_FOUND");
    }

    public int getThreadSafeCounter() {
        return threadSafeCounter.get();
    }

    public void incrementThreadSafeCounter() {
        threadSafeCounter.incrementAndGet();
    }

    public int getNonThreadSafeCounter() {
        return nonThreadSafeCounter;
    }

    public void incrementNonThreadSafeCounter() {
        nonThreadSafeCounter++;
    }

    public void raceConditionTest() {
        ExecutorService executor = Executors.newFixedThreadPool(50);

        threadSafeCounter.set(0);
        nonThreadSafeCounter = 0;

        for (int i = 0; i < 1000; i++) {
            executor.submit(() -> {
                incrementThreadSafeCounter();
                incrementNonThreadSafeCounter();
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        log.info("Race condition test result - Safe counter: {}, Unsafe counter: {}",
                threadSafeCounter.get(), nonThreadSafeCounter);
    }
}