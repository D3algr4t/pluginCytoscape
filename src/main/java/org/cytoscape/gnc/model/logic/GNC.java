package org.cytoscape.gnc.model.logic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
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
import org.cytoscape.gnc.model.logic.graphs.algorithms.BreadthFirst;
import org.cytoscape.gnc.model.logic.graphs.algorithms.DistanceMatrixAlgorithm;

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
    private final DistanceMatrixAlgorithm distanceMatrixAlgorithm  = new BreadthFirst(completionService);
    
    private float coherenceMatrixProgressSection;
    private float measureProgressSection;
    private float gncProgressSection;
    
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
            int commonGenesCount = 0;
            Set<Node> commonGenes = new HashSet<Node>();
            Map<Integer, Position> positions = new HashMap<Integer, Position>();
            
            for (int i = 0; i < network.getNodes().length; i++) {
                Node node = network.getNodes()[i];
                Integer nodeId = db.getNodeId(node);
                if (nodeId != null) {
                    Position aux = new Position(node, i, nodeId);
                    positions.put(commonGenesCount, aux);
                    commonGenes.add(node);
                    commonGenesCount++;
                }
            }
            
            if (commonGenesCount == 0) {
                throw new AnalysisErrorException("No common genes found between the network and the database.");
            }
            
            int totalSteps = network.getNodes().length + db.getNodes().length + network.getEdges().size() + commonGenesCount;
            coherenceMatrixProgressSection = ((float)network.getNodes().length + db.getNodes().length) / totalSteps;
            measureProgressSection = (float) network.getEdges().size() / totalSteps;
            gncProgressSection = (float)commonGenesCount / totalSteps;
            
            pm.setStatus("Calculating distance matrices");

            int[][] networkMatrix = distanceMatrixAlgorithm.getDistanceMatrix(network);
            int[][] dbMatrix = distanceMatrixAlgorithm.getDistanceMatrix(db);

            int threadCount = network.getNodes().length + db.getNodes().length;
            for (int i = 0; i < threadCount; i++) {
                if (isInterrupted) {
                    throw new InterruptedException("GNC execution was cancelled");
                }
                completionService.take().get();
                pm.setProgress(coherenceMatrixProgressSection * i / threadCount);
            }
            
            if (isInterrupted) {
                throw new InterruptedException("GNC execution was cancelled");
            }
            pm.setStatus("Calculating PPV and F-Measure");
            calculateMeasures(network, db, commonGenes);

            if (isInterrupted) {
                throw new InterruptedException("GNC execution was cancelled");
            }
            pm.setStatus("Calculating GNC");
            calculateGNC(networkMatrix, dbMatrix, positions, commonGenesCount);

            if (isInterrupted) {
                throw new InterruptedException("GNC execution was cancelled");
            }
            return new GNCResult(network, db, Measures.GNC, Measures.ppv, Measures.fMeasure, commonGenes.toArray(new Node[commonGenes.size()]), Measures.coherenceMatrix);
        } catch (AnalysisErrorException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AnalysisErrorException(ex);
        } finally {
            pool.shutdownNow();  
        }
    }
    
    private void calculateMeasures(final IGRN network, final IGRN db, final Set<Node> commonGenes) throws InterruptedException, ExecutionException {
        final ConcurrentLinkedQueue<Edge> fnEdges= new ConcurrentLinkedQueue<Edge>(db.getEdges());
        for (final Edge inEdge : network.getEdges()) {
            completionService.submit(new Callable<Boolean>() {
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
            pm.setProgress(coherenceMatrixProgressSection + (measureProgressSection * i / network.getEdges().size()));
        }
        
        int FN = fnEdges.size();
        int TP = network.getEdges().size() - FP;
//        int TN = db.getEdges().size() - FN;

        Measures.ppv = (float)TP / (TP + FP);
        Measures.recall = (float)TP / (TP + FN);
        Measures.fMeasure = 2.0F * Measures.ppv * Measures.recall / (Measures.ppv + Measures.recall);
    }
    
    private void calculateGNC(final int[][] networkMatrix, final int[][] dbMatrix, final Map<Integer, Position> positions, final int commonGenesCount) throws InterruptedException, ExecutionException {
       Measures.coherenceMatrix = new float[commonGenesCount][commonGenesCount];
        for (int it = 0; it < commonGenesCount; it++) {
            final int i = it;
            final Position positionI = positions.get(i);
            completionService.submit(new Callable<Float>() {
                @Override
                public Float call() throws InterruptedException {
                    float gncAccumulator = 0;
                    for (int j = i + 1; j < commonGenesCount; j++) {
                        Position positionJ = positions.get(j);

                        float alfa = networkMatrix[positionI.getInputPosition()][positionJ.getInputPosition()];
                        float beta = dbMatrix[positionI.getDBPosition()][positionJ.getDBPosition()];
                        float coherence = 1.0F / (Math.abs(alfa - beta) * Math.min(alfa, beta) + 1.0F);
                        Measures.coherenceMatrix[i][j] = coherence;
                        Measures.coherenceMatrix[j][i] = coherence;
                        gncAccumulator +=  coherence;
                    }
                    return gncAccumulator;
                }
            });
        }

        float intialProgress = coherenceMatrixProgressSection + measureProgressSection;
        Measures.GNC = 0;
        for (int i = 0; i < commonGenesCount; i++) {
            if (isInterrupted) {
                throw new InterruptedException("GNC execution was cancelled");
            }
            Measures.GNC += (float)completionService.take().get();
            pm.setProgress(intialProgress + (gncProgressSection * i / commonGenesCount));
        }

        Measures.GNC /= commonGenesCount * (commonGenesCount - 1) / 2.0F;
    }
    private static class Measures { 
        public static float GNC;
        public static float ppv;
        public static float recall;
        public static float fMeasure;
        public static float[][] coherenceMatrix;
    } 
}