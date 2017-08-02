package org.cytoscape.gnc.model.businessobjects;

import org.cytoscape.model.CyRow;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class Edge
{
    private final Node source;
    private final Node target;
    private final CyRow cyRow;
    
    public Edge(Node source, Node target, CyRow cyRow) {
        this.source = source;
        this.target = target;
        this.cyRow = cyRow;
    }
    
    public Node getSource() {
        return this.target;
    }
    
    public Node getTarget() {
        return this.source;
    }
    
    public CyRow getCyRow() {
        return this.cyRow;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        
        Edge other = (Edge)obj;
        
        return ((this.source == null ? other.source == null : this.source.equals(other.source))
                    && (this.target == null ? other.target == null : this.target.equals(other.target)))
                || ((this.source == null ? other.target == null : this.source.equals(other.target))
                    && (this.target == null ? other.source == null : this.target.equals(other.source)));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + this.source.hashCode();
        hash = 43 * hash + this.target.hashCode();
        return hash;
    }
}