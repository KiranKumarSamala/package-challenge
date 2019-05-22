package com.code.challenge.service;

import com.code.challenge.model.Problem;
import com.code.challenge.model.Triplet;

import java.util.List;

/**
 * Define Packing Strategy Protocol to be implemeneted by Concrete classes
 */
public interface Packing {
    List<Triplet> getOptimalTriplets(Problem problem);

    String getOptimalItemIdsInString(Problem problem);
}
