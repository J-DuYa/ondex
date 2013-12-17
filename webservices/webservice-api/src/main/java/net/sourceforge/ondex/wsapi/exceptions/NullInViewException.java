/*
 * Thrown when one of the mthods that returns a view returns a null instead..
 */

package net.sourceforge.ondex.wsapi.exceptions;

import org.apache.log4j.Logger;

/**
 *
 * @author Christian Brenninkmeijer
 */
public class NullInViewException extends WebserviceException {

    /**
     * Constructs an instance of <code>GraphNotFoundException</code> with the specified detail message.
     * @param msg the detail message.
     * @param logger The logger of the calling class.
     */
    public NullInViewException(String msg, Logger logger) {
        super(msg, logger);
    }

}
