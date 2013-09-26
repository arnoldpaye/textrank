package com.apt.textrank;

import java.util.ArrayList;
import java.util.List;

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

    public List<Keyword> getCollocations(Language language, List<String> keyList) {
        List<Keyword> keywords = new ArrayList<Keyword>();
        int i = 0;
        while (i < tokens.length) {
            if (keyList.contains(keys[i]) && language.isNoun(tags[i]) && tokens[i].length() > 4) {
                int numberOfTokens = 1;
                StringBuilder stringBuilder = new StringBuilder(tokens[i].toUpperCase());
                int px1 = -1;
                if (i + 1 < tokens.length) {
                    if (language.isAdjective(tags[i + 1]) && tokens[i + 1].length() > 4) {
                        stringBuilder.append(" ").append(tokens[i + 1].toUpperCase());
                        px1 = i + 2;
                        numberOfTokens++;
                    } else {
                        px1 = i + 1;
                    }
                }
                if (px1 != -1 && (tags[px1].equals("SPS") || tags[px1].equals("CC"))) {
                    int px2 = -1;
                    if (px1 + 1 < tokens.length && (keyList.contains(keys[px1 + 1]) || language.isNoun(tags[px1 + 1]) && tokens[px1 + 1].length() > 4)) {
                        numberOfTokens++;
                        numberOfTokens++;
                        stringBuilder.append(" ").append(tokens[px1].toUpperCase());
                        stringBuilder.append(" ").append(tokens[px1 + 1].toUpperCase());
                        px2 = px1 + 1;
                        if (px2 + 1 < tokens.length && language.isAdjective(tags[px2 + 1]) && tokens[px2 + 1].length() > 4) {
                            numberOfTokens++;
                            stringBuilder.append(" ").append(tokens[px2 + 1].toUpperCase());
                        }
                    }
                }
                if (numberOfTokens > 1) {
                    keywords.add(new Keyword(stringBuilder.toString(), 0));
                }
            }
            i++;
        }
        return keywords;
    }
}