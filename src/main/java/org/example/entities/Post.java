package org.example.entities;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Post {
    private String title;
    private String description;
    private String url;
    private String[] highlight;

    public Post(String title, String description, String url) {
        this.title = title;
        this.description = description;
        this.url = url;
    }

    public Post(String title, String description, String url, String[] highlight) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.highlight = highlight;
    }

    public Post() {}

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }

    public String[] getHighlight() {
        return highlight;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setHighlight(String[] highlight) {
        this.highlight = highlight;
    }

    public String toJson() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(this);
        } catch (Exception e) {
            e.printStackTrace();
            return "{}";
        }
    }
    public void printInfo() {
        System.out.println("Title: " + title + " URL: " + url);
        for (String s : highlight) {
            System.out.print("/" + s + "/ ");
        }
        System.out.println();
    }
}
