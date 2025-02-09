package org.example;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PostPageParser {
    public Post parse(Document doc, String url) {
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
