package org.example.search;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
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
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.example.entities.Post;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Indexer {
    private Path indexPath;
    private IndexWriter writer;
    private List<Post> posts;
    private Analyzer analyzer;
    private Directory directory;

    public Indexer(String indexDirectoryPath, List<Post> postsList) throws IOException {
        posts = postsList;
        indexPath = Paths.get(indexDirectoryPath);
        analyzer = new MyAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        try {
            directory = FSDirectory.open(indexPath);
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

    public void close() throws IOException {
        writer.close();
    }

    private static void analyzeText(Analyzer analyzer, String text) throws IOException {
        try (TokenStream tokenStream = analyzer.tokenStream("text", new StringReader(text))) {
            CharTermAttribute termAttribute = tokenStream.addAttribute(CharTermAttribute.class);
            OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);

            tokenStream.reset();

            while (tokenStream.incrementToken()) {
                System.out.println("Токен: " + termAttribute.toString() +
                        ", Start Offset: " + offsetAttribute.startOffset() +
                        ", End Offset: " + offsetAttribute.endOffset());
            }

            tokenStream.end();
        }
    }

    private void indexPosts() throws IOException {
        for (Post post : posts) {
            Document doc = new Document();
            doc.add(new TextField("title", post.getTitle(), Field.Store.YES));
            doc.add(new TextField("description", post.getDescription(), Field.Store.YES));
            doc.add(new StringField("url", post.getUrl(), Field.Store.YES));
            writer.addDocument(doc);
        }
    }

    private Post documentToPost(Document doc, String[] highlight) {
        return new Post(doc.get("title"), doc.get("description"), doc.get("url"), highlight);
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

    public class CustomFormatter implements Formatter {
        @Override
        public String highlightTerm(String originalText, TokenGroup tokenGroup) {
            if (tokenGroup.getTotalScore() > 0) {
                return "<" + originalText + ">";
            }
            return originalText;
        }
    }

    private List<Post> search(Query query, int maxResults) {
        Formatter formatter = new CustomFormatter();
        QueryScorer scorer = new QueryScorer(query);
        Highlighter highlighter = new Highlighter(formatter, scorer);
        Fragmenter fragmenter = new SimpleSpanFragmenter(scorer, 10);
        highlighter.setTextFragmenter(fragmenter);
        try {
            IndexReader reader = DirectoryReader.open(directory);
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs results = searcher.search(query, maxResults);
            ScoreDoc[] hits = results.scoreDocs;

            List<Post> posts = new ArrayList<>();
            for (int i = 0; i < hits.length; i++) {
                int docId = hits[i].doc;
                Document doc = searcher.doc(docId);
                TokenStream stream = TokenSources.getAnyTokenStream(reader, docId, "description", analyzer);
                String text = doc.get("description");
                String[] frags = highlighter.getBestFragments(stream, text, 10);
                posts.add(documentToPost(doc, frags));
            }
            return posts;
        } catch (IOException e) {
            System.err.println("Ошибка при поиске: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        } catch (InvalidTokenOffsetsException e) {
            throw new RuntimeException(e);
        }
    }
}