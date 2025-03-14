package org.example.search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.example.entities.Post;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PostSearcher {

    private final String indexDir;
    private final Analyzer analyzer;

    public PostSearcher(String indexDir) {
        this.indexDir = indexDir;
        this.analyzer = new RussianAnalyzer();
    }

    private Post documentToPost(Document doc) {
        return new Post(doc.get("title"), doc.get("description"), doc.get("url"));
    }

    private List<Post> search(Query query, int maxResults) {
        try (Directory dir = FSDirectory.open(Paths.get(indexDir));
             IndexReader reader = DirectoryReader.open(dir)) {

            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs results = searcher.search(query, maxResults);
            ScoreDoc[] hits = results.scoreDocs;

            List<Post> posts = new ArrayList<>();
            for (ScoreDoc hit : hits) {
                Document doc = searcher.doc(hit.doc);
                posts.add(documentToPost(doc));
            }

            return posts;

        } catch (IOException e) {
            System.err.println("Ошибка при поиске: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Post> searchByTitle(String queryStr, int maxResults) {
        try {
            QueryParser parser = new QueryParser("title", analyzer);
            Query query = parser.parse(queryStr);
            return search(query, maxResults);
        } catch (Exception e) {
            System.err.println("Error searchByTitle: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Post> searchByDescription(String queryStr, int maxResults) {
        try {
            QueryParser parser = new QueryParser("description", analyzer);
            Query query = parser.parse(queryStr);
            return search(query, maxResults);
        } catch (Exception e) {
            System.err.println("Error searchByDescription: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Post> searchByTitleAndDescription(String queryStr, int maxResults) {
        try {
            String[] fields = {"title", "description"};
            MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, analyzer);
            Query query = parser.parse(queryStr);
            return search(query, maxResults);
        } catch (Exception e) {
            System.err.println("Error searchByTitleAndDescription: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}