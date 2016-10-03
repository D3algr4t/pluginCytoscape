package org.cytoscape.gnc.model.logic;

import java.util.HashMap;
import org.cytoscape.gnc.model.businessobjects.Edge;
import org.cytoscape.gnc.model.businessobjects.GNCResult;
import org.cytoscape.gnc.model.businessobjects.IGRN;
import org.cytoscape.gnc.model.businessobjects.Node;
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
    
    public GNC(ProgressMonitor pm) {
        this.pm = pm;
    }
    
    public void interrupt() {
        isInterrupted = true;
    }
    
    public boolean isInterrupted() {
        return isInterrupted;
    }

    public GNCResult execute(IGRN network, IGRN db){
        pm.setStatus("Calculating distance matrices");
        int[][] networkMatrix = getDistanceMatrix(network);
        int[][] dbMatrix = getDistanceMatrix(db);
        
        
        pm.setStatus("Calculating coherence matrix");
        float[] mCoherencia = calculaInterseccion(network, networkMatrix, db, dbMatrix);

        pm.setStatus("Calculating GNC");
        return new GNCResult(network, db, mCoherencia[0],mCoherencia[1],mCoherencia[2], (int)mCoherencia[3]);
    }
   
    private int[][] getDistanceMatrix(IGRN grn) {
        int[][] adjMatrix = grn.getAdjMatrix();
        Integer[] maxI = new Integer[adjMatrix.length];
        Integer[] maxJ = new Integer[adjMatrix.length];
        
        for (int k = 0; k < adjMatrix.length; k++) {
            for (int i = 0; i < adjMatrix.length; i++) {
                Integer currentMaxI = maxI[i];
                if (currentMaxI == null) {
                    currentMaxI = adjMatrix[i][0];
                    for (int row : adjMatrix[i]) {
                        if (row > currentMaxI) {
                            currentMaxI = row;
                        }
                    }
                    maxI[i] = currentMaxI;
                }

                for (int j = 0; j < adjMatrix.length; j++) {
                    Integer currentMaxJ = maxJ[j];
                    if (currentMaxJ == null) {
                        currentMaxJ = adjMatrix[0][k];
                        for (int[] row : adjMatrix) {
                            if (row[k] > currentMaxJ) {
                                currentMaxJ = row[k];
                            }
                        }
                        maxJ[j] = currentMaxJ;
                    }
                    int dt = currentMaxI + currentMaxJ;
                    int increase = dt - adjMatrix[i][j];

                    if (increase > 0){
                        adjMatrix[i][j] = dt;
                        currentMaxI = currentMaxI + increase;
                        maxI[i] = currentMaxI;
                        maxJ[j] = currentMaxJ + increase;
                        
                    }
                }
            }
        }
        
        return adjMatrix;
    }
   
    private float[] calculaInterseccion(IGRN network, int[][] networkMatrix, IGRN db, int[][] dbMatrix) {
        float sumaDiagonalSup = 0;
        int TP = 0, TN = 0;
        
        int commonGenes = 0;
        HashMap<Integer, Position> positions = new HashMap();

        for (int i = 0; i < network.getNodes().size(); i++) {
            Node node = network.getNodes().get(i);
            Integer nodeId = db.getNodeId(node);
            if (nodeId != null) {
                Position aux = new Position(node, i, nodeId);
                positions.put(commonGenes, aux);
                commonGenes++;
            }
        }
        
        if (commonGenes <= 0) {
            return new float[] {0, 0, 0, 0};
        }

        for (int i = 0; i < commonGenes; i++) {
            for (int j = 0; j < commonGenes; j++) {
                if (i != j) {
                    Position positionI = positions.get(i);
                    Position posicionJ = positions.get(j);
                    
                    float alfa = networkMatrix[positionI.getInputPosition()][posicionJ.getInputPosition()];
                    float beta = dbMatrix[positionI.getDBPosition()][posicionJ.getDBPosition()];
                    if (alfa < Properties.infinity && beta < Properties.infinity) {
                        if (alfa == beta) {
                            TP++;
                        } else {
                            TN++;
                        }
                    }

                    if (i < j) {
                        sumaDiagonalSup += 1 / (((Math.abs(alfa - beta)) * Math.min(alfa, beta)) + 1);;
                    }
                }
            }
        }

        sumaDiagonalSup = sumaDiagonalSup / ((float) (commonGenes * (commonGenes -  1)) / 2);

        int FP = calculateFP(network, db);
        int FN = calculateFN(network, db);
            
        float coherencePPV = ((float) TP / ((float) TP + (float) FP));
        float coherenceRecall = ((float) TP / ((float) TP + (float) FN));
        float coherenceF = (((float) 2 * (float) coherencePPV * (float) coherenceRecall) / ((float) coherencePPV + (float) coherenceRecall));

        return new float[] {sumaDiagonalSup, coherencePPV, coherenceF, commonGenes};
    }
    
    private int calculateFP (IGRN network, IGRN db) {
        int FP = 0;
        for (int i = 0; i < network.getEdges().size(); i++) {
            boolean enc = false;
            for (int j = 0; j < db.getEdges().size(); j++) {                
                Edge inA = network.getEdges().get(i);
                Edge bdA = db.getEdges().get(j);                
                if (inA.getTarget().equalsIgnoreCase(bdA.getTarget()) && inA.getSource().equalsIgnoreCase(bdA.getSource())) {
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
    
    private int calculateFN(IGRN network, IGRN db) {
        int FN = 0;
        for (int i = 0; i < db.getEdges().size(); i++) {
            boolean enc = false;
            for (int j = 0; j < network.getEdges().size(); j++) {                
                Edge inA = network.getEdges().get(j);
                Edge bdA = db.getEdges().get(i);                
                if (inA.getTarget().equalsIgnoreCase(bdA.getTarget()) && inA.getSource().equalsIgnoreCase(bdA.getSource())) {
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