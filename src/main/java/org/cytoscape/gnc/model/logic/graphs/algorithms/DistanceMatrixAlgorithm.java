package org.cytoscape.gnc.model.logic.graphs.algorithms;

import org.cytoscape.gnc.model.businessobjects.IGRN;
import org.cytoscape.gnc.model.businessobjects.utils.ProgressMonitor;

/**
 *
 * @author Juan José Díaz Montaña
 */
public interface DistanceMatrixAlgorithm {
    int[][] getDistanceMatrix(IGRN grn) throws InterruptedException;
    int[][] getDistanceMatrix(IGRN grn, ProgressMonitor pm) throws InterruptedException;
}
