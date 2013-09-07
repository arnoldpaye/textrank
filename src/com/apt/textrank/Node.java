package com.apt.textrank;

import java.util.HashSet;
import java.util.Set;

/**
 * @project textrank
 * @package com.apt.textrank
 * @class Node.java (UTF-8)
 * @date 06/09/2013
 * @author Arnold Paye
 */
public class Node {

    /* Members */
    private String value;
    private String key;
    private int id;
    private double rank;
    private Set<Node> edges;

    /* Getters and Setters */
    public String getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }

    public int getId() {
        return id;
    }

    public double getRank() {
        return rank;
    }

    public void setRank(double rank) {
        this.rank = rank;
    }

    public Set<Node> getEdges() {
        return edges;
    }

    /**
     * Constructor with three parameters.
     *
     * @param value
     */
    public Node(String value, String key, int id) {
        this.value = value;
        this.key = key;
        this.id = id;
        this.edges = new HashSet<Node>();
    }
}
