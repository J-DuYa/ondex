/*
 * Thrown when the graph could not be exported.
 */

package net.sourceforge.ondex.wsapi.exceptions;

import org.apache.log4j.Logger;

/**
 *
 * @author Christian Brenninkmeijer
 */
public class ExportException extends WebserviceException {

    /**
     * Constructs an instance of <code>GraphNotFoundException</code> with the specified detail message.
     * @param msg the detail message.
     * @param logger The logger of the calling class.
     */
    public ExportException(String msg, Logger logger) {
        super(msg, logger);
    }

}
