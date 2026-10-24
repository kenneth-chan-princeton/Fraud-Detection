import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;

public class BoostingAlgorithm {
    private int[][] input;
    private int[] labels;
    private double[] weights;
    private Queue<WeakLearner> learners;
    private Clustering cluster;

    // create the clusters and initialize your data structures
    public BoostingAlgorithm(int[][] input, int[] labels,
                             Point2D[] locations, int k) {
        cluster = new Clustering(locations, k);
        weights = new double[labels.length];
        for (int i = 0; i < labels.length; i++) {
            weights[i] = (double) 1 / labels.length;
        }
        this.labels = labels;
        this.input = new int[labels.length][k];
        for (int i = 0; i < labels.length; i++) {
            this.input[i] = cluster.reduceDimensions(input[i]);
        }
        learners = new Queue<>();
    }

    // return the current weight of the ith point
    public double weightOf(int i) {
        return weights[i];
    }

    // apply one step of the boosting algorithm
    public void iterate() {
        WeakLearner learner = new WeakLearner(input, weights, labels);
        learners.enqueue(learner);
        for (int i = 0; i < labels.length; i++) {
            int predict = learner.predict(input[i]);
            if (predict != labels[i]) {
                weights[i] *= 2;
            }
        }
        normalize(weights);
    }

    private void normalize(double[] arr) {
        double sum = 0;
        for (double d : arr)
            sum += d;
        for (int i = 0; i < arr.length; i++) {
            arr[i] /= sum;
        }
    }

    // return the prediction of the learner for a new sample
    public int predict(int[] sample) {
        int pos = 0;
        int[] reduced = cluster.reduceDimensions(sample);
        for (WeakLearner wl : learners) {
            pos += wl.predict(reduced);
        }
        if (pos > learners.size() / 2)
            return 1;
        return 0;
    }

    // unit testing
    public static void main(String[] args) {
        // read in the terms from a file
        DataSet training = new DataSet(args[0]);
        DataSet testing = new DataSet(args[1]);
        int k = Integer.parseInt(args[2]);
        int T = Integer.parseInt(args[3]);

        int[][] trainingInput = training.getInput();
        int[][] testingInput = testing.getInput();
        int[] trainingLabels = training.getLabels();
        int[] testingLabels = testing.getLabels();
        Point2D[] trainingLocations = training.getLocations();

        // train the model
        Stopwatch s = new Stopwatch();
        BoostingAlgorithm model = new BoostingAlgorithm(
                trainingInput, trainingLabels, trainingLocations, k
        );
        for (int t = 0; t < T; t++)
            model.iterate();

        // calculate the training data set accuracy
        double trainingAccuracy = 0;
        for (int i = 0; i < training.getN(); i++)
            if (model.predict(trainingInput[i]) == trainingLabels[i])
                trainingAccuracy += 1;
        trainingAccuracy /= training.getN();

        // calculate the test data set accuracy
        double testingAccuracy = 0;
        for (int i = 0; i < testing.getN(); i++)
            if (model.predict(testingInput[i]) == testingLabels[i])
                testingAccuracy += 1;
        testingAccuracy /= testing.getN();

        StdOut.println("Training accuracy of model: " + trainingAccuracy);
        StdOut.println("Test accuracy of model: " + testingAccuracy);
        StdOut.println("Time: " + s.elapsedTime() + " seconds");
    }
}
