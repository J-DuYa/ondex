/*
 * Thrown when the Arguements provided are in some way incorrect.
 *
 * This could be:
 *
 * Missing required arguement that has no default.
 *
 * Proving an arguement whose name is unexpected.
 *
 * Proving an arguement whose value is unexpected.
 *
 * Proving different size list of names and values.
 */
package net.sourceforge.ondex.wsapi.exceptions;

import net.sourceforge.ondex.ONDEXPluginArguments;
import net.sourceforge.ondex.ONDEXPlugin;
import net.sourceforge.ondex.args.ArgumentDefinition;
import org.apache.log4j.Logger;

/**
 *
 * @author Christian Brenninkmeijer
 */
public class IllegalArguementsException extends WebserviceException {

    /**
     * Constructs an instance of <code>AttributeNameNotFoundException</code> with the specified detail message.
     * @param msg The detail message.
     * @param graph The ONDEXGraph which caused the exception
     * @param logger The logger of the calling class.
     */
    public IllegalArguementsException(String msg, ONDEXPlugin plugin, Logger logger) {
        super(msg, logger);
        logArguements(plugin, logger);
    }

    public IllegalArguementsException(String msg, Exception e, ONDEXPlugin plugin, Logger logger) {
        super(msg + " caused: " +e.getClass().getName()+" "+e.getMessage(), logger);
        StackTraceElement[] trace = e.getStackTrace();
        for (StackTraceElement element: trace){
            logger.error(element.toString());
        }
        logArguements(plugin, logger);
    }

    private void logArguements (ONDEXPlugin plugin, Logger logger) {
        logger.info("required arguements are");
        ONDEXPluginArguments arguments = plugin.getArguments();
        ArgumentDefinition[] argumentDefinitions =
                plugin.getArgumentDefinitions();
        for (ArgumentDefinition argumentDefinition: argumentDefinitions) {
            StringBuffer info = new StringBuffer();
            info.append(argumentDefinition.getName());
            info.append(", ");
            info.append(argumentDefinition.getClassType());
            info.append(", ");
            info.append(argumentDefinition.getDefaultValue());
            if (argumentDefinition.isRequiredArgument()) {
                info.append(", Required");
            } else {
                info.append(", Optional");
            }
            if (argumentDefinition.isAllowedMultipleInstances()) {
                info.append(", Multiple");
            } else {
                info.append(", Single");
            }
            logger.info(info.toString());
        }
    }

}
