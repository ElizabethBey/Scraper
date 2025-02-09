package org.example;

import java.io.IOException;
import java.util.Random;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class HttpRequestManager {
    private static final int MAX_RETRIES = 3;
    private static final int DELAY = 2000;
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
    private static Random random = new Random();

    public Document sendRequest(String url) throws IOException {
        int retryCount = 0;
        while (retryCount < MAX_RETRIES) {
            try {
                Connection.Response response = Jsoup.connect(url)
                        .userAgent(USER_AGENT)
                        .method(Connection.Method.GET)
                        .timeout(5000)
                        .execute();

                if (response.contentType().contains("html")) {
                    return response.parse();
                } else {
                    String errorDescription = "Получен ответ не HTML: " + response.contentType();
                    ErrorLogger.logError(url, errorDescription);
                    throw new IOException(errorDescription);
                }
            } catch (IOException e) {
                System.err.println("Ошибка при обработке URL (попытка " + (retryCount + 1) + "): " + url);
                retryCount++;
                try {
                    Thread.sleep(DELAY + random.nextInt(1000));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Прервано во время ожидания между попытками", ie);
                }
            }
        }
        String errorMessage = "Не удалось получить данные после " + MAX_RETRIES + " попыток для URL: " + url;
        ErrorLogger.logError(url, errorMessage);
        throw new IOException(errorMessage);
    }
}
