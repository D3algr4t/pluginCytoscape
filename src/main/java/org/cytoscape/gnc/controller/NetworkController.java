package org.cytoscape.gnc.controller;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.gnc.controller.utils.NetworkAdapter;
import org.cytoscape.gnc.model.businessobjects.Edge;
import org.cytoscape.gnc.model.businessobjects.GNCResult;
import org.cytoscape.gnc.model.businessobjects.IGRN;
import org.cytoscape.gnc.model.businessobjects.Node;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public final class NetworkController {
    private static CyApplicationManager applicationManager;
    private static VisualMappingManager visualMappingManager;
    
    public static final String GNCColumn = "GNC";    
    public static final String PPVColumn = "GNC - PPV";    
    public static final String FMeasureColumn = "GNC - F-Measure";
    public static final String DbColumn = "GNC - database";
    public static final String IsCommonGeneColumn = "GNC - Common";

    private final CyNetwork network;
    private final CyNetworkView networkView;
    
    public static void init(CyApplicationManager applicationManager, VisualMappingManager visualMappingManager) {
        NetworkController.applicationManager = applicationManager;
        NetworkController.visualMappingManager = visualMappingManager;
    }
    
    public NetworkController() throws InstantiationException {
        networkView = applicationManager.getCurrentNetworkView();
        if (networkView != null) {
            network = networkView.getModel();
        } else {
            network = applicationManager.getCurrentNetwork();
        }
        
        if (network == null) {
            throw new InstantiationException("No network selected.");
        }
    }
    
    public void dispose() {
    }
    
    public CyNetwork getCyNetwork() {
        return network;
    }
    
    public IGRN getGraph() {
        return NetworkAdapter.CyNetworkToGRN(network);
    }
    
    public void addGNCInfo(GNCResult result) {
        addNetworkInfo(result.getDb().getName(), result.getGNC(), result.getPPV(), result.getFMeasure());
        addNodeInfo(result.getGenes());
        addEdgeInfo(result);
        if (networkView != null) {
            applyVisualStyle();
        }
    }
    
    private void addNetworkInfo(String dbName, float gnc, float ppv, float fMeasure) {
        CyTable networkTable = network.getDefaultNetworkTable();

        try {
            networkTable.createColumn(GNCColumn, Double.class, false);
        } catch (IllegalArgumentException ex) {
            // Do nothing and just override the values in the collumn
        }
        try {
            networkTable.createColumn(PPVColumn, Double.class, false);
        } catch (IllegalArgumentException ex) {
            // Do nothing and just override the values in the collumn
        }
        try {
            networkTable.createColumn(FMeasureColumn, Double.class, false);
        } catch (IllegalArgumentException ex) {
            // Do nothing and just override the values in the collumn
        }
        try {
            networkTable.createColumn(DbColumn, String.class, false);
        } catch (IllegalArgumentException ex) {
            // Do nothing and just override the values in the collumn
        }

        try {
            CyRow row = networkTable.getAllRows().get(0);
            row.set(GNCColumn, (double)gnc);
            row.set(PPVColumn, (double)ppv);
            row.set(FMeasureColumn, (double)fMeasure);
            row.set(DbColumn, dbName);
        } catch(Exception e) {
            // Do nothing, Cytoscape is buggy
        }
    }
    
    private void addNodeInfo(Node[] genes) {
        CyTable nodeTable = network.getDefaultNodeTable();
        
        try {
            nodeTable.createColumn(IsCommonGeneColumn, Boolean.class, false, false);
        } catch (IllegalArgumentException ex) {
            // Do nothing and just override the values in the collumn
        }
        
        for (Node gene : genes) {
            gene.getCyRow().set(IsCommonGeneColumn, true);
        }
    }
    
    private void addEdgeInfo(GNCResult result) {
        CyTable edgeTable = network.getDefaultEdgeTable();
        try {
            edgeTable.createColumn(GNCColumn, Double.class, false);
        } catch (IllegalArgumentException ex) {
            // Do nothing and just override the values in the collumn
        }

        Node[] genes = result.getGenes();
        float[][] coherenceMatrix = result.getCohrenceMatrix();
        for (int i = 0; i < coherenceMatrix.length; i++) {
            Node n1 = genes[i];
            float[] n1Row = coherenceMatrix[i];
            for (int j = i + 1; j < coherenceMatrix.length; j++) {
                Node n2 = genes[j];
                float coherence = n1Row[j];
                if (coherence != -1) {
                    for (Edge edge : n1.getEdges()) {
                        if (edge.getSource().equals(n2) || edge.getTarget().equals(n2)) {
                            edge.getCyRow().set(GNCColumn, (double)coherence);
                        }
                    }
                }
            }
        }
    }
    
    public void applyVisualStyle() {
        for (VisualStyle style : visualMappingManager.getAllVisualStyles()) {
            if (style.getTitle().equals("GNC")) {
                style.apply(networkView);
                visualMappingManager.setVisualStyle(style, networkView);
                break;
            }
        }
    }
}