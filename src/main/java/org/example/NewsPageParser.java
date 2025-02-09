package org.example;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;

public class NewsPageParser {
    private String regex = "^https:\\/\\/www\\.nkj\\.ru\\/news\\/\\d+\\/\\?$";

    public HashSet<String> getPostLinks(Document doc) {
        HashSet<String> postLinks = new HashSet<>();
        Elements links = doc.select("a[href^='/news/']:not([href*='?PAGEN_1'])");
        for (Element link : links) {
            String postUrl = link.absUrl("href");
            if (!postUrl.equals("https://www.nkj.ru/news/")
                && !postUrl.contains("mobile")
                && !postUrl.contains("PAGEN")) {
                postLinks.add(postUrl);
            }
        }
        return postLinks;
    }
}
