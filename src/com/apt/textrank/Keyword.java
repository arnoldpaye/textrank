package com.apt.textrank;

/**
 * @project textrank
 * @package com.apt.textrank
 * @class Keyword.java (UTF-8)
 * @date 06/09/2013
 * @author Arnold Paye
 */
public class Keyword implements Comparable<Keyword> {
    
    /* Members */
    private String value;
    private double rank;
    
    /* Getters and Setters */
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public double getRank() {
        return rank;
    }

    public void setRank(double rank) {
        this.rank = rank;
    }
    
    /**
     * Constructor with two parameters.
     * @param value
     * @param rank 
     */
    public Keyword(String value, double rank) {
        this.value = value;
        this.rank = rank;
    }

    /**
     * 
     * @param that
     * @return 
     */
    @Override
    public int compareTo(Keyword that) {
        if (this.getRank() > that.getRank()) {
            return -1;
        } else if (this.getRank() < that.getRank()) {
            return 1;
        } else {
            return 0;
        }
    }
}