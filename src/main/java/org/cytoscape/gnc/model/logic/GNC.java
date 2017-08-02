package org.cytoscape.gnc.model.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.cytoscape.gnc.model.businessobjects.Edge;
import org.cytoscape.gnc.model.businessobjects.GNCResult;
import org.cytoscape.gnc.model.businessobjects.IGRN;
import org.cytoscape.gnc.model.businessobjects.Node;
import org.cytoscape.gnc.model.businessobjects.exceptions.AnalysisErrorException;
import org.cytoscape.gnc.model.businessobjects.utils.Position;
import org.cytoscape.gnc.model.businessobjects.utils.ProgressMonitor;
import org.cytoscape.gnc.model.businessobjects.utils.Properties;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class GNC {
    protected ProgressMonitor pm;
    protected boolean isInterrupted = false;
    private final int maxThreads = Runtime.getRuntime().availableProcessors();
    private final ExecutorService pool = Executors.newFixedThreadPool(maxThreads);
    private final CompletionService completionService = new ExecutorCompletionService(pool);

    Measures measures = new Measures();
    private float calculateDistanceMatricesSteps;
    private float calculateMeasuresSteps;
    private float calculateGNCSteps;
    private float totalSteps;

    public GNC(ProgressMonitor pm) {
        this.pm = pm;
    }

    public void interrupt() {
        isInterrupted = true;
        pool.shutdownNow();
    }

    public boolean isInterrupted() {
        return isInterrupted;
    }

    public GNCResult execute(final IGRN network, final IGRN db) {
        try {
            List<Node> commonGenes = new LinkedList();
            List<Position> positions = new ArrayList();

            int networkSize = network.getNodes().length;
            final int[][] networkMatrix = new int[networkSize][networkSize];
            int dbSize = db.getNodes().length;
            final int[][] dbMatrix = new int[dbSize][dbSize];

            pm.setStatus("Calculating distance matrices");

            for (int i = 0; i < network.getNodes().length; i++) {
                final Node networkNode = network.getNodes()[i];
                final Integer nodeId = db.getNodeId(networkNode);
                if (nodeId != null) {
                    commonGenes.add(networkNode);
                    positions.add(new Position(networkNode, i, nodeId));
                    
                    completionService.submit(new Callable()
                    {
                        @Override
                        public Void call() throws InterruptedException {
                            fillDistanceMatrix(network, networkNode, networkMatrix);
                            return null;
                        }
                    });
                    completionService.submit(new Callable()
                    {
                        @Override
                        public Void call() throws InterruptedException {
                            fillDistanceMatrix(db, db.getNodes()[nodeId], dbMatrix);
                            return null;
                        }
                    });
                }
            }

            int commonGenesCount = commonGenes.size();
            
            if (commonGenesCount == 0) {
                throw new AnalysisErrorException("No common genes found between the network and the database.");
            }

            calculateDistanceMatricesSteps = (2 * positions.size());
            calculateMeasuresSteps = network.getEdges().size();
            calculateGNCSteps = commonGenesCount;
            totalSteps = (calculateDistanceMatricesSteps + calculateGNCSteps + calculateGNCSteps);
            
            for (int i = 0; i < calculateDistanceMatricesSteps; i++) {
                if (isInterrupted) {
                    throw new InterruptedException("GNC execution was cancelled");
                }
                completionService.take().get();
                pm.setProgress(i / totalSteps);
            }

            if (isInterrupted) {
                throw new InterruptedException("GNC execution was cancelled");
            }
            pm.setStatus("Calculating PPV and F-Measure");
            calculateMeasures(network, db, new HashSet(commonGenes));

            if (isInterrupted) {
                throw new InterruptedException("GNC execution was cancelled");
            }
            pm.setStatus("Calculating GNC");
            calculateGNC(networkMatrix, dbMatrix, positions);

            if (isInterrupted) {
                throw new InterruptedException("GNC execution was cancelled");
            }
            return new GNCResult(network, db, measures.GNC, measures.ppv, measures.fMeasure, (Node[])commonGenes.toArray(new Node[commonGenes.size()]), measures.coherenceMatrix);
        } catch (AnalysisErrorException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AnalysisErrorException(ex);
        } finally {
            pool.shutdownNow();
        }
    }

    private void fillDistanceMatrix(IGRN grn, Node node, int[][] distanceMatrix) throws InterruptedException {
        int nodeIndex = grn.getNodeId(node);
        boolean[] marked = new boolean[grn.getNodes().length];
        int[] row = distanceMatrix[nodeIndex];
        Arrays.fill(row, Properties.infinity);
        
        marked[nodeIndex] = true;
        row[nodeIndex] = 0;
        LinkedList<Node> q = new LinkedList();
        q.add(node);
        Node node1;
        int distanceNode1; while (!q.isEmpty()) {
            if (isInterrupted) {
                throw new InterruptedException("GNC execution was cancelled");
            }
            node1 = (Node)q.poll();
            int index1 = grn.getNodeId(node1);
            distanceNode1 = row[index1];
            for (Edge edge : node1.getEdges()) {
                if (isInterrupted) {
                    throw new InterruptedException("GNC execution was cancelled");
                }
                Node node2 = edge.getSource().equals(node1) ? edge.getTarget() : edge.getSource();
                int index2 = grn.getNodeId(node2);
                if (!marked[index2]) {
                    row[index2] = (distanceNode1 + 1);
                    marked[index2] = true;
                    q.add(node2);
                }
            }
        }
    }

    private void calculateMeasures(IGRN network, IGRN db, final Set<Node> commonGenes) throws InterruptedException, ExecutionException {
        final ConcurrentLinkedQueue<Edge> fnEdges = new ConcurrentLinkedQueue(db.getEdges());
        for (final Edge inEdge : network.getEdges()) {
            completionService.submit(new Callable() {
                @Override
                public Boolean call() throws InterruptedException {
                    if (!commonGenes.contains(inEdge.getSource()) || !commonGenes.contains(inEdge.getTarget())) {
                        return true;
                    }

                    for (Iterator<Edge> it = fnEdges.iterator(); it.hasNext();) {
                        if (isInterrupted) {
                            throw new InterruptedException("GNC execution was cancelled");
                        }

                        Edge dbEdge = it.next();
                        if (inEdge.equals(dbEdge)) {
                            it.remove();
                            return false;
                        }
                    }
                    
                    return true;
                }
            });
        }

        int FP = 0;
        for (int i = 0; i < network.getEdges().size(); i++) {
            if (isInterrupted) {
                throw new InterruptedException("GNC execution was cancelled");
            }
            if ((boolean)completionService.take().get()) {
                FP++;
            }
            pm.setProgress(calculateDistanceMatricesSteps + i / totalSteps);
        }

        int FN = fnEdges.size();
        int TP = network.getEdges().size() - FP;
//        int TN = db.getEdges().size() - FN;

        measures.ppv = (float)TP / (TP + FP);
        measures.recall = (float)TP / (TP + FN);
        measures.fMeasure = 2.0F * measures.ppv * measures.recall / (measures.ppv + measures.recall);
    }

    private void calculateGNC(final int[][] networkMatrix, final int[][] dbMatrix, final List<Position> positions) throws InterruptedException, ExecutionException {
        measures.coherenceMatrix = new float[positions.size()][positions.size()];
        for (int it = 0; it < positions.size(); it++) {
            final int i = it;
            final Position positionI = (Position)positions.get(i);
            completionService.submit(new Callable() {
                @Override
                public Float call() throws InterruptedException {
                    float gncAccumulator = 0;
                    for (int j = i + 1; j < positions.size(); j++) {
                        Position positionJ = positions.get(j);
                        
                        float alfa = networkMatrix[positionI.getInputPosition()][positionJ.getInputPosition()];
                        float beta = dbMatrix[positionI.getDBPosition()][positionJ.getDBPosition()];
                        float coherence = 1.0F / (Math.abs(alfa - beta) * Math.min(alfa, beta) + 1.0F);
                        measures.coherenceMatrix[i][j] = coherence;
                        measures.coherenceMatrix[j][i] = coherence;
                        gncAccumulator += coherence;
                    }
                    return gncAccumulator;
                }
            });
        }

        float intialProgress = calculateDistanceMatricesSteps + calculateMeasuresSteps;
        measures.GNC = 0;
        for (int i = 0; i < positions.size(); i++) {
            if (isInterrupted) {
                throw new InterruptedException("GNC execution was cancelled");
            }
            measures.GNC += (float)completionService.take().get();
            pm.setProgress(intialProgress + i / totalSteps);
        }

        measures.GNC /= positions.size() * (positions.size() - 1) / 2.0F;
    }

    private class Measures {
        public float GNC;
        public float ppv;
        public float recall;
        public float fMeasure;
        public float[][] coherenceMatrix;
    }
}