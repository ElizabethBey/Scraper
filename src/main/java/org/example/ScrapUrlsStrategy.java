package org.example;

import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Set;

public class ScrapUrlsStrategy implements ScrapingStrategy {
    @Override
    public void execute(String url, HttpRequestManager requestManager, DataStorage dataStorage, PostPageParser postPageParser, NewsPageParser newsPageParser) throws IOException {
        Document html = requestManager.sendRequest(url);
        Set<String> postLinks = newsPageParser.getPostLinks(html);
        for (String postLink : postLinks) {
            dataStorage.saveData(postLink);
        }
    }
}