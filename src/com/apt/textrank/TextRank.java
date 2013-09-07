package com.apt.textrank;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @project textrank
 * @package com.apt.textrank
 * @class TextRank.java (UTF-8)
 * @date 05/09/2013
 * @author Arnold Paye
 */
public class TextRank {

    /* Members */
    private String text;
    private Language language;
    private List<Sentence> sentences;

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
    public TextRank(String text, String pathResources) throws IOException {
        this.text = text;
        language = new Language(pathResources);
        sentences = new ArrayList<Sentence>();
    }

    /**
     * Build graph.
     *
     * @return
     */
    public Graph buildGraph() {
        Graph graph = new Graph();
        Node lastNode = null;
        Node currentNode = null;
        int idCont = 0;
        for (String sentence : language.splitParagraph(text)) {
            Sentence sent = new Sentence(sentence);
            String[] tokenList = language.tokenizeSentence(sentence);
            String[] tagList = language.tagTokens(tokenList);
            String[] keyList = new String[tokenList.length];
            for (int i = 0; i < tokenList.length; i++) {
                keyList[i] = language.getKey(tokenList[i], tagList[i]);
                if (language.isRelevant(tagList[i].trim()) && !tokenList[i].trim().isEmpty()) {
                    if (!graph.getNodes().containsKey(keyList[i])) {
                        currentNode = new Node(tokenList[i], keyList[i], idCont++);
                        graph.getNodes().put(keyList[i], currentNode);
                    } else {
                        currentNode = graph.getNodes().get(keyList[i]);
                    }
                    if (lastNode != null) {
                        graph.getNodes().get(currentNode.getKey()).getEdges().add(lastNode);
                        graph.getNodes().get(lastNode.getKey()).getEdges().add(currentNode);
                    }
                    lastNode = currentNode;
                }
            }
            sent.setTokens(tokenList);
            sent.setTags(tagList);
            sent.setKeys(keyList);
            sentences.add(sent);
        }
        return graph;
    }

    /**
     * Build adjacency matrix.
     *
     * @param graph
     * @return
     */
    private double[][] buildAdjacencyMatrix(Graph graph) {
        double[][] A = new double[graph.getNodes().size()][graph.getNodes().size()];
        for (Node node : graph.getNodes().values()) {
            for (Node edge : node.getEdges()) {
                A[node.getId()][edge.getId()] = 1;
                A[edge.getId()][node.getId()] = 1;

            }
        }
        return A;
    }

    /**
     * Build transition probability matrix. Assumptions: A is a square matrix,
     * alpha is between 0 and 1.
     *
     * @param A adjacency matrix of the graph.
     * @param alpha probability of the teleport operation.
     * @return
     */
    private double[][] buildTransitionProbabilityMatrix(double[][] A, double alpha) {
        double aux[] = new double[A.length];
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A[i].length; j++) {
                if (A[i][j] > 0) {
                    aux[i]++;
                }
            }
        }
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A[i].length; j++) {
                if (aux[i] > 0) {
                    A[i][j] = ((A[i][j] / aux[i]) * (1 - alpha)) + (alpha / A.length);
                } else {
                    A[i][j] = 1 / A.length;
                }
            }
        }
        return A;
    }

    /**
     * Naive algorithm to check whether the iteration converges.
     *
     * @param xt1
     * @param xt2
     * @return
     */
    private boolean converges(double xt1[], double xt2[]) {
        double error = 0.000001;
        for (int i = 0; i < xt1.length; i++) {
            if (Math.abs(xt1[i] - xt2[i]) > error) {
                return false;
            }
        }
        return true;
    }

    /**
     * PageRank algorithm based on the book "An Introduction to Information
     * Retrieval" of Christopher D. Manning et. al. Assumptions: A is a square
     * matrix.
     *
     * @param graph text graph
     * @return vector of pagerank
     */
    public double[] pageRank(Graph graph) {
        double[][] A = buildAdjacencyMatrix(graph);
        double pr[] = new double[A.length];
        for (int i = 0; i < pr.length; i++) {
            pr[i] = 1;
        }
        double P[][] = buildTransitionProbabilityMatrix(A, 0.56);
        int cont = 0;
        while (true) {
            double aux[] = new double[pr.length];
            for (int j = 0; j < P.length; j++) {
                for (int i = 0; i < P.length; i++) {
                    aux[j] += (pr[i] * P[i][j]);
                }
            }
            if (!converges(aux, pr) && cont < 100) {
                cont++;
                pr = aux;
            } else {
                break;
            }
        }
        return pr;
    }

    /**
     * Get keywords.
     * @param pr
     * @param graph
     * @return 
     */
    public List<Keyword> getKeywords(double[] pr, Graph graph) {
        List<Keyword> keywords = new ArrayList<Keyword>();
        // First, keywords contain the list of keys for sorting.
        for (int i = 0; i < pr.length; i++) {
            for (Node node : graph.getNodes().values()) {
                if (node.getId() == i) {
                    node.setRank(pr[i]);
                    keywords.add(new Keyword(node.getKey(), node.getRank()));
                    break;
                }
            }
        }
        Collections.sort(keywords);
        // Second, create a list of string keys.
        List<String> keys = new ArrayList<String>();
        for (Keyword keyword : keywords) {
//            System.out.println("DBG KEYWORD " + keyword.getValue() + " " + keyword.getRank());
            // TODO: Determine this value.
            if (keyword.getRank() > 1.2) {
                keys.add(keyword.getValue());
            }
        }
        // Iterate sentences for the collocations
        keywords.clear();
        for (Sentence sentence : sentences) {
            List<Keyword> kl = sentence.getCollocations(keys);
            for (Keyword k : kl) {
                keywords.add(k);
            }
        }
//        System.out.println("DBG KEYS " + keys);
        return keywords;
    }
}