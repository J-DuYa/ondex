/*
 * Thrown when an unexpected exception is caught.
 * Should not be used to catch exceptions caused by incorrect user input
 */
package net.sourceforge.ondex.wsapi.exceptions;

import net.sourceforge.ondex.wsapi.plugins.BufferedOndexListener;
import org.apache.log4j.Logger;

/**
 *
 * @author Christian Brenninkmeijer
 */
public class CaughtException extends WebserviceException {

    String localMessage;

    /**
     * Constructs an instance of <code>CaughtException</code>
     * based on the caught exception.
     * @param msg the detail localMessage.
     * @param logger The logger of the calling class.
     */
    public CaughtException(Exception e, Logger logger) {
        super("Exception Caught by ONDEX WS " + e.getClass().getName()+" "+e.getMessage(), logger);
        StackTraceElement[] trace = e.getStackTrace();
        StringBuilder stackTrace = new StringBuilder();
        for (StackTraceElement element: trace){
            logger.error(element.toString());
            stackTrace.append(" " + element.toString());
        }
        localMessage = "Exception Caught by ONDEX WS " + e.getClass().getName() + " " + e.getMessage() + stackTrace.toString();
    }

    /**
     * Constructs an instance of <code>CaughtException</code>
     * based on the caught exception.
     * @param msg the detail localMessage.
     * @param logger The logger of the calling class.
     */
    public CaughtException(String newMessage, Exception e, Logger logger) {
        super(newMessage + " caused: " +e.getClass().getName()+" "+e.getMessage(), logger);
        StackTraceElement[] trace = e.getStackTrace();
        StringBuilder stackTrace = new StringBuilder();
        for (StackTraceElement element: trace){
            logger.error(element.toString());
            stackTrace.append(" " + element.toString());
        }
        localMessage = newMessage + " caused: " +e.getClass().getName()+" "+e.getMessage() + stackTrace.toString();
    }

    /**
     * Constructs an instance of <code>CaughtException</code>
     * based on the caught exception.
     * @param msg the detail localMessage.
     * @param listener The Ondex listener to add localMessage to
     * @param logger The logger of the calling class.
     */
    public CaughtException(Exception e, BufferedOndexListener listener, Logger logger) {
        super(e.getClass().getName()+" "+e.getMessage()+" "+listener.getCompleteEventHistory(), logger);
        StackTraceElement[] trace = e.getStackTrace();
        StringBuilder stackTrace = new StringBuilder();
        for (StackTraceElement element: trace){
            logger.error(element.toString());
            stackTrace.append(" " + element.toString());
        }
        logger.error(listener.getCompleteEventHistory());
        localMessage = "Exception Caught by ONDEX WS " +e.getClass().getName()+" "+e.getMessage() + stackTrace.toString() + " "+listener.getCompleteEventHistory();
    }

    /**
     * Constructs an instance of <code>CaughtException</code>
     * based on the caught exception.
     * @param msg the detail localMessage.
     * @param e The exception caught
     * @param listener The Ondex listener to add localMessage to
     * @param logger The logger of the calling class.
     */
    public CaughtException(String newMessage, Exception e, BufferedOndexListener listener, Logger logger) {
        super(newMessage + " caused: " +e.getClass().getName()+" "+e.getMessage()+" "+listener.getCompleteEventHistory(), logger);
        StackTraceElement[] trace = e.getStackTrace();
        StringBuilder stackTrace = new StringBuilder();
        logger.error(listener.getCompleteEventHistory());
        for (StackTraceElement element: trace){
            logger.error(element.toString());
            stackTrace.append(" " + element.toString());
        }
        logger.error(listener.getCompleteEventHistory());
        localMessage = newMessage + " caused: " +e.getClass().getName()+" "+e.getMessage() + stackTrace.toString() + " "+listener.getCompleteEventHistory();
    }

    @Override
    public String getMessage(){
       return localMessage; 
    }

}
