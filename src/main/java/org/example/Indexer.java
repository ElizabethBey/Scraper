package org.example;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class Indexer {

    private IndexWriter writer;
    private List<Post> posts;

    public Indexer(String indexDirectoryPath, List<Post> posts) throws IOException {
        this.posts = posts;
        Directory dir = FSDirectory.open(Paths.get(indexDirectoryPath));
        Analyzer analyzer = new RussianAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        writer = new IndexWriter(dir, iwc);
    }

    public void close() throws IOException {
        writer.close();
    }

    public void indexPosts() throws IOException {
        for (Post post : posts) {
            indexPost(post);
        }
    }

    private void indexPost(Post post) throws IOException {
        Document doc = new Document();

        doc.add(new TextField("title", post.getTitle(), Field.Store.YES));
        doc.add(new TextField("description", post.getDescription(), Field.Store.YES));
        doc.add(new StringField("url", post.getUrl(), Field.Store.YES));

        writer.addDocument(doc);
    }

    public static void createIndex(String indexDir, List<Post> posts) throws IOException {
        Indexer indexer = null;
        try {
            indexer = new Indexer(indexDir, posts);
            indexer.indexPosts();
        } catch (IOException e) {
            System.err.println("Error creating Index: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (indexer != null) {
                indexer.close();
            }
        }
        System.out.println("Index created successfully");
    }
}