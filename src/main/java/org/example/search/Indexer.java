package org.example.search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.hunspell.Dictionary;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.synonym.SynonymGraphFilterFactory;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.example.entities.Post;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Indexer {
    private IndexWriter writer;
    private List<Post> posts;
    private Analyzer analyzer;
    private Directory directory;
    private SynonymGraphFilterFactory synonymGraphFilterFactory;

    public Indexer(String indexDirectoryPath, String synonymsDir, List<Post> postsList) throws IOException {
        posts = postsList;
        synonymGraphFilterFactory = new SynonymGraphFilterFactory(readSynonyms(synonymsDir));
        analyzer = new RussianAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        try {
            directory = FSDirectory.open(Paths.get(indexDirectoryPath));
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            writer = new IndexWriter(directory, iwc);
            indexPosts();
        } catch (IOException e) {
            System.err.println("Error creating Index: " + e.getMessage());
        } finally {
            close();
        }
        System.out.println("Index created successfully");
    }

    public Map readSynonyms(String synonymsDir) throws IOException {
        Map<String, String> synonyms = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(synonymsDir))) {
            while (reader.ready()) {
                String line = reader.readLine();
                String[] parts = line.split("=>");
                String[] inputs = parts[0].trim().split("\\s+");
                String output = parts[1].trim();
                for (String input : inputs) {
                    synonyms.put(input.trim(), output);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return synonyms;
    }

    public void close() throws IOException {
        writer.close();
    }

    public void indexPosts() throws IOException {
        for (Post post : posts) {
            Document doc = new Document();
            doc.add(new TextField("title", post.getTitle(), Field.Store.YES));
            doc.add(new TextField("description", post.getDescription(), Field.Store.YES));
            doc.add(new StringField("url", post.getUrl(), Field.Store.NO));
            writer.addDocument(doc);
        }
    }

    private Post documentToPost(Document doc) {
        return new Post(doc.get("title"), doc.get("description"), doc.get("url"));
    }

    public List<Post> searchByTitleAndDescription(String queryStr, int maxResults) {
        try {
            String[] fields = {"title", "description"};
            MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, analyzer);
            Query query = parser.parse(queryStr);
            return search(query, maxResults);
        } catch (Exception e) {
            System.err.println("Error searchByTitleAndDescription: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<Post> search(Query query, int maxResults) {
        try {
            IndexReader reader = DirectoryReader.open(directory);
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
}