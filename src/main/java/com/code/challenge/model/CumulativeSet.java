package com.code.challenge.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Set Created as a result of merge operation.
 */
public class CumulativeSet {
    private List<Triplet> triplets;
    private final int maximumCapacity;

    public CumulativeSet(List<Triplet> triplets, int maximumCapacity) {
        this.triplets = triplets;
        this.maximumCapacity = maximumCapacity;
    }

    public CumulativeSet(int maximumCapacity) {
        this.maximumCapacity = maximumCapacity;
        this.triplets = new ArrayList<>();
    }

    public List<Triplet> getTriplets() {
        return triplets;
    }

    public int getMaximumCapacity() {
        return maximumCapacity;
    }

    /**
     * Check if given Triplet exists in the list of triplets in this set.
     *
     * @param triplet triplet to compare others with.
     * @return true if exists, otherwise false.
     */
    public boolean exists(Triplet triplet){

        boolean found = false;
        int cursor= this.getTriplets().size()-1;
        for (int j= cursor; j >= 0; j--) {
            Triplet currItem= this.getTriplets().get(j);
            if (currItem.equals(triplet)) {
                found= true;
                break;
            }
            if (currItem.getWeight() < triplet.getWeight())
                break;
        }
        return found;
    }

    @Override
    public String toString() {
        return "CumulativeSet{" +
                "maximumCapacity=" + maximumCapacity +
                ", triplets=" + triplets +
                '}';
    }
}
