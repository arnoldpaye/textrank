package com.apt.textrank;

import java.util.HashMap;
import java.util.Map;

/**
 * @project textrank
 * @package com.apt.textrank
 * @class Graph.java (UTF-8)
 * @date 06/09/2013
 * @author Arnold Paye
 */
public class Graph {
    
    /* Members */
    private Map<String, Node> nodes;
    
    /* Getters and Setters */
    public Map<String, Node> getNodes() {
        return nodes;
    }
    
    /**
     * Default constructor.
     */
    public Graph() {
        nodes = new HashMap<String, Node>();
    }
}