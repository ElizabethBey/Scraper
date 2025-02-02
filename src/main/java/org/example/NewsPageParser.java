package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.Random;

public class NewsPageParser {
    private String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
    private int delay;
    private Random random = new Random();
    private String regex = "^https:\\/\\/www\\.nkj\\.ru\\/news\\/\\d+\\/\\?$";

    public NewsPageParser(int delay) {
        this.delay = delay;
    }

    public HashSet<String> getPostLinks(String url) {
        HashSet<String> postLinks = new HashSet<>();
        try {
            Document doc = Jsoup.connect(url).userAgent(userAgent).get();
            Elements links = doc.select("a[href^='/news/']:not([href*='?PAGEN_1'])");
            for (Element link : links) {
                String postUrl = link.absUrl("href");
                if (postUrl.equals("https://www.nkj.ru/news/") || postUrl.contains("mobile") || postUrl.contains("PAGEN")) {
                    continue;
                }
                postLinks.add(postUrl);
            }
            Thread.sleep(delay + random.nextInt(1000));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return postLinks;
    }
}
