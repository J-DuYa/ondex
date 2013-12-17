/*
 * Thrown when the DataSource could not be found..
 */

package net.sourceforge.ondex.wsapi.exceptions;

import net.sourceforge.ondex.core.ONDEXGraph;
import org.apache.log4j.Logger;

/**
 *
 * @author Christian Brenninkmeijer
 */
public class DataSourceNotFoundException extends WebserviceException {

    /**
     * Constructs an instance of <code>DataSourceNotFoundException</code> with the specified detail message.
     * @param msg the detail message.
     * @param logger The logger of the calling class.
     */
    public DataSourceNotFoundException(String msg, Logger logger) {
        super(msg, logger);
    }

   /**
     * Constructs an instance of <code>DataSourceNotFoundException</code>
     *  with the specified detail message.
     * @param msg The detail message.
     * @param graph The ONDEXGraph which caused the exception
     * @param logger The logger of the calling class.
     */
     public DataSourceNotFoundException(String msg, ONDEXGraph graph, Logger logger) {
        super(msg, graph, logger);
    }
}
