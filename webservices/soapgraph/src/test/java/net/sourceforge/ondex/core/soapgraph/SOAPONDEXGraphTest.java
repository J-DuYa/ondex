package net.sourceforge.ondex.core.soapgraph;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.ondex.core.ONDEXGraph;
import net.sourceforge.ondex.core.test.AbstractONDEXGraphTest;
import net.sourceforge.ondex.webservice.client.WebserviceException_Exception;

import org.junit.After;

/**
 * Tests memory implementation of AbstractONDEXGraph.
 * 
 * @author taubertj
 * 
 */
public class SOAPONDEXGraphTest extends AbstractONDEXGraphTest implements
		ActionListener {

	List<ActionEvent> list;

	@After
	public void tearDown() throws Exception {
		assertTrue(list.isEmpty());
	}

	@Override
	protected void finalize() throws IOException {
		// not needed

	}

	@Override
	protected ONDEXGraph initialize(String name) {

		SOAPONDEXGraph graph;
		try {
			list = new ArrayList<ActionEvent>();
			graph = new SOAPONDEXGraph(
					new URL(
							"http://rpc466.cs.man.ac.uk:8080/ondex/services/ONDEXapiWS?wsdl"),
					name, false);
			graph.addActionListener(this);
			return graph;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (WebserviceException_Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		list.add(e);
	}
}
