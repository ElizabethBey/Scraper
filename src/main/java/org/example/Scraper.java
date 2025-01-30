package org.example;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Scraper {
    private ExecutorService executor;
    private HttpRequestManager requestManager;
    private PostPageParser postPageParser;
    private NewsPageParser newsPageParser;
    private DataStorage dataStorage;
    private String typeOfTask;

    public Scraper(String typeOfTask) {
        this.executor = Executors.newFixedThreadPool(10);
        this.requestManager = new HttpRequestManager();
        this.postPageParser = new PostPageParser();
        this.newsPageParser = new NewsPageParser();
        this.typeOfTask = typeOfTask;
        this.dataStorage = new DataStorage(typeOfTask.equals("parseUrl") ? "links.txt" : "posts.json");
    }

    public void startScraping(List<String> urls) {
        for (String url : urls) {
            executor.submit(new ScraperTask(url, requestManager, postPageParser, dataStorage, newsPageParser, this.typeOfTask));
        }
    }

    public void shutdown() {
        executor.shutdown();
    }
}
