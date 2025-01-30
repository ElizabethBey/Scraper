package org.example;

import java.io.IOException;
import java.util.Set;

public class ScraperTask implements Runnable {
    private String url;
    private HttpRequestManager requestManager;
    private PostPageParser postPageParser;
    private DataStorage dataStorage;
    private NewsPageParser newsPageParser;
    private String typeOfTask;

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
    }

    public void scrapPost() throws IOException {
        String html = this.requestManager.sendRequest(url);
        if (html != null) {
            Post post = postPageParser.PostPageParser(html);
            dataStorage.saveData(post.toJson());
        } else {
            System.err.println("Ошибка при обработке URL: " + url);
        }
    }

    public void scrapUrls() throws IOException {
        Set<String> postLinks = this.newsPageParser.getPostLinks(url);
        for (String postLink : postLinks) {
            dataStorage.saveData(postLink);
        }
    }

    @Override
    public void run() {
        try {
            if (typeOfTask.equals("post")) {
                scrapPost();
            } else {
                scrapUrls();
            }
        } catch (IOException e) {
            System.err.println("Ошибка при обработке URL: " + url);
            e.printStackTrace();
            ErrorLogger.logError(url, e.getMessage());
        }
    }
}
