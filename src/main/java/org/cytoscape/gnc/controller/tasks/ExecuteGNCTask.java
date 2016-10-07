package org.cytoscape.gnc.controller.tasks;

import java.io.BufferedReader;
import org.cytoscape.gnc.controller.NetworkController;
import org.cytoscape.gnc.controller.ResultPanelController;
import org.cytoscape.gnc.controller.utils.CySwing;
import org.cytoscape.gnc.controller.utils.CytoscapeTaskMonitor;
import org.cytoscape.gnc.controller.utils.NetworkAdapter;
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
    private GNC gnc;
    private final String dbName;
    private final BufferedReader br;
    
    public ExecuteGNCTask(String dbName, BufferedReader br)
    {
        this.dbName = dbName;
        this.br = br;
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

        if (br == null) {
            CySwing.displayPopUpMessage("No database selected.");
            return;
        }

        try {
            ProgressMonitor pm = new CytoscapeTaskMonitor(tm);
            gnc = new GNC(pm);
            IGRN db = NetworkAdapter.FileToGRN(this.dbName, this.br);
            tm.setTitle("Executing GNC analysis");
            GNCResult result = gnc.execute(network.getGraph(), db);
            pm.setStatus("Displaying the results.");
            new ResultPanelController(network, result);
            CySwing.displayPopUpMessage("GNC anlysis succesfully completed!");
        } catch (Exception ex) {
            CySwing.displayPopUpMessage(ex.getMessage());
        }
    }
    
    @Override
    public void cancel() {
        if (gnc != null) {
            gnc.interrupt();
        }
        super.cancel();
    }
}