package org.cytoscape.gnc.controller.utils;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cytoscape.gnc.model.businessobjects.Edge;
import org.cytoscape.gnc.model.businessobjects.GRN;
import org.cytoscape.gnc.model.businessobjects.IGRN;
import org.cytoscape.gnc.model.businessobjects.Node;
import org.cytoscape.gnc.model.businessobjects.exceptions.DatabaseParsingException;
import org.cytoscape.gnc.model.businessobjects.utils.Cache;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class NetworkAdapter {
    public static IGRN CyNetworkToGRN(CyNetwork network) {
        List<CyNode> cyNodes = network.getNodeList();
        List<CyEdge> cyEdges = network.getEdgeList();
        if (cyNodes.isEmpty() || cyEdges.isEmpty()) {
            throw new IllegalArgumentException("The current network view seems to be empty.");
        }
        
        String name = network.getRow(network).get(CyNetwork.NAME, String.class);
        Map<String, Node> nodes = new HashMap<String, Node>(cyNodes.size());
        List<Edge> edges = new ArrayList<Edge>(cyEdges.size());
        
        for (CyNode cyNode : cyNodes) {
            String nodeName = network.getRow(cyNode).get(CyNetwork.NAME, String.class);
            nodes.put(nodeName, new Node(nodeName));
        }
        for (CyEdge cyEdge : cyEdges) {
            String sourceName = network.getRow(cyEdge.getSource()).get(CyNetwork.NAME, String.class);
            String targetName = network.getRow(cyEdge.getTarget()).get(CyNetwork.NAME, String.class);
            Edge edge = new Edge(sourceName, targetName);
            edges.add(edge);
            nodes.get(sourceName).addEdge(edge);
            nodes.get(targetName).addEdge(edge);
        }
        
        return new GRN(name, (Node[])nodes.values().toArray(new Node[nodes.size()]), edges);
    }
    
    public static IGRN FileToGRN(String file, BufferedReader br) {
        Cache<Node> nodesCache = new Cache();
        List<Edge> edges = new ArrayList();
        
        try {
            String line;
            while ((line = br.readLine()) != null) {
                String[] nodeNames = line.split(",");
                if (nodeNames.length != 2) {
                    continue;
                }

                Node node1 = nodesCache.getOrAdd(new Node(nodeNames[0]));
                Node node2 = nodesCache.getOrAdd(new Node(nodeNames[1]));
                Edge edge = new Edge(node1.getName(), node2.getName());
                edges.add(edge);
                node1.addEdge(edge);
                node2.addEdge(edge);
            }
        } catch (Exception e) {
            throw new DatabaseParsingException(e);
        }
        
        return new GRN(file, nodesCache.getAll().toArray(new Node[nodesCache.size()]), edges);
    }
}