package org.example;

import java.io.FileWriter;
import java.io.IOException;

public class ErrorLogger {

    private static final String ERROR_LOG_FILE = "./errorLogger.txt";

    public static void logError(String url, String message) {
        String logMessage = String.format("Thread: %s URL: %s /---/ Error: %s%n",
                Thread.currentThread().getName(), url, message);
        try (FileWriter writer = new FileWriter(ERROR_LOG_FILE, true)) {
            writer.write(logMessage);
        } catch (IOException e) {
            System.err.println("Ошибка при записи в лог файл: " + e.getMessage());
        }
    }
}
