import edu.princeton.cs.algs4.IndexMinPQ;
import edu.princeton.cs.algs4.ST;

public class WeakLearner {
    // optimal parameters for a decision stump on input, weights and labels
    private int d, v, s;

    // train the weak learner
    public WeakLearner(int[][] input, double[] weights, int[] labels) {

        // transpose and sort the input array
        int[][] transposed = transpose(input);

        int n = input.length;

        // create a champion to track the lowest weight
        double champ = 0;
        for (double weight : weights) {
            champ += weight;
        }
        champ += 1;

        int k = transposed.length;
        double[] error; // error[i] corresponds to sp = 0, 1

        IndexMinPQ<Double> pq0; // for s = 0
        IndexMinPQ<Double> pq1; // for s = 1
        ST<Integer, Integer> map;

        // for each dimension (each cluster)
        for (int i = 0; i < k; i++) {
            // create 2 d array of size 2 x n (net weights array)
            double[][] netWeights = new double[2][n];
            // create ST<Int, Int> value, reference index of the weights
            ST<Integer, Integer> reference = new ST<Integer, Integer>();
            // IndexMinPQ<Integer> reference index and vp
            IndexMinPQ<Integer> sortValue = new IndexMinPQ<>(n);

            for (int z = 0; z < transposed[0].length; z++) {
                if (!reference.contains(transposed[i][z])) {
                    reference.put(transposed[i][z], z);
                    sortValue.insert(z, transposed[i][z]);
                }
                if (labels[z] == 1) {
                    netWeights[0][reference.get(transposed[i][z])]
                            += weights[z];
                }
                if (labels[z] == 0) {
                    netWeights[1][reference.get(transposed[i][z])]
                            += weights[z];
                }
            }

            error = new double[2];
            for (int y = 0; y < netWeights[0].length; y++) {
                error[0] += netWeights[1][y];
                error[1] += netWeights[0][y];
            }

            int val = sortValue.minKey() - 1;

            double errorChamp;

            pq0 = new IndexMinPQ<>(n + 1);
            pq1 = new IndexMinPQ<>(n + 1);
            map = new ST<Integer, Integer>();

            map.put(0, val);
            pq0.insert(0, error[0]);
            pq1.insert(0, error[1]);

            int j = 0;

            // training and moving the value line
            // for each value (each amount spent in the cluster)
            // subtract the weight from the error if a point is correct
            while (!sortValue.isEmpty()) {
                val = sortValue.minKey();
                int index = sortValue.minIndex();
                sortValue.delMin();

                // s = 0

                error[0] += netWeights[0][index] - netWeights[1][index];
                error[1] += netWeights[1][index] - netWeights[0][index];

                map.put(j + 1, val);
                pq0.insert(j + 1, error[0]);
                pq1.insert(j + 1, error[1]);
                j++;
            }
            if (pq0.minKey() < pq1.minKey()) {
                errorChamp = pq0.minKey();
                if (errorChamp < champ) {
                    d = i;
                    v = map.get(pq0.minIndex());
                    s = 0;
                    champ = errorChamp;
                }
            }
            else {
                errorChamp = pq1.minKey();
                if (errorChamp < champ) {
                    d = i;
                    v = map.get(pq1.minIndex());
                    s = 1;
                    champ = errorChamp;
                }
            }
        }
    }

    // transpose the double array
    private int[][] transpose(int[][] input) {
        int[][] ret = new int[input[0].length][input.length];

        for (int i = 0; i < input[0].length; i++) {
            for (int j = 0; j < input.length; j++) {
                ret[i][j] = input[j][i];
            }
        }
        return ret;
    }


    // return the prediction of the learner for a new sample
    public int predict(int[] sample) {
        if (sample[d] <= v)
            return s;
        else return (s + 1) % 2;
    }

    // return the dimension the learner uses to separate the data
    public int dimensionPredictor() {
        return d;
    }

    // return the value the learner uses to separate the data
    public int valuePredictor() {
        return v;
    }

    // return the sign the learner uses to separate the data
    public int signPredictor() {
        return s;
    }

    // unit testing
    public static void main(String[] args) {
    }
}
