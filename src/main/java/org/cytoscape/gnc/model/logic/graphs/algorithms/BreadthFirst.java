package org.cytoscape.gnc.model.logic.graphs.algorithms;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import org.cytoscape.gnc.model.businessobjects.Edge;
import org.cytoscape.gnc.model.businessobjects.IGRN;
import org.cytoscape.gnc.model.businessobjects.Node;
import org.cytoscape.gnc.model.businessobjects.utils.Properties;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class BreadthFirst implements DistanceMatrixAlgorithm {
    private final CompletionService completionService;
    protected boolean isInterrupted = false;
    
    public BreadthFirst(CompletionService completionService) {
        this.completionService = completionService;
    }

    public void interrupt() {
        isInterrupted = true;
    }

    @Override
    public int[][] getDistanceMatrix(final IGRN grn) throws InterruptedException {
        final int nodeCount = grn.getNodes().length;
        final int[][] distanceMatrix = new int[nodeCount][nodeCount];

        for (final Node node : grn.getNodes()) {
            completionService.submit(new Callable() {
                @Override
                public Void call() throws InterruptedException {
                    int nodeIndex = grn.getNodeId(node);
                    int[] row = distanceMatrix[nodeIndex];
                    Arrays.fill(row, Properties.infinity);
                    boolean[] marked = new boolean[nodeCount];

                    marked[nodeIndex] = true;
                    distanceMatrix[nodeIndex][nodeIndex] = 0;
                    LinkedList<Node> q = new LinkedList<Node>();
                    q.add(node);

                    while (!q.isEmpty()) {
                        if (isInterrupted) {
                            throw new InterruptedException("GNC execution was cancelled");
                        }
                        Node node1 = q.poll();
                        int index1 = grn.getNodeId(node1);
                        for (Edge edge : node1.getEdges()) {
                            if (isInterrupted) {
                                throw new InterruptedException("GNC execution was cancelled");
                            }
                            Node node2 = edge.getSource().equals(node1) ? edge.getTarget() : edge.getSource();
                            int index2 = grn.getNodeId(node2);
                            if (!marked[index2]) {
                                row[index2] = row[index1] + 1;
                                marked[index2] = true;
                                q.add(node2);
                            }
                        }
                    }
                    return null;
                }
            });
        }

        return distanceMatrix;
    }
}