/*
 * Thrown when the callable job caused some kind of exception.
 */

package net.sourceforge.ondex.server.exceptions;

import net.sourceforge.ondex.wsapi.exceptions.WebserviceException;
import org.apache.log4j.Logger;

/**
 *
 * @author Christian Brenninkmeijer
 */
public class JobException extends WebserviceException {

    /**
     * Constructs an instance of <code>JobException</code>
     * with the specified detail message.
     * @param msg the detail message.
     * @param logger
     */
    public JobException(String msg, Logger logger) {
        super(msg, logger);
    }

   /**
     * Constructs an instance of <code>JobException</code>
     * based on the caught exception.
     * @param msg the detail message.
     * @param logger The logger of the calling class.
     */
    public JobException(String message, Exception e, Logger logger) {
        super(message + " caused: " +e.getClass().getName()+" "+e.getMessage(), logger);
        StackTraceElement[] trace = e.getStackTrace();
        for (StackTraceElement element: trace){
            logger.error(element.toString());
        }
    }
    /**
     * Constructs an instance of <code>JobException</code>
     * based on the caught exception.
     * @param msg the detail message.
     * @param logger The logger of the calling class.
     */
    public JobException(Exception e, Logger logger) {
        super(e.getClass().getName()+" "+e.getMessage(), logger);
        StackTraceElement[] trace = e.getStackTrace();
        StringBuffer stackTrace = new StringBuffer();
        for (StackTraceElement element: trace){
            logger.error(element.toString());
            stackTrace.append(element.toString());
        }
    }


}
