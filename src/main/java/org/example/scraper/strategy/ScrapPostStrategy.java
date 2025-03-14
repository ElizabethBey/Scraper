package org.example.scraper.strategy;

import org.example.entities.Post;
import org.example.util.DataStorage;
import org.example.util.ErrorLogger;
import org.example.util.HttpRequestManager;
import org.example.util.Parser;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class ScrapPostStrategy implements ScrapingStrategy {
    @Override
    public void execute(
            String url,
            HttpRequestManager requestManager,
            DataStorage dataStorage,
            Parser parser
    ) throws IOException {
        Document html = requestManager.sendRequest(url);
        if (html != null) {
            Post post = parser.parsePost(html, url);
            if (post != null)
                dataStorage.saveData(post.toJson());
            else {
                ErrorLogger.logError(url, "Не удалось получить информацию о посте");
            }
        } else {
            ErrorLogger.logError(url, "Не удалось получить html");
        }
    }
}
