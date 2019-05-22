# Package Challenge 2019

An Approach to solve packaging problem to achieve a sequence of items in which values are maximized and weights are minimized.

---

## Introduction
Packing problem is a class of optimization problem and it aims to expand the value of items in a package 
while satisfying a set of constraints:
 
- The sum of weight in selected items should not surpassing the package capacity.
- Maximum weight of and item should be less than or equal to 100.
- Maximum cost of and item should be less than or equal to 100.
- There might only up to 15 items to select the sequence.

Following Item will be discussed in following sections:

- Algorithm and Time Complexity
- Data Structure
- Design Patterns


---
##Algorithm and Time Complexity

Packaging problem is a reformulation of 01 knapsack problem which is proved to be NP-Complete,thus there is no
known algorithm which always gives optimal solution and be fast on all cases.

In my approach I have chosen to follow an optimized Dynamic Programming (DP) approach with domination pruning, in which
I have tried to save space and number of computation needed to find the solution.

This algorithm is multi staged, and in each stage a new set will be generated based on choosing an item and it will calculate
its contribution to the items chosen in previous stages (adding its cost and weight). while processing dominated items will be 
pruned so that there would not be the need to persist and computation in the next rounds.

Pruning a dominated item is defined as to purge any element whose weight is same or higher and its cost is same or lower than another item.

In the implementation an item is defined as a Triplet as there is three elements related to and item e.g. (id,weight,cost).

There are two major passes in this algorithm:
- Forward pass: generating sets where sets contain cumulative (weight, cost) of item chosen. Forward pass include two operations:
    * Extend: in this step an item selected and at its cost and weight to all triplets result form merge operation in last round.
    * Merge: extended set will be merged with previous cumulative set with dominance pruning to reach all possible selecting items to find solution later on.
- Backward pass: Traverses cumulative sets back to source to find optimal solution.

For example imaging here is the table to run the algorithm on with max capacity 9:

| item | weight | cost |
|------|--------|------|
|  0   |   0    |   0  |
|  1   |   2    |   1  |
|  2   |   3    |   2  |
|  3   |   4    |   5  |

* item 0 is a sentinel added in the first of algorithm.

CS = Cumulative Set and ES = Extended Set

**FORWARD PASS:**

- CS(0) = (0,0)
    * ES = (2,1)                      #ES elements adds to all tuples in CS(0)
- CS(1) = (0,0)(2,1)                  #CS(n) is merged CS(n-1) and ES
    * ES = (3,2)(5,3)
- CS(2) = (0,0)(2,1)(3,2)~~[(5,3)]~~
    * ES = (4,5)(6,6)(7,7)(9,8)
- CS(3) = (0,0)(2,1)(3,2)(4,5)(6,6)(7,7)(9,8)  # item [(5,3)] is dominated by (4,5) because of less weight and more value,it wont be merged in this CS

**BACKWARD PASS:**

Starting from last item in last CS:
- if pair exists in previous CS, item not in solution (it means it did not contribute anything).
- if pair not in previous CS, item is in solution.
    * for next round subtract weight,cost of item n when in round n from item not found and continue trace back to find subtracted pair in previous set.
    
_Consideration:_

* This algorithm only interested in CS and not in ES, so only CS sets will be kept.

* In the implantation version of the algorithm first of all and before execution triplet items is sorted in descending order of cost/weight
, to keep fittest items earlier to allow pruning.


## Data Structure
The data structure chosen in the algorithm to keep triplets is ArrayList and the reasons are :
 * CumulativSets (or CS described in algorithm section) are varying and ArrayList is dynamic in size.
 * Get and add operation in ArrayList is O(1)
 
## Design Pattern

 There are mainly two design pattern used in this approach: Singleton and Strategy pattern
 * _**Parser**_ follows Singleton design pattern
 * _**CumulativePacking**_ follows Strategy design pattern.
 * _**NewLineFormatter**_ follows Strategy design pattern.

Apart from these DPs, a Layer architecture approached has been followed in code organization.

  