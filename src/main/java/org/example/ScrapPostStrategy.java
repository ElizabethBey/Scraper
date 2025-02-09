package org.example;

import org.jsoup.nodes.Document;

import java.io.IOException;

public class ScrapPostStrategy implements ScrapingStrategy {
    @Override
    public void execute(String url, HttpRequestManager requestManager, DataStorage dataStorage, PostPageParser postPageParser, NewsPageParser newsPageParser) throws IOException {
        Document html = requestManager.sendRequest(url);
        if (html != null) {
            Post post = postPageParser.parse(html, url);
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
