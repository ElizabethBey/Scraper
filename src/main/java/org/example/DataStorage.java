package org.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataStorage {
    private String fileName;
    private ConcurrentLinkedQueue<String> queue;
    private ExecutorService writerExecutor;
    private boolean isRunning = true;

    public DataStorage(String fileName) {
        this.fileName = fileName;
        this.queue = new ConcurrentLinkedQueue<>();
        this.writerExecutor = Executors.newSingleThreadExecutor();
        startWriterThread();
    }

    public void saveData(String message) {
        queue.add(message);
    }

    private void startWriterThread() {
        writerExecutor.submit(() -> {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
                while (isRunning) {
                    String data = queue.poll();
                    if (data != null) {
                        writer.write(data + "\n");
                    } else {
                        Thread.sleep(100);
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public void stopWriter(){
        isRunning = false;
        writerExecutor.shutdown();
        try {
            writerExecutor.awaitTermination(Long.MAX_VALUE, java.util.concurrent.TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
