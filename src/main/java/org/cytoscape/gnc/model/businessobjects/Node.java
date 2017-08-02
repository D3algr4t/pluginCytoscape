package org.cytoscape.gnc.model.businessobjects;

import java.util.ArrayList;
import java.util.List;
import org.cytoscape.model.CyRow;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class Node {
    private final String name;
    private final CyRow cyRow;
    private final List<Edge> edges = new ArrayList();
    
    public Node(String name, CyRow cyRow) {
        this.name = name;
        this.cyRow = cyRow;
    }
    
    public List<Edge> getEdges() {
        return this.edges;
    }
    
    public void addEdge(Edge edge) {
        this.edges.add(edge);
    }
    
    public String getName() {
        return this.name;
    }
    
    public CyRow getCyRow() {
        return this.cyRow;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        
        Node other = (Node)obj;
        
        return this.name == null ? other.name == null : this.name.equalsIgnoreCase(other.name);
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }
}