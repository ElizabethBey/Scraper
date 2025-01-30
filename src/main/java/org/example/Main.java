package org.example;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<String> pageUlrList = new ArrayList<>();
        // 388
        for (int i = 1; i <= 388; i++) {
            String link = "https://www.nkj.ru/news/?PAGEN_1=" + i;
            pageUlrList.add(link);
        }

        System.out.println(pageUlrList);
        Scraper scraper = new Scraper("pagesUrl");
        scraper.startScraping(pageUlrList);
        scraper.shutdown();
    }
}