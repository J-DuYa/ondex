/*
 * Thrown when the graph is not found in the registry.
 */

package net.sourceforge.ondex.wsapi.exceptions;

import org.apache.log4j.Logger;

/**
 *
 * @author Christian Brenninkmeijer
 */
public class AttributeNotFoundException extends WebserviceException {

    /**
     * Constructs an instance of <code>AttributeNotFoundException</code> with the specified detail message.
     * @param msg the detail message.
     * @param logger The logger of the calling class.
     */
    public AttributeNotFoundException(String msg, Logger logger) {
        super(msg, logger);
    }

}
