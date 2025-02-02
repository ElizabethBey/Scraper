package org.example;

import java.io.IOException;

public class ScrapPostStrategy implements ScrapingStrategy {
    @Override
    public void execute(String url, HttpRequestManager requestManager, DataStorage dataStorage, PostPageParser postPageParser, NewsPageParser newsPageParser) throws IOException {
        String html = requestManager.sendRequest(url);
        if (html != null) {
            Post post = postPageParser.parse(html);
            if(post != null)
                dataStorage.saveData(post.toJson());
            else {
                ErrorLogger.logError(url, "Не удалось получить информацию о посте");
            }
        } else {
            ErrorLogger.logError(url, "Не удалось получить html");
        }
    }
}
