/**
 * An Exception relating to webservices.
 * 
 * Most likely an incorrect input.
 *
 */

package net.sourceforge.ondex.wsapi.exceptions;

import net.sourceforge.ondex.core.ONDEXGraph;
import org.apache.log4j.Logger;

/**
 *
 * @author Christian Brenninkmeijer
 */
public class WebserviceException extends Exception {

    /**
     * Constructs an instance of <code>WebserviceException</code> with the specified detail message.
     * @param msg the detail message.
     * @param grpah The graph whihc caused the exception
     * @param logger The logger of the calling class.
     */
    public WebserviceException(String msg, ONDEXGraph graph, Logger logger) {
        super(msg+" with graph "+graph.getName());
        logger.error(msg+" with graph "+graph.getName());
    }
    
    /**
     * Constructs an instance of <code>WebserviceException</code> with the specified detail message.
     * @param msg the detail message.
     * @param logger The logger of the calling class.
     */
    public WebserviceException(String msg, Logger logger) {
        super(msg);
        logger.error(msg);
    }

    /**
     * Value to be used to represent an error.
     *
     * @return wenservice wide error code.
     */
    public static int getErrorValue(){
        return -1;
    }

}
