/*
 * Thrown when the graph is not found in the registry.
 */

package net.sourceforge.ondex.wsapi.exceptions;

import org.apache.log4j.Logger;

/**
 *
 * @author Christian Brenninkmeijer
 */
public class GraphNotFoundException extends WebserviceException {

    /**
     * Constructs an instance of <code>GraphNotFoundException</code> with the specified detail message.
     * @param msg the detail message.
     * @param logger The logger of the calling class.
     */
    public GraphNotFoundException(String msg, Logger logger) {
        super(msg, logger);
    }
}
