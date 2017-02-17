package org.cytoscape.gnc.model.logic.graphs.algorithms;

import org.cytoscape.gnc.model.businessobjects.IGRN;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public interface DistanceMatrixAlgorithm {
    int[][] getDistanceMatrix(IGRN grn) throws InterruptedException;
}
