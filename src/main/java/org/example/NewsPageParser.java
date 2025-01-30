package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;

public class NewsPageParser {
    private String baseUrl = "https://www.nkj.ru/";
    private String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
    public HashSet<String> getPostLinks(String url) {
        HashSet<String> postLinks = new HashSet<>();
        try {
            Document doc = Jsoup.connect(url).userAgent(userAgent).get();
            Elements links = doc.select("a[href^='/news/']:not([href*='?PAGEN_1'])");
            for (Element link : links) {
                String postUrl = link.attr("href");
                if (postUrl.equals("/news/")) {
                    continue;
                }
                postLinks.add(baseUrl + postUrl);
            }
            Thread.sleep(2000);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return postLinks;
    }
}
