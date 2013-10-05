package com.apt.textrank;

import java.util.ArrayList;
import java.util.List;

/**
 * @project textrank
 * @package com.apt.textrank
 * @class Node.java (UTF-8)
 * @date 04/10/2013
 * @author Arnold Paye
 */
public class Node {

    /* Members */
    private String key;
    private double rank;
    private List<Edge> edges;

    /*Getters and setters*/
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public double getRank() {
        return rank;
    }

    public void setRank(double rank) {
        this.rank = rank;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }
    
    public Node() {
        this.key = "";
        edges = new ArrayList<Edge>();
    }
    
    public Node(String key) {
        this.key = key;
        this.rank = 1;
        edges = new ArrayList<Edge>();
    }
}
