package org.cytoscape.plugin;

import java.util.Properties;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.osgi.framework.BundleContext;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Manuel Gandul Pérez
 */
public class PluginCytoscape extends AbstractCyActivator {
    @Override
    public void start(BundleContext context) throws Exception {
        CyApplicationManager applicationManager = getService(context, CyApplicationManager.class);
        CySwingApplication swingApplication = getService(context, CySwingApplication.class);
        CyServiceRegistrar serviceRegistrar = getService(context, CyServiceRegistrar.class);
        
        CySwing.init(swingApplication, serviceRegistrar);
        
        MenuAction menuAction = new MenuAction(applicationManager);
        registerService(context, menuAction, CyAction.class, new Properties());
    }
}