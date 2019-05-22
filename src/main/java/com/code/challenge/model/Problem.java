package com.code.challenge.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Container to hold data parsed form input file.
 */
public class Problem {
    private List<Triplet> triplets;
    private int maxCapacity;

    public Problem(int maxCapacity) {
        this.maxCapacity = maxCapacity;
        triplets = new ArrayList<>();
    }

    public Problem(int maxCapacity, List<Triplet> triplets) {
        this.maxCapacity = maxCapacity;
        this.triplets = triplets;
    }


    public List<Triplet> getTriplets() {
        return triplets;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    @Override
    public String toString() {
        return "Problem{" +
                "maxCapacity=" + maxCapacity +
                ", triplets=" + triplets +
                '}';
    }
}

