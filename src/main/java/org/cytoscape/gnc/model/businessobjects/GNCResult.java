package org.cytoscape.gnc.model.businessobjects;

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
    private final int commonGenes;
    
    public GNCResult(IGRN network, IGRN db, float GNC, float PPV, float F, int commonGenes) {
        this.network = network;
        this.db = db;
        this.gnc = GNC;
        this.ppv = PPV;
        this.fMeasure = F;
        this.commonGenes = commonGenes;
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
    
    public int getCommonGenes() {
        return commonGenes;
    }
}