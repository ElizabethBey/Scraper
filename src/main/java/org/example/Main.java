package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.lucene.document.Document;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final int MAX_RESULTS = 10;
    private static String postDir = "posts.txt";
    private static String linksDir = "links.txt";
    private static String queriesDir = "queries.txt";
    private static String indexDir = "index";
    private static List<String> postPageUrls = new ArrayList<>();
    private static List<Post> posts = new ArrayList<>();
    private static List<String> queries = new ArrayList<>();
    private static PostSearcher searcher;

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
        try (BufferedReader reader = new BufferedReader(new FileReader(linksDir))) {
            while (reader.ready()) {
                postPageUrls.add(reader.readLine());
            }
            System.out.println("Have read " + postPageUrls.size() + " urls");
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

    public static void readPosts() {
        try (BufferedReader reader = new BufferedReader(new FileReader(postDir))) {
            ObjectMapper objectMapper = new ObjectMapper();
            while (reader.ready()) {
                Post post = objectMapper.readValue(reader.readLine(), Post.class);
                posts.add(post);
            }
            System.out.println("Have read " + posts.size() + " posts");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readQueries(){
        try (BufferedReader reader = new BufferedReader(new FileReader(queriesDir))) {
            while (reader.ready()) {
                queries.add(reader.readLine());
            }
            System.out.println("Have read " + queries.size() + " queries");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void printSearchResults(List<Post> searchResults) {
        searchResults.forEach(post ->
            System.out.println(String.format("Title: %s, URL: %s", post.getTitle() ,post.getUrl()))
        );
    }

    public static void main(String[] args) throws IOException {
//        scrapPostPagesUrl();
//        readPostPagesUrls();
//        scrapPostPages();
        readPosts();
        readQueries();

        Indexer.createIndex(indexDir, posts);
        searcher = new PostSearcher(indexDir);
        queries.forEach(query -> {
                System.out.println(String.format("\nQuery: %s\n", query));
                printSearchResults(searcher.searchByTitleAndDescription(query, MAX_RESULTS));
            }
        );
    }
}