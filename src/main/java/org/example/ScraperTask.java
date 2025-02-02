package org.example;

import java.io.IOException;

public class ScraperTask implements Runnable {
    private String url;
    private HttpRequestManager requestManager;
    private PostPageParser postPageParser;
    private DataStorage dataStorage;
    private NewsPageParser newsPageParser;
    private String typeOfTask;
    private ScrapingStrategy strategy;

    public ScraperTask(
            String url,
            HttpRequestManager requestManager,
            PostPageParser postPageParser,
            DataStorage dataStorage,
            NewsPageParser newsPageParser,
            String typeOfTask) {
        this.url = url;
        this.requestManager = requestManager;
        this.postPageParser = postPageParser;
        this.dataStorage = dataStorage;
        this.typeOfTask = typeOfTask;
        this.newsPageParser = newsPageParser;
        if (typeOfTask.equals("post")) {
            this.strategy = new ScrapPostStrategy();
        } else {
            this.strategy = new ScrapUrlsStrategy();
        }
    }

    @Override
    public void run() {
        try {
            strategy.execute(url, requestManager, dataStorage, postPageParser, newsPageParser);
        } catch (IOException e) {
            System.err.println("Ошибка при обработке URL: " + url);
            e.printStackTrace();
            ErrorLogger.logError(url, e.getMessage());
        }
    }
}
