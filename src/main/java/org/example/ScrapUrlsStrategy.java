package org.example;

import java.io.IOException;
import java.util.Set;

public class ScrapUrlsStrategy implements ScrapingStrategy {
    @Override
    public void execute(String url, HttpRequestManager requestManager, DataStorage dataStorage, PostPageParser postPageParser, NewsPageParser newsPageParser) throws IOException {
        Set<String> postLinks = newsPageParser.getPostLinks(url);
        for (String postLink : postLinks) {
            dataStorage.saveData(postLink);
        }
    }
}