package org.cytoscape.gnc.model.businessobjects;

import java.util.ArrayList;
import java.util.List;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class Node {
    private final String name;
    private final List<Edge> edges = new ArrayList();
    
    public Node(String name)
    {
        this.name = name;
    }
    
    public List<Edge> getEdges()
    {
        return this.edges;
    }
    
    public void addEdge(Edge edge) {
        this.edges.add(edge);
    }
    
    public String getName() {
        return this.name;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        
        Node other = (Node)obj;
        
        return this.name == null ? other.name == null : this.name.equalsIgnoreCase(other.name);
    }
    
    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 59 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }
}