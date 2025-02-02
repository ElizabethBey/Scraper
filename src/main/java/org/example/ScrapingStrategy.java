package org.example;

import java.io.IOException;

public interface ScrapingStrategy {
    void execute(String url, HttpRequestManager requestManager, DataStorage dataStorage, PostPageParser postPageParser, NewsPageParser newsPageParser) throws IOException;
}
