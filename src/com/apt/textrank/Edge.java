package com.apt.textrank;

/**
 * @project textrank
 * @package com.apt.textrank
 * @class Edge.java (UTF-8)
 * @date 04/10/2013
 * @author Arnold Paye
 */
public class Edge {

    /* Members */
    private Node node;
    private int weight;

    /* Getters and setters */
    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
    
    public Edge(Node node, int weight) {
        this.node = node;
        this.weight = weight;
    }
}
