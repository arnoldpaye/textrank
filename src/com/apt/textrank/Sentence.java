package com.apt.textrank;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @project textrank
 * @package com.apt.textrank
 * @class Sentence.java (UTF-8)
 * @date 06/09/2013
 * @author Arnold Paye
 */
public class Sentence {

    /* Members */
    private String text;
    private String[] tokens;
    private String[] tags;
    private String[] keys;

    /* Getters and Setters */
    public String getText() {
        return text;
    }

    public String[] getTokens() {
        return tokens;
    }

    public void setTokens(String[] tokens) {
        this.tokens = tokens;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String[] getKeys() {
        return keys;
    }

    public void setKeys(String[] keys) {
        this.keys = keys;
    }

    /**
     * Constructor with a parameter.
     *
     * @param text
     */
    public Sentence(String text) {
        this.text = text;
    }

    public List<Keyword> getCollocations2(Language language, Map<String, Double> keyList) {
        List<Keyword> keywords = new ArrayList<Keyword>();
        int i = 0;
        while (i < tokens.length) {
            int numberOfTokens = 0;
            int numberOfKeywords = 0;
            double cumulativeRank = 0;
            if (language.isNoun(tags[i]) && keyList.containsKey(keys[i]) && !language.isPlural(tokens[i])) {
                numberOfTokens++;
                numberOfKeywords++;
                cumulativeRank += keyList.get(keys[i]);
                StringBuilder sb = new StringBuilder(tokens[i].toUpperCase());
                int p = i + 1;
                if (p < tokens.length && (language.isAdjective(tags[p]) || language.isNoun(tags[p])) && keyList.containsKey(keys[p])) {
                    sb.append(" ").append(tokens[p].toUpperCase());
                    numberOfTokens++;
                    numberOfKeywords++;
                    cumulativeRank += keyList.get(keys[p]);
                    p++;
                }
                if (p < tokens.length && language.isConjuction(tags[p])) {
                    if (p + 1 < tokens.length && language.isNoun(tags[p + 1]) && keyList.containsKey(keys[p + 1]) && !keys[p+1].equals(keys[i])) {
                        sb.append(" ").append(tokens[p].toUpperCase()).append(" ").append(tokens[p + 1].toUpperCase());
                        numberOfTokens++;
                        numberOfTokens++;
                        numberOfKeywords++;
                        cumulativeRank += keyList.get(keys[p + 1]);
                        p += 2;
                    }
                    if (p < tokens.length && language.isAdjective(tags[p]) && keyList.containsKey(keys[p])) {
                        sb.append(" ").append(tokens[p].toUpperCase());
                        numberOfTokens++;
                        numberOfKeywords++;
                        cumulativeRank += keyList.get(keys[p]);
                    }
                }
                if (numberOfTokens > 1) {
                    keywords.add(new Keyword(sb.toString(), cumulativeRank / numberOfKeywords));
                }
            }
            i++;
        }
        return keywords;
    }

    public List<Keyword> getCollocations(Language language, Map<String, Double> keyList) {
        List<Keyword> keywords = new ArrayList<Keyword>();
        int i = 0;
        while (i < tokens.length) {
            int contRank = 0;
            double rank = 0;
            if (keyList.containsKey(keys[i]) && language.isNoun(tags[i]) && tokens[i].length() > 4) {
                rank += keyList.get(keys[i]);
                contRank++;
                int numberOfTokens = 1;
                StringBuilder stringBuilder = new StringBuilder(tokens[i].toUpperCase());
                int px1 = -1;
                if (i + 1 < tokens.length) {
                    if (keyList.containsKey(keys[i + 1]) && language.isAdjective(tags[i + 1]) && tokens[i + 1].length() > 4) {
                        rank += keyList.get(keys[i + 1]);
                        contRank++;
                        stringBuilder.append(" ").append(tokens[i + 1].toUpperCase());
                        px1 = i + 2;
                        numberOfTokens++;
                    } else {
                        px1 = i + 1;
                    }
                }
                if (px1 != -1 && (tags[px1].equals("SPS") || tags[px1].equals("CC"))) {
                    int px2 = -1;
                    if (px1 + 1 < tokens.length && (keyList.containsKey(keys[px1 + 1]) && language.isNoun(tags[px1 + 1]) && tokens[px1 + 1].length() > 4)) {
                        rank += keyList.get(keys[px1 + 1]);
                        contRank++;
                        numberOfTokens++;
                        numberOfTokens++;
                        stringBuilder.append(" ").append(tokens[px1].toUpperCase());
                        stringBuilder.append(" ").append(tokens[px1 + 1].toUpperCase());
                        px2 = px1 + 1;
                        if (px2 + 1 < tokens.length && keyList.containsKey(keys[px2 + 1]) && language.isAdjective(tags[px2 + 1]) && tokens[px2 + 1].length() > 4) {
                            rank += keyList.get(keys[px2 + 1]);
                            contRank++;
                            numberOfTokens++;
                            stringBuilder.append(" ").append(tokens[px2 + 1].toUpperCase());
                        }
                    }
                }
                if (numberOfTokens > 1) {
                    keywords.add(new Keyword(stringBuilder.toString(), rank / contRank));
                }
            }
            i++;
        }
        return keywords;
    }
}