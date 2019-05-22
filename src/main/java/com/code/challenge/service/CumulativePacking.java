package com.code.challenge.service;

import com.code.challenge.utility.Util;
import com.code.challenge.exception.APIException;
import com.code.challenge.model.CumulativeSet;
import com.code.challenge.model.Problem;
import com.code.challenge.model.Triplet;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Concrete class to solve packing problem with Cumulative Approach.
 */
public class CumulativePacking implements Packing {

    Merger merger;

    public static final int MAX_TRIPLET_WEIGHT = 100;
    public static final int MAX_TRIPLET_COST = 100;
    public static final int MAX_TRIPLETS_SIZE_IN_PROBLEM = 15;

    public CumulativePacking() {
        this.merger = new Merger();
    }

    /**
     * Solve Given problem and produce optimal item sequence as list of Triplets.
     *
     * @param problem
     * @return optimal triplets list
     */
    @Override
    public List<Triplet> getOptimalTriplets(Problem problem) {

        this.validateProblem(problem);
        List<CumulativeSet> sets = this.buildCumulativeSets(problem);
        List<Triplet> optimalTriplets = this.findOptimalTripletsInCumulativeSets(problem, sets);

        return optimalTriplets;
    }

    /**
     * Solve Given problem and produce optimal item sequence in a String.
     *
     * @param problem Includes package capacity and list of items with their costs and weight.
     * @return Selected triplet item ids in a comma delimited string and - for empty strings.
     */
    @Override
    public String  getOptimalItemIdsInString(Problem problem) {
        List<Triplet> optimalTriplets = this.getOptimalTriplets(problem);
        String output = optimalTriplets.stream()
                .map(Triplet::getId)
                .map(String::valueOf)
                .collect(Collectors.joining(", "));

        return Util.defaultIfEmpty(output, "-");
    }

    /**
     * Check a problem to have valid cost, weight and right number of items defined.
     * @param problem Includes package capacity and list of items with their costs and weight.
     */
    private void validateProblem(Problem problem) {
         this.validateTripletCosts(problem);
         this.validateTripletWeights(problem);
         this.validateTripletMaxSize(problem);
    }

    /**
     * validate right number of items be available in a problem.
     * @param problem Includes package capacity and list of items with their costs and weight.
     */
    private void validateTripletMaxSize(Problem problem) {
        if (problem.getTriplets().size() > MAX_TRIPLETS_SIZE_IN_PROBLEM)
            throw new APIException(
                    String.format("Invalid problem : Max items possible is %d", MAX_TRIPLETS_SIZE_IN_PROBLEM));

    }

    /**
     * Validate all items weight values to be in valid range.
     * @param problem Includes package capacity and list of items with their costs and weight.
     */
    private void validateTripletWeights(Problem problem) {
        boolean anyInvalidWeight =  problem.getTriplets()
                .stream()
                .map(Triplet::getWeight)
                .anyMatch(weight -> weight > MAX_TRIPLET_WEIGHT || weight <= 0.0f);

        if (anyInvalidWeight)
            throw new APIException(
                    String.format("Invalid problem : Max item weight possible is %d", MAX_TRIPLET_WEIGHT));

    }

    /**
     * Validate all items cost values to be in valid range.
     * @param problem Includes package capacity and list of items with their costs and weight.
     */
    private void validateTripletCosts(Problem problem) {
        boolean anyInvalidCost = problem.getTriplets()
                .stream()
                .map(Triplet::getCost)
                .anyMatch(cost -> cost > MAX_TRIPLET_COST || cost <= 0);

        if (anyInvalidCost)
            throw new APIException(
                    String.format("Invalid problem : Max item cost possible is %d", MAX_TRIPLET_COST));
    }

    /**
     * Create Cumulative Sets from triplet Items in a Problem.
     * This Approach consists of two major operations: extend and merge
     * <p>
     * extend operation includes a Triplet Item from given problem and
     * adds it's cost and value to the triplets chosen in the previous
     * round (previous cumulativeSet).
     * <p>
     * merge operation merges the result of extended set with the cumulative set
     * in the last round and create a new cumulative set which includes
     * a Cumulative Set including selected items in previous rounds and
     * the new one in extend operation.
     *
     * @param problem Includes package capacity and list of items with their costs and weight.
     * @return
     */
    private List<CumulativeSet> buildCumulativeSets(Problem problem) {

        problem.getTriplets().add(new Triplet(0, 0, 0));
        this.sortProblemTripletsWithRatio(problem);
        List<CumulativeSet> sets = this.getInitializedCumulativeSets(problem);
        this.removeOverCapacityTriplets(problem);


        for (int i = 1; i < problem.getTriplets().size(); i++) {
            CumulativeSet currentSet = sets.get(i - 1);
            List<Triplet> extendedSet = this.extend(currentSet, problem.getTriplets().get(i));
            List<Triplet> mergedTriplets = merger.merge(currentSet.getTriplets(), extendedSet);
            sets.add(new CumulativeSet(mergedTriplets, currentSet.getMaximumCapacity()));
        }

        return sets;
    }

    /**
     * finds optimal triplet items which results in a maximized packages gain cost.
     *
     * @param problem includes package capacity and list of items with their costs and weight.
     * @param sets    list of cumulative sets resulted from  buildCumulativeSets method.
     * @return Selected optimal triplet items can be chosen to maximise profit.
     */
    private List<Triplet> findOptimalTripletsInCumulativeSets(Problem problem, List<CumulativeSet> sets) {
        int lastSetIndex = sets.size() - 1;    // Start at last set
        int lastSetItem = sets.get(lastSetIndex).getTriplets().size() - 1;    // Get last item
        Triplet lastItem = sets.get(lastSetIndex).getTriplets().get(lastSetItem);
        List<Triplet> solution = new ArrayList<>();

        int cumulativeCost = lastItem.getCost();
        float cumulativeWeight = lastItem.getWeight();
        Triplet prevTriplet = lastItem;

        for (int i = lastSetIndex - 1; i >= 0; i--) {
            int prevSetIndex = i + 1;
            CumulativeSet currSet = sets.get(i);
            boolean found = currSet.exists(prevTriplet);
            // Pair (cum wgt, cum profit) not found in preceding set; item is in solution
            if (!found) {
                solution.add(problem.getTriplets().get(prevSetIndex));
                cumulativeCost -= problem.getTriplets().get(prevSetIndex).getCost();
                cumulativeWeight = Util.round(cumulativeWeight - problem.getTriplets().get(prevSetIndex).getWeight());
                prevTriplet = new Triplet(cumulativeWeight, cumulativeCost);
            }    // else keep searching for prev item in the next set
        }
        return solution;
    }

    /**
     * Sort Triplet items in a problem based on ratio  cost/weight and the reverse the list.
     * this way most valuable package with less weight would be in the head of the list.
     *
     * @param problem includes package capacity and list of items with their costs and weight.
     */
    private void sortProblemTripletsWithRatio(Problem problem) {
        problem.getTriplets().sort(Comparator.comparing(Triplet::getRatio).reversed());
    }

    /**
     * Initialize Cumulative set with sentinel triplet item (0,0,0).
     *
     * @param problem includes package capacity and list of items with their costs and weight.
     * @return Initialized Cumulative sets with sentinel triplet.
     */
    private List<CumulativeSet> getInitializedCumulativeSets(Problem problem) {
        List<CumulativeSet> sets = new ArrayList<>();

        CumulativeSet cumulativeSet = new CumulativeSet(problem.getMaxCapacity());
        cumulativeSet.getTriplets().add(problem.getTriplets().get(0));
        sets.add(cumulativeSet);
        return sets;
    }

    /**
     * Prune Triplet items with weight exceeding the capacity defined in problem.
     *
     * @param problem includes package capacity and list of items with their costs and weight.
     */
    private void removeOverCapacityTriplets(Problem problem) {
        problem.getTriplets().removeIf(t -> t.getWeight() > problem.getMaxCapacity());
    }

    /**
     * add Given A triplet, which represents an item in the package, to a cumulative set
     * to include the impact of deciding to choose an item.
     * The result sequence would be used to build new Cumulative Set which includes
     * the decision of including given triplet item.
     *
     * @param set     a set generated including all items before this one.
     * @param triplet represents an item in a package.
     * @return extended list including given item.
     */
    private List<Triplet> extend(CumulativeSet set, Triplet triplet) {
        List<Triplet> extendedTriplets = set.getTriplets().stream()
                .map(t -> this.add(t, triplet))
                .filter(t -> this.validCapacity(t, set.getMaximumCapacity()))
                .collect(Collectors.toCollection(ArrayList::new));
        return extendedTriplets;
    }

    /**
     * Validate items wights to be under maximum capacity defined in a problem
     *
     * @param triplet         represents an item in a package.
     * @param maximumCapacity maximum capacity a package can take,defined in a problem.
     * @return true if item weight is less than or equal maximum capacty,otherwise false.
     */
    private boolean validCapacity(Triplet triplet, int maximumCapacity) {
        if (triplet.getWeight() <= maximumCapacity)
            return true;

        return false;
    }

    /**
     * add two triplets costs and weights and return new triplet item.
     *
     * @param triplet1 represents an item in a package.
     * @param triplet2 represents an item in a package.
     * @return new triplet result from adding given two triplets.
     */
    private Triplet add(Triplet triplet1, Triplet triplet2) {
        int cumulativeCost = triplet1.getCost() + triplet2.getCost();
        float cumulativeWeight = triplet1.getWeight() + triplet2.getWeight();

        return new Triplet(triplet2.getId(), cumulativeWeight, cumulativeCost);
    }

    /**
     * Merge a List of Triplets from a Cumulative set with a List extended by
     * choosing a triplet item using dominance pruning mechanism.
     */
    private class Merger {

        private int firstPointer;
        private int firstMaxIndex;
        private int firstLastItemCost;

        private int secondPointer;
        private int secondMaxIndex;
        private int secondLastItemCost;
        List<Triplet> firstTriplets;
        List<Triplet> secondTriplets;

        /**
         * Initializes pointers, max index and last item cost for first and second triplet items.
         *
         * @param firstTriplets represents a list of items in a package.
         * @param secondTriplets represents a list of items in a package.
         */
        public void initialize(List<Triplet> firstTriplets, List<Triplet> secondTriplets) {

            firstPointer = 0;
            firstMaxIndex = firstTriplets.size() - 1;
            firstLastItemCost = firstTriplets.get(firstMaxIndex).getCost();

            secondPointer = 0;
            secondMaxIndex = secondTriplets.size() - 1;
            secondLastItemCost = secondTriplets.get(secondMaxIndex).getCost();

            this.firstTriplets = firstTriplets;
            this.secondTriplets = secondTriplets;
        }

        /**
         * In merge operation Items would be merged in a ascending order of weights with domination:
         * <p>
         * Domination is when one item has less weight and higher cost comparing to the other.
         * <p>
         * cases:
         * if item1 weight < other: write item1 to result and move second pointer until can not be dominated.
         * else if weights are equal move pointer for item dominated.
         * else if item1 weight > other: same logic holds for item2 in the first case.
         *
         * @param firstTriplets  calculated cumulative triplet to include all items in the previous rounds.
         * @param secondTriplets an extended triplets in which one specific item added contributing to total weights/cost.
         * @return new merged list of triplet.
         */
        public List<Triplet> merge(List<Triplet> firstTriplets, List<Triplet> secondTriplets) {

            this.initialize(firstTriplets, secondTriplets);
            List<Triplet> result = new ArrayList<>();

            while (arePointersNotTraversedCompletely()) {
                if (areBothPointersInRange()) {
                    Triplet firstTriplet = this.firstTriplets.get(firstPointer);
                    Triplet secondTriplet = this.secondTriplets.get(secondPointer);

                    if (firstTriplet.getWeight() < secondTriplet.getWeight()) {
                        result.add(firstTriplet);    // Add item; can't be dominated by other item
                        firstPointer++;
                        moveSecondPointerUntilNotDominated(firstTriplet, secondTriplet);

                    } else if (firstTriplet.getWeight() == secondTriplet.getWeight()) {
                        moveDominatedPointerInCaseEqualWeights(firstTriplet, secondTriplet);

                    } else {
                        result.add(secondTriplet);    //  Add other item, can't be dominated by item
                        secondPointer++;
                        moveFirstPointerInCaseEqualWeights(firstTriplet, secondTriplet);
                    }
                } else if (firstPointer > firstMaxIndex) {    // Only other items left to consider
                    addSecondTripletToResultIfNotDominated(result);
                } else {    // indexOther > maxIndexOther. Only items left to consider
                    addFirstTripletToResultIfNotDominated(result);
                }

            }
            return result;
        }

        /**
         * Logic holds for the first case in merge operation:
         * if item1 weight < other: write item1 to result and move second pointer until can not be dominated.
         *
         * @param result result set to add item when possible.
         */
        private void addFirstTripletToResultIfNotDominated(List<Triplet> result) {
            while (firstPointer <= firstMaxIndex) {
                Triplet firstTriplet = firstTriplets.get(firstPointer);
                if (firstTriplet.getCost() > secondLastItemCost)
                    result.add(firstTriplet);
                firstPointer++;
            }
        }

        /**
         * Logic holds for the third case in merge operation:
         * if item1 weight > other: write item2 to result and move first pointer until can not be dominated.
         *
         * @param result result set to add item when possible.
         */
        private void addSecondTripletToResultIfNotDominated(List<Triplet> result) {
            while (secondPointer <= secondMaxIndex) {
                Triplet secondTriplet = secondTriplets.get(secondPointer);
                if (secondTriplet.getCost() > firstLastItemCost)
                    result.add(secondTriplet);
                secondPointer++;
            }
        }

        /**
         * Move First Pointer until not dominated by second triplet.
         *
         * @param firstTriplet represents a cumulative triplet.
         * @param secondTriplet represents a cumulative triplet.
         */
        private void moveFirstPointerInCaseEqualWeights(Triplet firstTriplet, Triplet secondTriplet) {
            while (firstTriplet.getCost() < secondTriplet.getCost() && firstPointer <= firstMaxIndex) {    // item dominated; skip it
                if (firstPointer == firstMaxIndex) {
                    ++firstPointer;
                    break;
                }
                firstTriplet = firstTriplets.get(++firstPointer);
            }
        }

        /**
         * Logic holds for the second case in merge operation:
         * if weights are equal move pointer for item dominated. no item would add to result as
         * any of them could be dominated in the next round.
         *
         * @param thisTriplet represents a cumulative triplet.
         * @param secondTriplet represents a cumulative triplet.
         */
        private void moveDominatedPointerInCaseEqualWeights(Triplet thisTriplet, Triplet secondTriplet) {
            if (thisTriplet.getCost() >= secondTriplet.getCost())    // Other item dominated
                secondPointer++;
            else
                firstPointer++;                            // Item dominated
        }

        /**
         * Move Second Pointer until not dominated by second triplet.
         *
         * @param firstTriplet represents a cumulative triplet.
         * @param secondTriplet represents a cumulative triplet.
         */
        private void moveSecondPointerUntilNotDominated(Triplet firstTriplet, Triplet secondTriplet) {
            while (secondTriplet.getCost() < firstTriplet.getCost() && secondPointer <= secondMaxIndex) {    // Other item dominated; skip it
                if (secondPointer == secondMaxIndex) {
                    ++secondPointer;
                    break;
                }
                secondTriplet = secondTriplets.get(++secondPointer);
            }
        }

        /**
         * Check if all items in both triplet lists are traversed.
         *
         * @return true if all items traversed, otherwise false.
         */
        private boolean arePointersNotTraversedCompletely() {
            return firstPointer <= firstMaxIndex || secondPointer <= secondMaxIndex;
        }

        /**
         * Check if both lists not traversed completely.
         *
         * @return false if any of the lists traversed, otherwise true.
         */
        private boolean areBothPointersInRange() {
            return firstPointer <= firstMaxIndex && secondPointer <= secondMaxIndex;
        }
    }

}
