package org.cytoscape.gnc.model.logic.graphs.algorithms;

import java.util.HashSet;
import java.util.Set;
import org.cytoscape.gnc.model.businessobjects.Edge;
import org.cytoscape.gnc.model.businessobjects.IGRN;
import org.cytoscape.gnc.model.businessobjects.Node;
import org.cytoscape.gnc.model.businessobjects.utils.ProgressMonitor;
import org.cytoscape.gnc.model.businessobjects.utils.Properties;

/**
 *
 * @author Juan José Díaz Montaña
 */
public class FloydWarshall implements DistanceMatrixAlgorithm {
    protected boolean isInterrupted = false;

    public void interrupt() {
        isInterrupted = true;
    }
    
    @Override
    public int[][] getDistanceMatrix(IGRN grn, ProgressMonitor pm) throws InterruptedException {
        int[][] adjMatrix = getAdjMatrix(grn);
        for (int k = 0; k < adjMatrix.length; k++) {
            pm.setProgress(0.8F * k / adjMatrix.length);
            int[] column = adjMatrix[k];
            for (int i = 0; i < adjMatrix.length; i++) {
                int[] row = adjMatrix[i];
                for (int j = i + 1; j < adjMatrix.length; j++) {
                    if (isInterrupted) {
                        throw new InterruptedException("GNC execution was cancelled");
                    }
                    int currentDistance = row[j];
                    if (currentDistance >= 2) {
                        int newDistance = row[k] + column[j];
                        
                        if (currentDistance > newDistance) {
                            row[j] = newDistance;
                            adjMatrix[j][i] = newDistance;
                        }
                    }
                }
            }
        }
        return adjMatrix;
    }

    @Override
    public int[][] getDistanceMatrix(IGRN grn) throws InterruptedException {
        int[][] adjMatrix = getAdjMatrix(grn);
        for (int k = 0; k < adjMatrix.length; k++) {
            int[] column = adjMatrix[k];
            for (int i = 0; i < adjMatrix.length; i++) {
                int[] row = adjMatrix[i];
                for (int j = i + 1; j < adjMatrix.length; j++) {
                    if (isInterrupted) {
                        throw new InterruptedException("GNC execution was cancelled");
                    }
                    int currentDistance = row[j];
                    if (currentDistance >= 2) {
                        int newDistance = row[k] + column[j];
                        
                        if (currentDistance > newDistance) {
                            row[j] = newDistance;
                            adjMatrix[j][i] = newDistance;
                        }
                    }
                }
            }
        }
        return adjMatrix;
    }

    private int[][] getAdjMatrix(IGRN network)  {
        Node[] nodes = network.getNodes();
        int[][] m = new int[nodes.length][nodes.length];
        
        for (int i = 0; i < nodes.length; i++) {
            Node node = nodes[i];
            Set neighbours = new HashSet();
            
            for (Edge edge : node.getEdges()) {
                neighbours.add(edge.getSource().equals(node.getName()) ? edge.getTarget() : edge.getSource());
            }
            
            m[i][i] = 0;
            for (int j = i + 1; j < nodes.length; j++) {
                if (neighbours.contains(nodes[j].getName())) {
                    m[i][j] = 1;
                    m[j][i] = 1;
                } else {
                    m[i][j] = Properties.infinity;
                    m[j][i] = Properties.infinity;
                }
            }
        }
        
        return m;

//        Node[] nodes = network.getNodes();
//        int[][] m = new int[nodes.length][nodes.length];
//        
//        for (int[] row : m) {
//            for (int i = 0; i < row.length; i++) {
//                row[i] = 99;
//            }
//        }
//        
//        for (int i = 0; i < nodes.length; i++) {
//            Node node = nodes[i];
//            
//            m[i][i] = 0;
//            for (Edge edge : node.getEdges()) {
//                Node neighbour = edge.getSource().equals(node.getName()) ? edge.getTarget() : edge.getSource();
//                m[i][neighbour.getIndex()] = 1;//edge.getWeight();
//            }
//        }
//
//        return m;
    }
}
