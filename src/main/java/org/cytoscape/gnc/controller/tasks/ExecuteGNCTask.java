package org.cytoscape.gnc.controller.tasks;

import org.cytoscape.gnc.controller.NetworkController;
import org.cytoscape.gnc.controller.ResultPanelController;
import org.cytoscape.gnc.controller.utils.CySwing;
import org.cytoscape.gnc.controller.utils.CytoscapeTaskMonitor;
import org.cytoscape.gnc.model.businessobjects.GNCResult;
import org.cytoscape.gnc.model.businessobjects.IGRN;
import org.cytoscape.gnc.model.businessobjects.utils.ProgressMonitor;
import org.cytoscape.gnc.model.logic.GNC;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class ExecuteGNCTask extends AbstractTask {
    private final IGRN db;
    
    public ExecuteGNCTask(IGRN db) {
        this.db = db;
    }
    
    @Override
    public void run(TaskMonitor tm) {
        NetworkController network;
        try {
            network = new NetworkController();
        } catch (InstantiationException ex) {
            CySwing.displayPopUpMessage(ex.getMessage());
            return;
        }
        if (db == null) {
            CySwing.displayPopUpMessage("No database selected.");
            return;
        }
        try {
            ProgressMonitor pm = new CytoscapeTaskMonitor(tm);
            GNC gnc = new GNC(pm);
            tm.setTitle("Executing GNC analysis");
            GNCResult result = gnc.execute(network.getGraph(), db);
            if (!gnc.isInterrupted()) {
                pm.setStatus("Displaying the  results.");
                new ResultPanelController(network, result);
                CySwing.displayPopUpMessage("GNC anlysis succesfully completed!");
            }
        } catch (Exception ex) {
            CySwing.displayPopUpMessage(ex.getMessage());
        }
    } 
    
    @Override
    public void cancel() {
//        gnc.interrupt();
        super.cancel();
        CySwing.displayPopUpMessage("GNC execution was cancelled");
    }
}