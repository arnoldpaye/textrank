package com.apt.textrank;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
        for (String sentence : language.splitParagraph(text)) {
            Sentence sent = new Sentence(sentence);
            String[] tokenArray = language.tokenizeSentence(sentence);
            String[] tagArray = language.tagTokens(tokenArray);
            String[] keyArray = new String[tokenArray.length];
            for (int i = 0; i < tokenArray.length; i++) {
                keyArray[i] = language.getKey(tokenArray[i], tagArray[i]);
                if (language.isRelevant(tagArray[i].trim()) && !tokenArray[i].trim().isEmpty() && !tokenArray[i].equals("(") && !tokenArray[i].equals(")")) {
                    currentKey = keyArray[i];
                    if (!lastKey.isEmpty()) {
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
                }
            }
            sent.setTokens(tokenArray);
            sent.setTags(tagArray);
            sent.setKeys(keyArray);
            sentences.add(sent);
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
//        List<String> keysOld = new ArrayList<String>();
        Map<String, Double> keys = new HashMap<String, Double>();
        
        for (Node node : graph.values()) {
            keywords.add(new Keyword(node.getKey(), node.getRank()));
        }
        Collections.sort(keywords);
//        for (Keyword k : keywords) {
//            System.out.println("DBG " + k.getValue() + " " + k.getRank());
//        }

        int lim = (int) (keywords.size() * 0.15);

        for (int i = 0; i < lim; i++) {
//            keysOld.add(keywords.get(i).getValue());
            keys.put(keywords.get(i).getValue(), keywords.get(i).getRank());
        }
//        System.out.println("DBG SIZE1 " + keysOld.size());
        System.out.println("DBG SIZE2 " + keys.size());
        for (String key : keys.keySet()) {
            System.out.println("DBG KEYS " + key + " " + keys.get(key));
        }
        
        keywords.clear();

        for (Sentence sentence : sentences) {
            List<Keyword> kl = sentence.getCollocations(language, keys);

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
//    public Graph buildGraph() {
//        Graph graph = new Graph();
//        Node lastNode = null;
//        Node currentNode = null;
//        int idCont = 0;
//        for (String sentence : language.splitParagraph(text)) {
//            Sentence sent = new Sentence(sentence);
//            String[] tokenList = language.tokenizeSentence(sentence);
//            String[] tagList = language.tagTokens(tokenList);
//            String[] keyList = new String[tokenList.length];
//            for (int i = 0; i < tokenList.length; i++) {
//                keyList[i] = language.getKey(tokenList[i], tagList[i]);
//                // TODO: Change this, dont hardcode
//                if (language.isRelevant(tagList[i].trim()) && !tokenList[i].trim().isEmpty() && !tokenList[i].equals("(") && !tokenList[i].equals(")")) {
//                    if (!graph.getNodes().containsKey(keyList[i])) {
//                        currentNode = new Node(tokenList[i], keyList[i], idCont++);
//                        graph.getNodes().put(keyList[i], currentNode);
//                    } else {
//                        currentNode = graph.getNodes().get(keyList[i]);
//                    }
//                    if (lastNode != null) {
//                        graph.getNodes().get(currentNode.getKey()).getEdges().add(lastNode);
//                        graph.getNodes().get(lastNode.getKey()).getEdges().add(currentNode);
//                    }
//                    lastNode = currentNode;
//                }
//            }
//            sent.setTokens(tokenList);
//            sent.setTags(tagList);
//            sent.setKeys(keyList);
//            sentences.add(sent);
//        }
//        return graph;
//    }
    /**
     * Get keywords.
     *
     * @param pr
     * @param graph
     * @return
     */
//    public List<Keyword> getKeywords(double[] pr, Graph graph) throws FileNotFoundException, IOException {
//        List<Keyword> keywords = new ArrayList<Keyword>();
//        List<String> keys = new ArrayList<String>();
//        List<Keyword> keywordHandleList = new ArrayList<Keyword>();
//        statistics.clear();
//        Double limRank = pr[0];
//        // First, keywords contain the list of keys for sorting.
//        for (int i = 0; i < pr.length; i++) {
//            statistics.addValue(pr[i]);
//            for (Node node : graph.getNodes().values()) {
//                if (node.getId() == i) {
//                    node.setRank(pr[i]);
//                    keywords.add(new Keyword(node.getKey(), node.getRank()));
//                    if (Math.abs(pr[i] - limRank) < 0.005 && (node.getKey().startsWith("NC") || node.getKey().startsWith("NP"))) {
//                        keywordHandleList.add(new Keyword(node.getValue().toUpperCase(), 0));
//                    }
//                    break;
//                }
//            }
//        }
//        Collections.sort(keywords);
////        System.out.println("DBG SIZE " + keywords.size());
//        int lim = (int)(keywords.size() * 0.15);
//        System.out.println("DBG LIM " + lim);
//        
//        for (int i = 0; i < lim; i++) {
//            keys.add(keywords.get(i).getValue());
////            System.out.println("\t\t\t" + keywords.get(i).getValue());
//        }
//        
////        System.out.println("DBG SIZE " + keys.size());
//        
//        // Iterate sentences for the collocations
//        for (Sentence sentence : sentences) {
////            System.out.println("DBG SENTENCE " + sentence.getText());
//            List<Keyword> kl = sentence.getCollocations(language, keys);
//
//            keywordHandleList.addAll(kl);
//        }
////        System.out.println("DBG KEYS " + keys);
//        // Delete equals elements
//        List<Keyword> ans = new ArrayList<Keyword>();
//        for (int i = 0; i < keywordHandleList.size(); i++) {
////            System.out.println("DBG " + keywordHandleList.get(i).getValue());
//            boolean eq = false;
//            for (int j = i + 1; j < keywordHandleList.size(); j++) {
//                if (keywordHandleList.get(i).getValue().trim().equals(keywordHandleList.get(j).getValue().trim())) {
//                    eq = true;
//                    break;
//                }
//            }
//            if (!eq) {
//                ans.add(keywordHandleList.get(i));
//            }
//        }
//        return ans;
//    }
}