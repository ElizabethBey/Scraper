package org.example;

import java.io.FileWriter;
import java.io.IOException;

public class ErrorLogger {

    private static final String ERROR_LOG_FILE = "error_urls.txt";

    public static void logError(String url, String message) {
        try (FileWriter writer = new FileWriter(ERROR_LOG_FILE, true)) {
            writer.write("URL: " + url + " /---/ Error: " + message + "\n");
        } catch (IOException e) {
            System.err.println("Ошибка при записи в лог файл: " + e.getMessage());
        }
    }
}
