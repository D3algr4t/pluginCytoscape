package org.cytoscape.gnc.model.businessobjects;

import java.io.BufferedReader;
import java.io.FileNotFoundException;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class DBFile {
    private final String path;
    private final char delimiter;
    private final boolean preloaded;
    
    public DBFile(String path, char delimiter) {
        this(path, delimiter, false);
    }
    
    public DBFile(String path, char delimiter, boolean preloaded) {
        this.path = path;
        this.delimiter = delimiter;
        this.preloaded = preloaded;
    }
    
    public String getPath() {
        return path;
    }
    
    public char getDelimiter() {
        return delimiter;
    }
    
    public BufferedReader getBufferedReader() throws FileNotFoundException {
        return new BufferedReader(new java.io.InputStreamReader(preloaded
            ? getClass().getClassLoader().getResourceAsStream(path)
            : new java.io.FileInputStream(path)));
    }
}
