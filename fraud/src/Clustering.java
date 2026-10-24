import edu.princeton.cs.algs4.CC;
import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;
import edu.princeton.cs.algs4.KruskalMST;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.StdRandom;

public class Clustering {

    private CC components;
    private int k;

    // run the clustering algorithm and create the clusters
    public Clustering(Point2D[] locations, int k) {
        this.k = k;
        int m = locations.length;
        EdgeWeightedGraph g1 = new EdgeWeightedGraph(m);

        for (int i = 0; i < m; i++) {
            for (int j = i + 1; j < m; j++) {
                double distance = locations[i].distanceSquaredTo(locations[j]);
                Edge edge = new Edge(i, j, distance);
                g1.addEdge(edge);
            }
        }

        KruskalMST kruskal = new KruskalMST(g1);
        MinPQ<Edge> minHeap = new MinPQ<Edge>(m);

        // get m-k smallest edges
        for (Edge e : kruskal.edges()) {
            minHeap.insert(e);
        }

        EdgeWeightedGraph g2 = new EdgeWeightedGraph(m);
        for (int i = 0; i < m - k; i++) {
            g2.addEdge(minHeap.delMin());
        }
        components = new CC(g2);
    }

    // return the cluster of the ith location
    public int clusterOf(int i) {
        return components.id(i);
    }

    // use the clusters to reduce the dimensions of an input
    public int[] reduceDimensions(int[] input) {
        int[] reduced = new int[k];
        for (int i = 0; i < input.length; i++) {
            reduced[clusterOf(i)] += input[i];
        }

        return reduced;
    }

    // unit testing (required)
    public static void main(String[] args) {
        int c = Integer.parseInt(args[0]);
        int p = Integer.parseInt(args[1]);

        Point2D[] centers = new Point2D[c];
        double x = StdRandom.uniformDouble(0, 1001);
        double y = StdRandom.uniformDouble(0, 1001);

        centers[0] = new Point2D(x, y);
        for (int i = 1; i < centers.length; i++) {
            Point2D point = new Point2D(x, y);
            boolean valid = true;
            for (int j = 0; j < i; j++) {
                if (centers[j].distanceTo(point) >= 4)
                    valid = false;
            }
            while (!valid) {
                x = StdRandom.uniformDouble(0, 1001);
                y = StdRandom.uniformDouble(0, 1001);
                point = new Point2D(x, y);
                valid = true;
                for (int j = 0; j < i; j++) {
                    if (centers[j].distanceTo(point) >= 4)
                        valid = false;
                }
            }
            centers[i] = point;
        }

        Point2D[] locations = new Point2D[c * p];
        for (int i = 0; i < c; i++) {
            for (int j = 0; j < p; j++) {
                double xCoor = StdRandom.uniformDouble(centers[i].x() - 1,
                                                       centers[i].x() + 1);
                double yCoor = StdRandom.uniformDouble(centers[i].y() - 1,
                                                       centers[i].y() + 1);
                Point2D point = new Point2D(xCoor, yCoor);
                while (point.distanceTo(centers[i]) > 1) {
                    xCoor = StdRandom.uniformDouble(centers[i].x() - 1,
                                                    centers[i].x() + 1);
                    yCoor = StdRandom.uniformDouble(centers[i].y() - 1,
                                                    centers[i].y() + 1);
                    point = new Point2D(xCoor, yCoor);
                }
                locations[i * p + j] = point;
            }
        }

        Clustering clustering = new Clustering(locations, c);

        for (int i = 0; i < c; i++) {
            for (int j = 0; j < p - 1; j++) {
                if (clustering.clusterOf(i * p + j) !=
                        clustering.clusterOf(i * p + j + 1)) {
                    throw new RuntimeException("Error!");
                }
            }
        }
    }
}
