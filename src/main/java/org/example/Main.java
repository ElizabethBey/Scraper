package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static List<String> postPageUrls = new ArrayList<>();
    private static List<Post> posts = new ArrayList<>();

    public static void scrapPostPagesUrl() {
        System.out.println("Scrapping urls...");
        List<String> pageUlrList = new ArrayList<>();
        for (int i = 1; i <= 388; i++) {
            String link = "https://www.nkj.ru/news/?PAGEN_1=" + i;
            pageUlrList.add(link);
        }
        Scraper scraperUrl = new Scraper("parseUrl", 10);
        scraperUrl.startScraping(pageUlrList);
        scraperUrl.shutdown();
    }

    public static void readPostPagesUrls(){
        try (BufferedReader reader = new BufferedReader(new FileReader("links.txt"))) {
            while (reader.ready()) {
                postPageUrls.add(reader.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void scrapPostPages() {
        System.out.println("Scrapping post pages...");
        Scraper scraper1 = new Scraper("post", 10);
        scraper1.startScraping(postPageUrls);
        scraper1.shutdown();
    }

    public static void readPosts(){
        try (BufferedReader reader = new BufferedReader(new FileReader("posts.txt"))) {
            ObjectMapper objectMapper = new ObjectMapper();
            while (reader.ready()) {
                Post post = objectMapper.readValue(reader.readLine(), Post.class);
                posts.add(post);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        scrapPostPagesUrl();
        readPostPagesUrls();
        scrapPostPages();
        readPosts();
        System.out.println(posts.size());
    }
}