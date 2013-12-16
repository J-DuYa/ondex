/*
 * Thrown when the Attribute Name could not be found..
 */
package net.sourceforge.ondex.wsapi.exceptions;

import net.sourceforge.ondex.core.ONDEXGraph;
import org.apache.log4j.Logger;

/**
 *
 * @author Christian Brenninkmeijer
 */
public class AttributeNameNotFoundException extends WebserviceException {

    /**
     * Constructs an instance of <code>AttributeNameNotFoundException</code> with the specified detail message.
     * @param msg The detail message.
     * @param graph The ONDEXGraph which caused the exception
     * @param logger The logger of the calling class.
     */
    public AttributeNameNotFoundException(String msg, ONDEXGraph graph, Logger logger) {
        super(msg, graph, logger);
    }
}
