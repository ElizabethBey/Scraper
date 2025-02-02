package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class PostPageParser {
    public Post parse(String html) {
        if (html == null || html.isEmpty()) {
            return null;
        }
        Document doc = Jsoup.parse(html);
        String title = doc.select("h1.title").text();
        String description = doc.select("div.description").text();

        return new Post(title, description, doc.location());
    }
}
