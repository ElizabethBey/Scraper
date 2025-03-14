package org.example.scraper.strategy;

import org.example.util.DataStorage;
import org.example.util.HttpRequestManager;
import org.example.util.Parser;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Set;

public class ScrapUrlsStrategy implements ScrapingStrategy {
    @Override
    public void execute(
            String url,
            HttpRequestManager requestManager,
            DataStorage dataStorage,
            Parser parser
    ) throws IOException {
        Document html = requestManager.sendRequest(url);
        Set<String> postLinks = parser.parsePostLinks(html);
        for (String postLink : postLinks) {
            dataStorage.saveData(postLink);
        }
    }
}