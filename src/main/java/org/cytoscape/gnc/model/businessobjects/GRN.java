package org.cytoscape.gnc.model.businessobjects;

import org.cytoscape.gnc.model.businessobjects.utils.Properties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class GRN implements IGRN
{
    private final String name;
    private final List<Node> nodes;
    private final Map<Node, Integer> nodesMap = new HashMap<Node, Integer>();
    private final List<Edge> edges;

    public GRN(String name, List<Node> nodes, List<Edge> edges) {
        this.name = name;
        this.nodes = nodes;
        this.edges = edges;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public List<Node> getNodes()
    {
        return nodes;
    }
    
    @Override
    public Integer getNodeId(Node node)
    {
        if (nodesMap.isEmpty()) {
            int i = 0;
            for (Node node1 : nodes) {
                nodesMap.put(node1, i++);
            }
        }
        return nodesMap.get(node);
    }

    @Override
    public List<Edge> getEdges()
    {
        return edges;
    }
 
    @Override
    public float getDensity() {
        return 2 * (float) edges.size() / (nodes.size() * (nodes.size() - 1));
    }
    
    @Override
    public int[][] getAdjMatrix()
    {
        int[][] m = new int[nodes.size()][nodes.size()];

        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            List neighbours = new ArrayList();

            for (Edge edge : node.getEdges()) {
               neighbours.add(edge.getSource().equals(node.getName()) ? edge.getTarget() : edge.getSource()); 
            }

            for (int j = 0; j < nodes.size(); j++) {
                if (i == j) {
                    m[i][j] = 0;
                } else if (neighbours.contains((nodes.get(j)).getName())) {
                    m[i][j] = 1;
                } else {
                    m[i][j] = Properties.infinity;
                }
            }
        }

        return m;
    }
}