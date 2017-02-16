package org.cytoscape.gnc.model.businessobjects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}