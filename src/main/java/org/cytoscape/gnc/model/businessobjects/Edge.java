package org.cytoscape.gnc.model.businessobjects;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class Edge
{
    private final Node source;
    private final Node target;
    
    public Edge(Node source, Node target) {
        this.source = source;
        this.target = target;
    }
    
    public Node getSource() {
        return this.target;
    }
    
    public Node getTarget() {
        return this.source;
    }
}