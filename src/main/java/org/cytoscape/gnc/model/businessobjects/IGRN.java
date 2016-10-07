package org.cytoscape.gnc.model.businessobjects;

import java.util.List;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public interface IGRN
{
    public String getName();
    
    public Node[] getNodes();
    
    public Integer getNodeId(Node paramNode);
    
    public List<Edge> getEdges();
    
    public float getDensity();
    
    public int[][] getAdjMatrix();
}