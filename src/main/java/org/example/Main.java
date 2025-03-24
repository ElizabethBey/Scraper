package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entities.Post;
import org.example.img.ImgTransformer;
import org.example.scraper.Scraper;
import org.example.search.Indexer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Main {
    private static final int MAX_RESULTS = 10;
    private static String postUrlsDir = "posts.txt";
    private static String linksDir = "links.txt";
    private static String queriesDir = "queries.txt";
    private static String imgDir = "img.txt";
    private static String indexDir = "index";
    private static String outputImgDir = "outputImg/";

    private static List<String> postPageUrls = new ArrayList<>();
    private static List<String> imgUrls = new ArrayList<>();
    private static List<Post> posts = new ArrayList<>();
    private static List<String> queries = new ArrayList<>();
    private static List<Post> testPosts = List.of(
            new Post("1 период", "период", "url"),
            new Post("2 периода", "периода", "url"),
            new Post("3 и кот", "и кот", "url")
    );

    private static Scraper scraper = new Scraper(10);
    private static Indexer indexer;
    private static ImgTransformer imageTransformer;

    public static List<String> getPostPageUrls() {
        List<String> pageUlrList = new ArrayList<>();
        for (int i = 1; i <= 388; i++) {
            String link = "https://www.nkj.ru/news/?PAGEN_1=" + i;
            pageUlrList.add(link);
        }
        return pageUlrList;
    }

    public static void scrapPostPagesUrl() {
        System.out.println("Scrapping post pages urls...");
        List<String> pageUlrList = getPostPageUrls();
        scraper.extractPostPageUrls(pageUlrList);
        scraper.shutdown();
    }

    public static void scrapPosts() {
        System.out.println("Scrapping post pages for posts...");
        scraper.extractPosts(postPageUrls);
        scraper.shutdown();
    }

    public static void scrapImgUrls() {
        System.out.println("Scrapping post pages for img urls...");
        scraper.extractImgUrls(postPageUrls);
        scraper.shutdown();
    }

    public static List<String> readFileData(String dir) {
        List<String> res = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(dir))) {
            while (reader.ready()) {
                res.add(reader.readLine());
            }
            System.out.println("Have read " + res.size() + " lines from " + dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static List<Post> readPosts() {
        List<Post> res = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(postUrlsDir))) {
            ObjectMapper objectMapper = new ObjectMapper();
            while (reader.ready()) {
                Post post = objectMapper.readValue(reader.readLine(), Post.class);
                res.add(post);
            }
            System.out.println("Have read " + res.size() + " posts");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static void printSearchResults(List<Post> searchResults) {
        IntStream.range(0, searchResults.size())
                .forEach(index -> {
                    Post post = searchResults.get(index);
                    Integer num = index + 1;
                    System.out.print("\n" + num + ") ");
                    post.printInfo();
                });
    }

    public static void main(String[] args) throws IOException {
        // scrap and read urls for post pages
//        scrapPostPagesUrl();
//        postPageUrls = readFileData(linksDir);

        // scrap post data and img
//        scrapPostPages();
//        scrapImgUrls();
//        imgUrls = readFileData(imgDir);

        // do img transformations
//        imageTransformer = new ImgTransformer(imgUrls.subList(0, 100), outputImgDir);
//        imageTransformer.removeImagesBg();

        String bold = "\033[1m";
        String reset = "\033[0m";
        // read posts and queries, make index and do search
        posts = readPosts();
        queries = readFileData(queriesDir);
        indexer = new Indexer(indexDir, posts);
        queries.forEach(query -> {
                System.out.println(String.format(bold + "\nQuery: %s" + reset, query));
                printSearchResults(indexer.searchByTitleAndDescription(query, MAX_RESULTS));
            }
        );
    }
}