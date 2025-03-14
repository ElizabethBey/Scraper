package org.example.scraper;

import org.example.util.DataStorage;
import org.example.util.HttpRequestManager;
import org.example.util.Parser;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Scraper {
    private ExecutorService executor;
    private int threadPoolSize;
    private HttpRequestManager requestManager;
    private Parser parser;
    private DataStorage dataStorage;
    private String typeOfTask;

    public Scraper(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
        this.requestManager = new HttpRequestManager();
        this.parser = new Parser();
    }

    public void extractPostPageUrls(List<String> newsPageUrls) {
        typeOfTask = TaskConstants.SCRAP_POSTS_URLS;
        executor = Executors.newFixedThreadPool(threadPoolSize);
        dataStorage = new DataStorage("links.txt");
        for (String url : newsPageUrls) {
            executor.submit(
                new ScraperTask(url, requestManager, parser, dataStorage, typeOfTask)
            );
        }
    }

    public void extractPosts(List<String> postPageUrls) {
        typeOfTask = TaskConstants.SCRAP_POSTS;
        executor = Executors.newFixedThreadPool(threadPoolSize);
        dataStorage = new DataStorage("post.txt");
        for (String url : postPageUrls) {
            executor.submit(
                new ScraperTask(url, requestManager, parser, dataStorage, typeOfTask)
            );
        }
    }

    public void extractImgUrls(List<String> postPageUrls) {
        typeOfTask = TaskConstants.SCRAP_IMG_URLS;
        this.executor = Executors.newFixedThreadPool(threadPoolSize);
        this.dataStorage = new DataStorage("img.txt");
        for (String url : postPageUrls) {
            executor.submit(
                new ScraperTask(url, requestManager, parser, dataStorage, typeOfTask)
            );
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
