package org.example.search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.util.CharsRef;
import org.tartarus.snowball.ext.RussianStemmer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MyAnalyzer extends Analyzer {
    private static String stopwordsFile = "russian_stopwords.txt";
    private static String synonymsFile = "synonyms.txt";

    private CharArraySet stopwords;
    private SynonymMap synonymMap;

    public MyAnalyzer() throws IOException {
        this.stopwords = loadStopwords(stopwordsFile);
        this.synonymMap = loadSynonyms(synonymsFile);
    }

    private CharArraySet loadStopwords(String stopwordsFile) throws IOException {
        List<String> stopWordsList = new ArrayList<>();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(stopwordsFile);
             InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {

            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stopWordsList.add(line.trim());
            }
        }
        return new CharArraySet(stopWordsList, true);
    }

    private SynonymMap loadSynonyms(String synonymsFile) throws IOException {
        SynonymMap.Builder synonymMapBuilder = new SynonymMap.Builder(true);

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(synonymsFile);
             InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {

            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("=>");
                if (parts.length != 2) continue;

                String[] synonyms = parts[0].trim().split("\\s+");
                String replacement = parts[1].trim();

                for (String synonym : synonyms) {
                    addSynonym(synonymMapBuilder, synonym, replacement);
                }
            }
        }

        return synonymMapBuilder.build();
    }

    private void addSynonym(SynonymMap.Builder synonymMapBuilder, String synonym, String replacement) {
        CharsRef synonymChars = new CharsRef(synonym);
        CharsRef replacementChars = new CharsRef(replacement);

        synonymMapBuilder.add(synonymChars, replacementChars, true);
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        final StandardTokenizer source = new StandardTokenizer();
        TokenStream result = new LowerCaseFilter(source);
        result = new StopFilter(result, stopwords);
        result = new SynonymGraphFilter(result, synonymMap, true);
        result = new SnowballFilter(result, new RussianStemmer());
        return new TokenStreamComponents(source, result);
    }
}