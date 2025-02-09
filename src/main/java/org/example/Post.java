package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Post {
    private String title;
    private String description;
    private String url;

    public Post(String title, String description, String url) {
        this.title = title;
        this.description = description;
        this.url = url;
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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUrl(String url) {
        this.url = url;
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
}
