package org.cytoscape.gnc.controller.tasks;

import org.cytoscape.gnc.controller.NetworkController;
import org.cytoscape.gnc.controller.ResultPanelController;
import org.cytoscape.gnc.controller.utils.CySwing;
import org.cytoscape.gnc.controller.utils.CytoscapeTaskMonitor;
import org.cytoscape.gnc.controller.utils.NetworkAdapter;
import org.cytoscape.gnc.model.businessobjects.DBFile;
import org.cytoscape.gnc.model.businessobjects.GNCResult;
import org.cytoscape.gnc.model.businessobjects.IGRN;
import org.cytoscape.gnc.model.businessobjects.exceptions.DatabaseParsingException;
import org.cytoscape.gnc.model.businessobjects.utils.ProgressMonitor;
import org.cytoscape.gnc.model.logic.GNC;
import org.cytoscape.gnc.view.configurationDialogs.ConfigurationDialog;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class ExecuteGNCTask extends AbstractTask {
    private GNC gnc;
    private final DBFile dbFile;
    private final ConfigurationDialog configurationDialog;
    
    public ExecuteGNCTask(DBFile dbFile, ConfigurationDialog configurationDialog)
    {
        this.dbFile = dbFile;
        this.configurationDialog = configurationDialog;
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
        
        if (dbFile == null) {
            CySwing.displayPopUpMessage("No database selected.");
            return;
        }

        try {
            ProgressMonitor pm = new CytoscapeTaskMonitor(tm);
            gnc = new GNC(pm);
            tm.setTitle("Loading biological database");
            IGRN db = NetworkAdapter.FileToGRN(dbFile);
            tm.setTitle("Executing GNC analysis");
            GNCResult result = gnc.execute(network.getGraph(), db);
            pm.setStatus("Displaying the results.");
            new ResultPanelController(network, result);
            CySwing.displayPopUpMessage("GNC anlysis succesfully completed!");
        } catch (DatabaseParsingException ex){
            configurationDialog.removeDB(dbFile);
            CySwing.displayPopUpMessage(ex.getMessage());
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