package org.cytoscape.gnc.model.businessobjects.utils;

import org.cytoscape.gnc.model.businessobjects.Node;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class Position
{
    private final int inputPosition;
    private final int dbPosition;
    private final Node node;
      
    public Position(Node node, int inputPosition, int dbPosition) {
        this.node = node;
        this.inputPosition = inputPosition;
        this.dbPosition = dbPosition;
    }
      
    public Node getNode() {
        return this.node;
    }
      
    public int getInputPosition() {
        return this.inputPosition;
    }
      
    public int getDBPosition() {
        return this.dbPosition;
    }
}