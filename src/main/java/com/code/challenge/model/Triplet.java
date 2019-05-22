package com.code.challenge.model;

import java.util.Objects;

/**
 * A Triplet represents an item in a package.
 */
public class Triplet {
    private final int id;
    private final float weight;
    private final int cost;

    public Triplet(float weight, int cost) {
        this(0, weight, cost);
    }

    public Triplet(int id, float weight, int cost) {
        this.id = id;
        this.weight = weight;
        this.cost = cost;
    }

    public int getId() {
        return id;
    }

    public float getWeight() {
        return weight;
    }

    public int getCost() {
        return cost;
    }

    public float getRatio() {
        return this.cost / this.weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Triplet triplet = (Triplet) o;
        return  Float.compare(triplet.weight, weight) == 0 &&
                cost == triplet.cost;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, weight, cost);
    }

    @Override
    public String toString() {
        return "Triplet[" +
                "id=" + id +
                ", weight=" + weight +
                ", cost=" + cost +
                ']';
    }
}
