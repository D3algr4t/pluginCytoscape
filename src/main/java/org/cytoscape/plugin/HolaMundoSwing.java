package org.cytoscape.plugin;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;

import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Random;
import java.awt.event.ActionEvent;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Manuel Gandul Pérez
 */
public class HolaMundoSwing extends JDialog {
	
	private final CyApplicationManager applicationManager;

	private CyNetwork network;
	private CyNetworkView networkView;
	
	private final JPanel contentPanel = new JPanel();

	public HolaMundoSwing(CyApplicationManager applicationManager) {		
		super(CySwing.getDesktopJFrame(), true);
		
		this.applicationManager = applicationManager;
		
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			JLabel lblHolaMundo = new JLabel("Hola Mundo");
			contentPanel.add(lblHolaMundo);
		}
		{
			JButton btnModifica = new JButton("modifica");
			btnModifica.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					actionButon();
				}
			});
			contentPanel.add(btnModifica);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
		}
		
		setLocationRelativeTo(CySwing.getDesktopJFrame());
	}
	
	private void actionButon() {   
		networkView = applicationManager.getCurrentNetworkView();
		if (networkView != null) {
			network = networkView.getModel();
		} else {
			network = applicationManager.getCurrentNetwork();
		}
		
		if(networkView != null) {
			List<CyNode> listaNodos = network.getNodeList();
	    	int i, y;   	
	    	
	    	Random aleatorio = new Random(System.currentTimeMillis());
	    	
	    	i = aleatorio.nextInt(listaNodos.size() + 1);
	    	y = aleatorio.nextInt(listaNodos.size() + 1);    	
	    	
	    	network.addEdge(listaNodos.get(i), listaNodos.get(y), true);
	        networkView.updateView();
		}		
	}

}
