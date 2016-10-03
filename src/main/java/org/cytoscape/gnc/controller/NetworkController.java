package org.cytoscape.gnc.controller;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.gnc.controller.utils.NetworkAdapter;
import org.cytoscape.gnc.model.businessobjects.IGRN;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public final class NetworkController {
    private static CyApplicationManager applicationManager;

    private final CyNetwork network;
    private final CyNetworkView networkView;
    
    public static void init(CyApplicationManager applicationManager) {
        NetworkController.applicationManager = applicationManager;
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
}