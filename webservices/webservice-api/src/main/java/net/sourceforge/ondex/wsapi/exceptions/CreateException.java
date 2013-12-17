/*
 * Thrown when graph was unable to create an item.
 . Graph will exist otherwise GraphNotFoundException will have been thrown.
 * Graph can write otherwise ReadOnlyException will have been called
 * All required items have been found otherwise ...NotFoundException called.
 */
package net.sourceforge.ondex.wsapi.exceptions;

import org.apache.log4j.Logger;

/**
 *
 * @author Christian Brenninkmeijer
 */
public class CreateException extends WebserviceException {

    /**
     * Constructs an instance of <code>CreateException</code> with the specified detail message.
     * @param msg the detail message.
     * @param logger The logger of the calling class.
     */
    public CreateException(String msg, Logger logger) {
        super(msg, logger);
    }

}
