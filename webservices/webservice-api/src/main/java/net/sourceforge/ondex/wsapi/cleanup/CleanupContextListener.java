package net.sourceforge.ondex.wsapi.cleanup;

import javax.servlet.ServletContextEvent;

import net.sourceforge.ondex.wsapi.WebServiceEngine;

/**
 * A context listener that does a database cleanup on servlet undeploy.
 *
 * @author David Withers
 * @athor Christian Brenninkmeijer
 */
public class CleanupContextListener implements javax.servlet.ServletContextListener {

	private static WebServiceEngine engine;
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		if (engine != null) {
			engine.cleanup();
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		// do nothing
	}

	/**
	 * Sets the engine.
	 *
	 * @param webServiceEngine the new value for engine
	 */
	public static void setOndexGraph(WebServiceEngine webServiceEngine) {
		engine = webServiceEngine;
	}

}
