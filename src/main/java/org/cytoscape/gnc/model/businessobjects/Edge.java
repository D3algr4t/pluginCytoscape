package org.cytoscape.gnc.model.businessobjects;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class Edge
{
    private final String source;
    private final String target;

    public Edge(String source, String target, int id)
    {
        this.source = source;
        this.target = target;
    }

    public Edge(String source, String target) {
        this.source = source;
        this.target = target;
    }

    public String getSource()
    {
        return this.target;
    }

    public String getTarget()
    {
        return this.source;
    }
}