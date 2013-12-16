package net.sourceforge.ondex.restful.util;

import net.sourceforge.ondex.restful.resources.ONDEXEntryPoint;

import com.sun.grizzly.http.SelectorThread;

/**
 * Example shutdown hook class which stops end point.
 * 
 * @author taubertj
 * 
 */
public class MyShutdown extends Thread {

	SelectorThread threadSelector = null;

	/**
	 * Set end point to stop
	 * 
	 * @param threadSelector
	 *            Jersey SelectorThread
	 */
	public MyShutdown(SelectorThread threadSelector) {
		this.threadSelector = threadSelector;
	}

	/**
	 * Stop end point
	 */
	public void run() {
		if (threadSelector != null)
			threadSelector.stopEndpoint();
		System.out.println("Jersey stopped");
		// clean up all open indices
		for (Integer key : ONDEXEntryPoint.indicies.keySet()) {
			ONDEXEntryPoint.indicies.get(key).closeIndex();
			ONDEXEntryPoint.indicies.get(key).cleanup();
		}
	}
}
