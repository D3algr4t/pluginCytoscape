package org.cytoscape.gnc.model.businessobjects;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class GNCResult {
    private final IGRN network;
    private final IGRN db;
    private final float gnc;
    private final float ppv;
    private final float fMeasure;
    private final Node[] genes;
    private final float[][] cohrenceMatrix;
    
    public GNCResult(IGRN network, IGRN db, float GNC, float PPV, float F, Node[] genes, float[][] cohrenceMatrix) {
        this.network = network;
        this.db = db;
        this.gnc = GNC;
        this.ppv = PPV;
        this.fMeasure = F;
        this.genes = genes;
        this.cohrenceMatrix = cohrenceMatrix;
    }
    
    public IGRN getNetwork() {
        return network;
    }
    
    public IGRN getDb() {
        return db;
    }
    
    public float getGNC() {
        return gnc;
    }
    
    public float getPPV() {
        return ppv;
    }
    
    public float getFMeasure() {
        return fMeasure;
    }
    
    public Node[] getGenes() {
        return genes;
    }
    
    public float[][] getCohrenceMatrix() {
        return cohrenceMatrix;
    }
    
    public void printMatrixToFile(String path) throws IOException {
        try (BufferedWriter fw = new BufferedWriter(new FileWriter(path))) {
            for (Node gene : genes) {
                fw.write("," + gene.getName());
            }
            fw.newLine();

            int i = 0;
            for (Node gene : genes) {
                fw.write(gene.getName());
                for (float coherence : cohrenceMatrix[i]) {
                    fw.write("," + coherence);
                }
                fw.newLine();
                i++;
            }
        }
    }
}