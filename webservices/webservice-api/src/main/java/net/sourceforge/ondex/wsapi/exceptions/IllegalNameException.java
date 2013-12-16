/*
 * Thrown when null MetaData returned by the graph.
 */
package net.sourceforge.ondex.wsapi.exceptions;

import org.apache.log4j.Logger;

/**
 *
 * @author Christian Brenninkmeijer
 */
public class IllegalNameException extends WebserviceException {

    /**
     * Constructs an instance of <code>GraphNotFoundException</code> with the specified detail message.
     * @param msg the detail message.
     * @param logger The logger of the calling class.
     */
    public IllegalNameException(String msg, Logger logger) {
        super(msg, logger);
    }

}
