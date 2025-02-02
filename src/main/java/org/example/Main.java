package org.example;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<String> pageUlrList = new ArrayList<>();
        for (int i = 1; i <= 388; i++) {
            String link = "https://www.nkj.ru/news/?PAGEN_1=" + i;
            pageUlrList.add(link);
        }
        Scraper scraper = new Scraper("parseUrl", 10, 2000);
        scraper.startScraping(pageUlrList);
        scraper.shutdown();
    }
}