Programming Assignment 7: Fraud Detection

/* *****************************************************************************
 *  Describe how you implemented the Clustering constructor
 **************************************************************************** */
We create an EdgeWeightedGraph object where each vertex represents a location.
Let the number of locations be m.
Each pair of distinct vertices is connected, i.e., the graph is complete.
For any pair of distinct vertices v, w, the weight of edge v-w is the squared
distance between v and w.

We then create a KruskalMST object on the EdgeWeightedGraph, from which we
get an iterable of all the edges in the minimum spanning tree. We then use a
MinPQ to get an iterable of the m-k edges of the minimum spanning tree with
lowest weight.

Then, we create a new EdgeWeightedGraph of size m and add the edges from our
new iterable. Lastly, we create a CC object on the new graph and save that as
an instance variable.

/* *****************************************************************************
 *  Describe how you implemented the WeakLearner constructor
 **************************************************************************** */
We first transpose the input array for better cache performance when iterating
on input column-major. We maintain a champion double, being the lowest error
from a particular (d, v, s) triple.

For each dimension:
We first must sort the coordinates. We maintain a netWeight 2D double array of
size 2 x m. This array stores for both s = 0 and s = 1 the "netWeight" of
guessing whether all points with the same coordinate value on the dimension at
issue is either 0 or 1. That is, if you guess 0 at some value, the weight
contribution from that guess is the sum of all the points with that value
whose label is 1, and vice versa. All points with the same coordinate value
for the dimension at issue are bucketed into the same index in the netWeight
array, and the refernce symbol table allows us to do this by saving a
reference index for each distinct coordinate value.

We then initialize an error value for s = 0 and s = 1 where we set the value to
be the minimum coordinate value for the given dimension minus 1, that is, guess
all of them to be 0 or all of them to be 1. This is the 0th value we've tried,
and it is entered into two minPQs as (0, error[0]) in pq0 and (0, error[1]) in
pq1. pq0 and pq1 track the error for s = 0 and s = 1 for the jth value we've
tried so far.

For each value, we increment the s = 0 error by adding the weights of the
points at that value for which the label is 1 and subtracting the weights
for which the labels are 0. We do the reverse for s = 1. This error is then
inserted into the PQs.

We finally check to see if the minimum (d, v, s) triple for this fixed d is
smaller than the smallest error we've seen from all dimensions so far tested.
If so, we make this (d, v, s) triple our instance variables and update our
champion.

/* *****************************************************************************
 *  Consider the large_training.txt and large_test.txt datasets.
 *  Run the boosting algorithm with different values of k and T (iterations),
 *  and calculate the test data set accuracy and plot them below.
 *
 *  (Note: if you implemented the constructor of WeakLearner in O(kn^2) time
 *  you should use the small_training.txt and small_test.txt datasets instead,
 *  otherwise this will take too long)
 **************************************************************************** */

      k          T         test accuracy       time (seconds)
   --------------------------------------------------------------------------
      2         1000        0.60875             1.596
      4         1000        0.80375             2.251
      8         1000        0.94875             3.385
      16        1000        0.96125             4.466
      32        1000        0.97375             5.888
      64        1000        0.9825              8.688
      128       1000        0.915               13.022
      96        1000        0.95625             10.995
      80        1000        0.9775              10.042
      72        1000        0.96375             9.466
      68        1000        0.97375             8.677
      66        1000        0.975               8.747
      65        1000        0.96875             8.386

      64        500         0.9825              4.266
      64        250         0.9825              2.29
      64        100         0.97125             0.992
/* *****************************************************************************
 *  Find the values of k and T that maximize the test data set accuracy,
 *  while running under 10 second. Write them down (as well as the accuracy)
 *  and explain:
 *   1. Your strategy to find the optimal k, T.
 *   2. Why a small value of T leads to low test accuracy.
 *   3. Why a k that is too small or too big leads to low test accuracy.
 **************************************************************************** */
k* = 64
T* = 250
accuracy = 0.9825

1. We first fixed T to be something reasonably large (1000) and looked for an
optimal k. We did this by checking powers of two until we saw a decrease.
We then effectively "binary searched" the range until we found the maximum on
[n, 2n].
We then experimented with values of T, and we found that 250 was "large enough"
to reach stabilization in performance. We also note monotone increase in
performance with T.

This testing methodology relies on the accuracy function being effectively
"concave everywhere." This seems to be heuristically sound, but the behavior at
(72, 1000) seems to suggest that this assumption is invalid.

2. A small value of T leads to low test accuracy because the model does not
have enough time to learn the data.
3. A k that is too small means that the model cannot distinguish between
transactions made in locations that are relatively far away, and this distinc-
tion is the entire impetus of the model. A k that is too large means that
transactions made in locations that are relatively near are distinguished,
which means that the model is really looking at which stores one is transacting,
rather than the geographical distance. This is over-specific data is a worse
predictor.
/* *****************************************************************************
 *  List any other comments here. Feel free to provide any feedback
 *  on how much you learned from doing the assignment, and whether
 *  you enjoyed doing it.
 **************************************************************************** */
Great final project!
