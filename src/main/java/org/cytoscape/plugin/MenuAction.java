package org.cytoscape.plugin;

import java.awt.event.ActionEvent;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;


/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Manuel Gandul Pérez
 */
public class MenuAction extends AbstractCyAction {
    private final HolaMundoSwing dialog;
    
    public MenuAction(/*TaskManager taskManager, */CyApplicationManager applicationManager/*, VisualMappingManager visualMappingManager*/) {
        super("Hola Mundo");
        setPreferredMenu("Apps");
        dialog = new HolaMundoSwing(applicationManager);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        dialog.setVisible(true);
    }
}