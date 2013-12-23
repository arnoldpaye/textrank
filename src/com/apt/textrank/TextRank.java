package com.apt.textrank;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;

/**
 * @project textrank
 * @package com.apt.textrank
 * @class TextRank.java (UTF-8)
 * @date 05/09/2013
 * @author Arnold Paye
 */
public class TextRank {

    /* Members */
    private Language language;
    private String pathResources;
    private List<Sentence> sentences;
    private SummaryStatistics statistics;
    private Map<String, Integer> weights;
    private static int WINDOWS_SIZE = 3;

    /* Getters and Setters */
    public List<Sentence> getSentences() {
        return sentences;
    }

    /**
     * Constructor with two parameters.
     *
     * @param text
     * @param pathResources
     * @throws IOException
     */
    public TextRank(String pathResources) throws IOException {
        this.pathResources = pathResources;
        language = new Language(pathResources);
        sentences = new ArrayList<Sentence>();
        statistics = new SummaryStatistics();
    }

    public Graph buildGraph(String text) {
        Graph graph = new Graph();
        weights = new TreeMap<String, Integer>();
        String lastKey = "";
        String currentKey = "";
        int lastPosition = -1;
        for (String sentence : language.splitParagraph(text)) {
            Sentence sent = new Sentence(sentence);
            String[] tokenArray = language.tokenizeSentence(sentence);
            String[] tagArray = language.tagTokens(tokenArray);
            String[] keyArray = new String[tokenArray.length];

            for (int i = 0; i < tokenArray.length; i++) {
                keyArray[i] = language.getKey(tokenArray[i], tagArray[i]);
                System.out.println(">>> " + tokenArray[i] + " " + tagArray[i] + " " + keyArray[i]);
                if (language.isRelevant(tagArray[i].trim()) && !tokenArray[i].trim().isEmpty() && tokenArray[i].trim().length() > 2 && !tokenArray[i].equals("(") && !tokenArray[i].equals(")")) {
                    currentKey = keyArray[i];
                    if (!lastKey.isEmpty() && lastPosition != -1 && ((i - lastPosition) <= WINDOWS_SIZE)) {
                        //System.out.println("KEYS >>>" + currentKey + " " + lastKey);
                        String combineKey = currentKey + '/' + lastKey;
                        String rcombineKey = lastKey + '/' + currentKey;
//                        System.out.println("DBG COMBINE KEY " + combineKey);
                        if (weights.containsKey(combineKey)) {
                            weights.put(combineKey, weights.get(combineKey) + 1);
                        } else if (weights.containsKey(rcombineKey)) {
                            weights.put(rcombineKey, weights.get(rcombineKey) + 1);
                        } else {
                            weights.put(combineKey, 1);
                        }
                    }
                    lastKey = currentKey;
                    lastPosition = i;
                }
            }
            sent.setTokens(tokenArray);
            sent.setTags(tagArray);
            sent.setKeys(keyArray);
            sentences.add(sent);
            lastPosition = -1;
        }
        for (String s : weights.keySet()) {
//            System.out.println("DBG S " + s);
            String key[] = s.split("/");
            String key0 = key[0];
            String key1 = key[1];

            if (!graph.containsKey(key0)) {
                graph.put(key[0], new Node(key[0]));
            }
            graph.get(key[0]).getEdges().add(new Edge(new Node(key[1]), weights.get(s)));
            if (!graph.containsKey(key1)) {
                graph.put(key[1], new Node(key[1]));
            }
            graph.get(key[1]).getEdges().add(new Edge(new Node(key[0]), weights.get(s)));
        }
        return graph;
    }

    public void runTextRank(Graph graph) {
        double d = 0.85;
        int maxIterations = graph.size();
        while (maxIterations > 0) {
            for (Node node : graph.values()) {
                double rank = (1 - d);
                double aux = 0;
                for (Edge edge : node.getEdges()) {
                    double we = edge.getWeight();
                    double sum = 0;
                    Node n = graph.get(edge.getNode().getKey());
                    for (Edge e : n.getEdges()) {
                        sum += e.getWeight();
                    }
                    aux += (we / sum) * n.getRank();
                }
                rank += d * aux;
                node.setRank(rank);
            }
            maxIterations--;
        }
    }

    public List<Keyword> getKeywords(Graph graph) {
        List<Keyword> keywords = new ArrayList<Keyword>();
        Map<String, Double> keys = new TreeMap<String, Double>();

        for (Node node : graph.values()) {
            keywords.add(new Keyword(node.getKey(), node.getRank()));
        }
        Collections.sort(keywords);
        System.out.println("DBG SIZE1 " + keywords.size());
        for (Keyword k : keywords) {
            System.out.println("DBG " + k.getValue() + " " + k.getRank());
        }
        //int lim = (int) (keywords.size() * 0.15);
        int lim = keywords.size() / (int) (keywords.size() * 0.05);

        for (int i = 0; i < lim; i++) {
            keys.put(keywords.get(i).getValue(), keywords.get(i).getRank());
        }
        System.out.println("DBG SIZE2 " + keys.size());
        for (String key : keys.keySet()) {
            System.out.println("\tDBG KEYS " + key + " " + keys.get(key));
        }

        keywords.clear();

        for (Sentence sentence : sentences) {
            //List<Keyword> kl = sentence.getCollocations(language, keys);
            List<Keyword> kl = sentence.getCollocations2(language, keys);
            keywords.addAll(kl);
        }
        // Delete equals elements
        List<Keyword> ans = new ArrayList<Keyword>();
        for (int i = 0; i < keywords.size(); i++) {
//            System.out.println("DBG " + keywordHandleList.get(i).getValue());
            boolean eq = false;
            for (int j = i + 1; j < keywords.size(); j++) {
                if (keywords.get(i).getValue().trim().equals(keywords.get(j).getValue().trim())) {
                    eq = true;
                    break;
                }
            }
            if (!eq) {
                ans.add(keywords.get(i));
            }
        }
        Collections.sort(ans);
        return ans;
    }
}