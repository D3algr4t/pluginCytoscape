package org.cytoscape.gnc;

import java.util.Properties;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.gnc.controller.NetworkController;
import org.cytoscape.gnc.controller.actions.MenuAction;
import org.cytoscape.gnc.controller.listener.NetworkClosedListener;
import org.cytoscape.gnc.controller.utils.CySwing;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskManager;
import org.osgi.framework.BundleContext;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class GNC extends AbstractCyActivator {
    @Override
    public void start(BundleContext context) throws Exception {
        CyApplicationManager applicationManager = getService(context, CyApplicationManager.class);
        CySwingApplication swingApplication = getService(context, CySwingApplication.class);
        CyServiceRegistrar serviceRegistrar = getService(context, CyServiceRegistrar.class);
        TaskManager taskManager = getService(context, TaskManager.class);
        
        CySwing.init(swingApplication, serviceRegistrar); 
        NetworkController.init(applicationManager);

        // UI controls
        MenuAction menuAction = new MenuAction(taskManager);
        registerService(context, menuAction, CyAction.class, new Properties());
        
        serviceRegistrar.registerService(new NetworkClosedListener(), NetworkAboutToBeDestroyedListener.class, new Properties());
    }
}