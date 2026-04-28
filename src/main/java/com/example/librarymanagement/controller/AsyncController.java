package com.example.librarymanagement.controller;

import com.example.librarymanagement.dto.BookDto;
import com.example.librarymanagement.service.AsyncTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api/concurrency")
public class AsyncController {

    private final AsyncTaskService asyncTaskService;

    @Autowired
    public AsyncController(AsyncTaskService asyncTaskService) {
        this.asyncTaskService = asyncTaskService;
    }

    @PostMapping("/process-books")
    public ResponseEntity<String> processBooks(@RequestBody List<BookDto> books) {
        String taskId = UUID.randomUUID().toString();
        asyncTaskService.processBooksAsync(taskId, books);
        return ResponseEntity.accepted().body("Задача создана, taskId: " + taskId);
    }

    @GetMapping("/status/{taskId}")
    public ResponseEntity<String> getTaskStatus(@PathVariable String taskId) {
        String status = asyncTaskService.getTaskStatus(taskId);
        return ResponseEntity.ok("Статус задачи " + taskId + ": " + status);
    }

    @GetMapping("/counter/safe/increment")
    public ResponseEntity<String> incrementSafeCounter() {
        asyncTaskService.incrementThreadSafeCounter();
        return ResponseEntity.ok("Потокобезопасный счётчик: " + asyncTaskService.getThreadSafeCounter());
    }

    @GetMapping("/counter/safe")
    public ResponseEntity<Integer> getSafeCounter() {
        return ResponseEntity.ok(asyncTaskService.getThreadSafeCounter());
    }

    @GetMapping("/counter/unsafe/increment")
    public ResponseEntity<String> incrementUnsafeCounter() {
        asyncTaskService.incrementNonThreadSafeCounter();
        return ResponseEntity.ok("НЕпотокобезопасный счётчик: " + asyncTaskService.getNonThreadSafeCounter());
    }

    @GetMapping("/counter/unsafe")
    public ResponseEntity<Integer> getUnsafeCounter() {
        return ResponseEntity.ok(asyncTaskService.getNonThreadSafeCounter());
    }

    @GetMapping("/race-test")
    public ResponseEntity<String> raceConditionTest() {
        asyncTaskService.raceConditionTest();
        return ResponseEntity.ok("Тест завершён. Смотри логи.");
    }

    @GetMapping("/race-test-result")
    public ResponseEntity<String> raceConditionTestResult() {
        asyncTaskService.raceConditionTest();
        String result = "Safe counter: "
                + asyncTaskService.getThreadSafeCounter()
                + ", Unsafe counter: " + asyncTaskService.getNonThreadSafeCounter();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/race-demo")
    public ResponseEntity<Map<String, Object>> raceConditionDemo() {
        int threads = 60;
        int incrementsPerThread = 2000;
        int expected = threads * incrementsPerThread;

        int[] unsafeCounter = {0};
        SyncCounter syncCounter = new SyncCounter();
        AtomicInteger atomicCounter = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(threads);

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    unsafeCounter[0]++;
                    syncCounter.increment();
                    atomicCounter.incrementAndGet();
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("threads", threads);
        result.put("incrementsPerThread", incrementsPerThread);
        result.put("expected", expected);
        result.put("unsafeResult", unsafeCounter[0]);
        result.put("synchronizedResult", syncCounter.getValue());
        result.put("atomicResult", atomicCounter.get());

        return ResponseEntity.ok(result);
    }

    class SyncCounter {
        private int count = 0;

        public synchronized void increment() {
            count++;
        }

        public int getValue() {
            return count;
        }
    }
}