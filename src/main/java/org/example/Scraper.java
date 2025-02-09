package org.example;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Scraper {
    private ExecutorService executor;
    private HttpRequestManager requestManager;
    private PostPageParser postPageParser;
    private NewsPageParser newsPageParser;
    private DataStorage dataStorage;
    private String typeOfTask;

    public Scraper(String typeOfTask, int threadPoolSize) {
        this.executor = Executors.newFixedThreadPool(threadPoolSize);
        this.requestManager = new HttpRequestManager();
        this.postPageParser = new PostPageParser();
        this.newsPageParser = new NewsPageParser();
        this.typeOfTask = typeOfTask;
        this.dataStorage = new DataStorage(typeOfTask.equals("parseUrl") ? "links.txt" : "posts.txt");
    }

    public void startScraping(List<String> urls) {
        for (String url : urls) {
            executor.submit(new ScraperTask(url, requestManager, postPageParser, dataStorage, newsPageParser, this.typeOfTask));
        }
    }

    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
                System.err.println("Executor did not terminate in the given time.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        dataStorage.stopWriter();
    }
}
