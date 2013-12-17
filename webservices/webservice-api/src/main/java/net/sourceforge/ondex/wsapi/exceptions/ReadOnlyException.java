/*
 * Thrown when the graph was found but was read only.
 * Will only be throw by a delete or create method.
 */
package net.sourceforge.ondex.wsapi.exceptions;

import org.apache.log4j.Logger;

/**
 *
 * @author Christian Brenninkmeijer
 */
public class ReadOnlyException extends WebserviceException {

    /**
     * Constructs an instance of <code>DeleteException</code> with the specified detail message.
     * @param msg the detail message.
     * @param logger The logger of the calling class.
     */
    public ReadOnlyException(String msg, Logger logger) {
        super(msg, logger);
    }

}
