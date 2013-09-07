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

    /**
     * Get collocations.
     * @param keyList
     * @return 
     */
    public List<Keyword> getCollocations(List<String> keyList) {
        List<Keyword> keywords = new ArrayList<Keyword>();
        int i = 0;
        while (i < tokens.length) {
            boolean sw = false;
            if (keyList.contains(keys[i])) {
                StringBuilder stringBuilder = new StringBuilder(tokens[i].toUpperCase());
                int px = i + 1;
                int numberOfTokes = 1;
                while (true) {
                    if (keyList.contains(keys[px]) || tags[px].equals("NC") && tokens[px].trim().length() > 1) {
                        stringBuilder.append(" ").append(tokens[px].toUpperCase());
                        sw = true;
                        numberOfTokes++;
                        px++;
                    } else if (((tags[px].equals("SPS") && tokens[px].trim().length() > 1) || tags[px].equals("CC"))) {
                        String aux = " " + tokens[px].toUpperCase();
                        numberOfTokes++;
                        px++;
                        if (keyList.contains(keys[px]) || tags[px].equals("NC") && tokens[px].trim().length() > 1) {
                            stringBuilder.append(aux).append(" ").append(tokens[px].toUpperCase());
                            sw = true;
                            numberOfTokes++;
                            px++;
                        } else {
                            break;
                        }
                    } else {
                        break;
                    }
                }
                if (sw && numberOfTokes < 5) {
                    keywords.add(new Keyword(stringBuilder.toString(), 0));
                }
            }
            i++;
        }
        return keywords;
    }
}