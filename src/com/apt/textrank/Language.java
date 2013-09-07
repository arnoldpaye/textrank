package com.apt.textrank;

import java.io.File;
import java.io.IOException;
import opennlp.tools.lang.spanish.PosTagger;
import opennlp.tools.lang.spanish.SentenceDetector;
import opennlp.tools.lang.spanish.Tokenizer;
import org.tartarus.snowball.ext.spanishStemmer;

/**
 * @project textrank
 * @package com.apt.textrank
 * @class Language.java (UTF-8)
 * @date 05/09/2013
 * @author Arnold Paye
 */
public class Language {
    
    /* Members */
    public static SentenceDetector splitter;
    public static Tokenizer tokenizer;
    public static PosTagger tagger;
    public static spanishStemmer stemmer;
    
    /**
     * Load resources.
     * @param pathResources
     * @throws IOException 
     */
    private void loadResources(String pathResources) throws IOException {
        splitter = new SentenceDetector((new File(pathResources, "SpanishSent.bin.gz")).getPath());
        tokenizer = new Tokenizer((new File(pathResources, "SpanishTok.bin.gz")).getPath());
        tagger = new PosTagger((new File(pathResources, "SpanishPOS.bin.gz")).getPath());
        stemmer = new spanishStemmer();
    }

    /**
     * Constructor with a parameter.
     * @param pathResources
     * @throws IOException 
     */
    public Language(String pathResources) throws IOException {
        if (splitter == null || tokenizer == null || tagger == null || stemmer == null) {
            loadResources(pathResources);
        }
    }
    
    /**
     * Split text into sentences.
     * @param text
     * @return 
     */
    public String[] splitParagraph(String text) {
        return splitter.sentDetect(text);
    }
    
    /**
     * Tokenize sentence.
     * @param sentence
     * @return 
     */
    public String[] tokenizeSentence(String sentence) {
        return tokenizer.tokenize(sentence);
    }
    
    /**
     * Tag token list with a part-of-speech.
     * @param tokenList
     * @return 
     */
    public String[] tagTokens(String[] tokenList) {
        return tagger.tag(tokenList);
    }
    
    public String stem(String token) {
        stemmer.setCurrent(token);
        stemmer.stem();
        return stemmer.getCurrent().toUpperCase();
    }
    
    public String getKey(String token, String tag) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(tag);
        stringBuilder.append('_');
        stemmer.setCurrent(token);
        stemmer.stem();
        stringBuilder.append(stemmer.getCurrent().toUpperCase());
        return stringBuilder.toString();
    }
    
    public boolean isRelevant(String pos) {
        return pos.equals("NC") || pos.equals("AQ");
    }
}