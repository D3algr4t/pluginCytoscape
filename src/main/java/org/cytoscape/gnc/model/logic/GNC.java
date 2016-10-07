package org.cytoscape.gnc.model.logic;

import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
    
    public GNC(ProgressMonitor pm) {
        this.pm = pm;
    }
    
    public void interrupt() {
        isInterrupted = true;
    }
    
    public boolean isInterrupted() {
        return isInterrupted;
    }
    
    public GNCResult execute(final IGRN network, final IGRN db) {
        try {
            int commonGenes = 0;
            HashMap<Integer, Position> positions = new HashMap<Integer, Position>();
            
            for (int i = 0; i < network.getNodes().length; i++) {
                Node node = network.getNodes()[i];
                Integer nodeId = db.getNodeId(node);
                if (nodeId != null) {
                    Position aux = new Position(node, i, nodeId);
                    positions.put(commonGenes, aux);
                    commonGenes++;
                }
            }
            
            if (commonGenes == 0) {
                throw new AnalysisErrorException("No common genes found between the network and the database.");
            }
            
            pm.setStatus("Calculating distance matrices");
            ExecutorService pool = Executors.newFixedThreadPool(maxThreads > 4 ? 4 : maxThreads);
            Future<int[][]> networkMatrixCallable = pool.submit(new Callable() {
                @Override
                public int[][] call() throws InterruptedException {
                    return network.getNodes().length >= db.getNodes().length ?getDistanceMatrix(network, pm) : getDistanceMatrix(network);
                }
            });

            Future<int[][]> dbMatrixCallable = pool.submit(new Callable() {
                @Override
                public int[][] call() throws InterruptedException {
                    return network.getNodes().length < db.getNodes().length ? getDistanceMatrix(db, pm) : getDistanceMatrix(db);
                }
            });

            Future<Integer> calculateFPCallable = pool.submit(new Callable() {
                @Override
                public Integer call() throws InterruptedException {
                    return calculateFP(network, db);
                } 
            });
            Future<Integer> calculateFNCallable = pool.submit(new Callable() {
                @Override
                public Integer call() throws InterruptedException {
                    return calculateFN(network, db);
                }
            });
            int[][] networkMatrix = networkMatrixCallable.get();
            int[][] dbMatrix = dbMatrixCallable.get();
            int FP = calculateFPCallable.get();
            int FN = calculateFNCallable.get();
            
            pm.setStatus("Obtaining measures");
            float gnc = 0.0F;
            int TP = 0;int TN = 0;
            for (int i = 0; i < commonGenes; i++) {
                pm.setProgress(0.8F + 0.2F * i / commonGenes);
                for (int j = 0; j < commonGenes; j++) {
                    if (i != j) {
                        Position positionI = positions.get(i);
                        Position positionJ = positions.get(j);
                        
                        float alfa = networkMatrix[positionI.getInputPosition()][positionJ.getInputPosition()];
                        float beta = dbMatrix[positionI.getDBPosition()][positionJ.getDBPosition()];
                        if ((alfa < Properties.infinity) && (beta < Properties.infinity)) {
                            if (alfa == beta) {
                                TP++;
                            } else {
                                TN++;
                            }
                        }
                        
                        if (i < j) {
                            gnc += 1.0F / (Math.abs(alfa - beta) * Math.min(alfa, beta) + 1.0F);
                        }
                    }
                }
            }
            
            gnc /= commonGenes * (commonGenes - 1) / 2.0F;
            
            float ppv = (float)TP / (TP + FP);
            float recall = (float)TP / (TP + FN);
            float f = 2.0F * ppv * recall / (ppv + recall);
            
            pm.setStatus("Calculating GNC");
            return new GNCResult(network, db, gnc, ppv, f, commonGenes);
        } catch (AnalysisErrorException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AnalysisErrorException(ex);
        }
    }
    
    private int[][] getDistanceMatrix(IGRN grn) throws InterruptedException {
        int[][] adjMatrix = grn.getAdjMatrix();
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
    
    private int[][] getDistanceMatrix(IGRN grn, ProgressMonitor pm) throws InterruptedException {
        int[][] adjMatrix = grn.getAdjMatrix();
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
    
    private int calculateFP(IGRN network, IGRN db) throws InterruptedException {
        int FP = 0;
        for (int i = 0; i < network.getEdges().size(); i++) {
            boolean enc = false;
            for (int j = 0; j < db.getEdges().size(); j++) {
                if (isInterrupted) {
                    throw new InterruptedException("GNC execution was cancelled");
                }
                Edge inA = (Edge)network.getEdges().get(i);
                Edge bdA = (Edge)db.getEdges().get(j);
                if ((inA.getTarget().equalsIgnoreCase(bdA.getTarget())) && (inA.getSource().equalsIgnoreCase(bdA.getSource()))) {
                    enc = true;
                    break;
                }
            }
            if (!enc) {
                FP++;
            }
        }
        return FP;
    }
    
    private int calculateFN(IGRN network, IGRN db) throws InterruptedException {
        int FN = 0;
        for (int i = 0; i < db.getEdges().size(); i++) {
            boolean enc = false;
            for (int j = 0; j < network.getEdges().size(); j++) {
                if (isInterrupted) {
                    throw new InterruptedException("GNC execution was cancelled");
                }
                Edge inA = (Edge)network.getEdges().get(j);
                Edge bdA = (Edge)db.getEdges().get(i);
                if ((inA.getTarget().equalsIgnoreCase(bdA.getTarget())) && (inA.getSource().equalsIgnoreCase(bdA.getSource()))) {
                    enc = true;
                    break;
                }
            }
            if (!enc) {
                FN++;
            }
        }
        return FN;
    }
}