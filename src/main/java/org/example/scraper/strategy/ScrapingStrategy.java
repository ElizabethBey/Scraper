package org.example.scraper.strategy;

import org.example.util.DataStorage;
import org.example.util.HttpRequestManager;
import org.example.util.Parser;

import java.io.IOException;

public interface ScrapingStrategy {
    void execute(String url, HttpRequestManager requestManager, DataStorage dataStorage, Parser parser) throws IOException;
}
