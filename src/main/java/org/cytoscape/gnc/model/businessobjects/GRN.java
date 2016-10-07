package org.cytoscape.gnc.model.businessobjects;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.cytoscape.gnc.model.businessobjects.utils.Properties;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class GRN implements IGRN {
    private final String name;
    private final Node[] nodes;
    private final Map<Node, Integer> nodesMap = new HashMap();
    private final List<Edge> edges;
    
    public GRN(String name, Node[] nodes, List<Edge> edges) {
        this.name = name;
        this.nodes = nodes;
        this.edges = edges;
    }

    @Override
    public String getName() {
        return name;
    }
 
    @Override
    public Node[] getNodes() {
        return nodes;
    }

    @Override
    public Integer getNodeId(Node node) {
        if (nodesMap.isEmpty()) {
            int i = 0;
            for (Node node1 : nodes) {
                nodesMap.put(node1, i++);
            }
        }
        return nodesMap.get(node);
    }

    @Override
    public List<Edge> getEdges() {
        return edges;
    }

    @Override  
    public float getDensity()
    {
        return 2.0F * (float)this.edges.size() / (this.nodes.length * (this.nodes.length - 1));
    }    

    @Override
    public int[][] getAdjMatrix()  {
        int[][] m = new int[nodes.length][nodes.length];
        
        for (int i = 0; i < nodes.length; i++) {
            Node node = nodes[i];
            Set neighbours = new HashSet();
            
            for (Edge edge : node.getEdges()) {
                neighbours.add(edge.getSource().equals(node.getName()) ? edge.getTarget() : edge.getSource());
            }
            
            m[i][i] = 0;
            for (int j = i + 1; j < nodes.length; j++) {
                if (neighbours.contains(nodes[j].getName())) {
                    m[i][j] = 1;
                    m[j][i] = 1;
                } else {
                    m[i][j] = Properties.infinity;
                    m[j][i] = Properties.infinity;
                }
            }
        }
        
        return m;
    }
}