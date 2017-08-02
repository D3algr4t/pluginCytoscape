package org.cytoscape.gnc.controller;

import java.util.HashMap;
import java.util.Map;
import org.cytoscape.gnc.controller.utils.CySwing;
import org.cytoscape.gnc.model.businessobjects.GNCResult;
import org.cytoscape.gnc.view.resultPanel.MainResultsView;
import org.cytoscape.model.CyNetwork;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class ResultPanelController {
    public static final Map<CyNetwork, ResultPanelController> panels = new HashMap();
    
    private final NetworkController network;
    private GNCResult result;
    private MainResultsView rv;
    
    @SuppressWarnings("LeakingThisInConstructor")
    public ResultPanelController(NetworkController network, GNCResult result) {
        this.network = network;
        this.result = result;
        
        network.addGNCInfo(result);
        rv = new MainResultsView(this);
        CySwing.addPanel(rv);
        
        if (panels.containsKey(network.getCyNetwork())) {
            CySwing.removePanel(panels.get(network.getCyNetwork()).rv, false);
        }
        
        panels.put(network.getCyNetwork(), this);
    }
    
    public void dispose() {
        panels.remove(network.getCyNetwork());
        CySwing.removePanel(rv, panels.isEmpty());
        rv = null;
        result = null;
    }
    
    public GNCResult getResult() {
        return result;
    }
}