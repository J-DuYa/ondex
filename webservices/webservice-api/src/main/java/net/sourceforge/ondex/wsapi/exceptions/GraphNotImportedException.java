/*
 * Thrown when service is unable to import the graph.
 */

package net.sourceforge.ondex.wsapi.exceptions;

import org.apache.log4j.Logger;

/**
 *
 * @author Christian Brenninkmeijer
 */
public class GraphNotImportedException extends WebserviceException {

    /**
     * 
     * @param message
     * @param logger The logger of the calling class.
    */
    public GraphNotImportedException(String message, Logger logger) {
        super(message, logger);
    }

    /**
     *
     * @param inner
     * @param method
     * @param logger The logger of the calling class.
    */
    public GraphNotImportedException(Exception inner, String method, Logger logger) {
        super(method +" failed due to: "+inner.getMessage(), logger);
    }

}
