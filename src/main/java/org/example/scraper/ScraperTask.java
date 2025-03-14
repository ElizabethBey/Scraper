package org.example.scraper;

import org.example.scraper.strategy.ScrapPostStrategy;
import org.example.scraper.strategy.ScrapUrlsStrategy;
import org.example.scraper.strategy.ScrapingImgUrlsStrategy;
import org.example.scraper.strategy.ScrapingStrategy;
import org.example.util.DataStorage;
import org.example.util.ErrorLogger;
import org.example.util.HttpRequestManager;
import org.example.util.Parser;

import java.io.IOException;

public class ScraperTask implements Runnable {
    private String url;
    private HttpRequestManager requestManager;
    private Parser parser;
    private DataStorage dataStorage;
    private ScrapingStrategy strategy;

    public ScraperTask(
            String url,
            HttpRequestManager requestManager,
            Parser parser,
            DataStorage dataStorage,
            String typeOfTask
    ) {
        this.url = url;
        this.requestManager = requestManager;
        this.parser = parser;
        this.dataStorage = dataStorage;
        switch (typeOfTask) {
            case TaskConstants.SCRAP_POSTS:
                this.strategy = new ScrapPostStrategy();
                break;
            case TaskConstants.SCRAP_POSTS_URLS:
                this.strategy = new ScrapUrlsStrategy();
                break;
            case TaskConstants.SCRAP_IMG_URLS:
                this.strategy = new ScrapingImgUrlsStrategy();
                break;
        }
    }

    @Override
    public void run() {
        try {
            strategy.execute(url, requestManager, dataStorage, parser);
        } catch (IOException e) {
            System.err.println("Ошибка при обработке URL: " + url);
            e.printStackTrace();
            ErrorLogger.logError(url, e.getMessage());
        }
    }
}
