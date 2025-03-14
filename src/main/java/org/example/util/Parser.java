package org.example.util;

import org.example.entities.Post;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;

public class Parser {
    private static final String BASE_URL = "https://www.nkj.ru";
    private String regex = "^https:\\/\\/www\\.nkj\\.ru\\/news\\/\\d+\\/\\?$";

    public HashSet<String> parsePostLinks(Document doc) {
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

    public HashSet<String> parseImgUrls(Document doc) {
        if (doc == null) {
            throw new IllegalArgumentException("Document is null");
        }

        HashSet<String> imgLinks = new HashSet<>();
        Elements imgElements = doc.select("div.figure-placeholder");
        for (Element imgElement : imgElements) {
            String imgUrl = imgElement.attr("data-fullsize-url");
            System.out.println(imgUrl);
            if (imgUrl != null && !imgUrl.isEmpty()) {
                imgLinks.add(BASE_URL + imgUrl);
            }
        }
        return imgLinks;
    }

    public Post parsePost(Document doc, String url) {
        if (doc == null) {
            throw new IllegalArgumentException("Document is null");
        }
        String title = doc.title();
        String description = "";
        Element mainElement = doc.selectFirst("main");
        if (mainElement != null) {
            Elements pTags = mainElement.getElementsByTag("p");
            for (Element pTag : pTags) {
                description += pTag.text() + " ";
            }
        }
        return new Post(title, description, url);
    }
}